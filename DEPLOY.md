# Tujulishane-Hub Deployment Guide

Complete deployment reference for the Tujulishane-Hub application on a VPS (Ubuntu/Debian) with nginx reverse proxy.

## Architecture Overview

```
Browser → DNS → VPS (nginx :443) → frontend static files
                                  → proxy /api/ → Spring Boot (:8080) → PostgreSQL
```

- **Frontend**: Vanilla HTML/JS/CSS (no build step) served by nginx
- **Backend**: Spring Boot 3.3.0 (Java 21) running on port 8080
- **Database**: PostgreSQL
- **Reverse Proxy**: nginx with SSL termination

---

## 1. DNS Setup

### The Problem with Truehost (or similar hosting providers)

If your domain was previously on shared hosting with SSL, the registrar may proxy traffic through their own servers. Even if the A record looks correct in the panel, the authoritative nameservers may return a proxy IP instead of your VPS IP.

### How to Diagnose

```bash
# Check what IP the world sees
dig lomogan.africa +short

# Check what the authoritative NS returns
dig @ns1.cloudoon.com lomogan.africa A +short

# Compare with your actual VPS IP
curl -s ifconfig.me
```

If `dig` returns a different IP than your VPS, the registrar is proxying.

### Fix: Use Cloudflare DNS (Free)

1. Sign up at [cloudflare.com](https://cloudflare.com) (free plan)
2. Add your domain
3. Set DNS records:
   - `@ A <YOUR_VPS_IP>` — **DNS only** (grey cloud, NOT proxied)
   - `www A <YOUR_VPS_IP>` — **DNS only**
   - Copy over MX and TXT records for email
4. In your registrar (Truehost), change nameservers to Cloudflare's
5. Wait for propagation (10 min to a few hours)

### Verify

```bash
dig lomogan.africa +short
# Should return your VPS IP: 156.232.88.204
```

### /etc/hosts Warning

If you have `127.0.0.1 lomogan.africa` in `/etc/hosts` on the VPS, **curl from the VPS will always work** even if DNS is broken. This masks real problems. Remove it after DNS is properly configured:

```bash
sed -i '/lomogan.africa/d' /etc/hosts
```

---

## 2. VPS Prerequisites

```bash
# Java 21
apt update && apt install -y openjdk-21-jdk

# PostgreSQL
apt install -y postgresql postgresql-contrib

# nginx
apt install -y nginx

# Certbot (SSL)
apt install -y certbot python3-certbot-nginx

# Git
apt install -y git
```

---

## 3. PostgreSQL Setup

```bash
sudo -u postgres psql

CREATE USER tujulishane WITH PASSWORD 'your_secure_password';
CREATE DATABASE tujulishane_hub OWNER tujulishane;
GRANT ALL PRIVILEGES ON DATABASE tujulishane_hub TO tujulishane;
\q
```

---

## 4. Backend Deployment

### 4.1 Build the JAR (on your dev machine or VPS)

```bash
cd backend
./gradlew clean bootJar -x test
# Output: build/libs/backend-0.0.1-SNAPSHOT.jar
```

### 4.2 Deploy to VPS

```bash
# Copy JAR to VPS
scp build/libs/*.jar root@YOUR_VPS:/root/lomogan_projects/Tujulishane-Hub/backend/build/libs/

# Or pull from git and build on VPS
cd /root/lomogan_projects/Tujulishane-Hub/backend
git pull
./gradlew clean bootJar -x test
```

### 4.3 Create systemd Service

Create `/etc/systemd/system/tujulishane-backend.service`:

```ini
[Unit]
Description=Tujulishane Hub Backend
After=network.target postgresql.service

[Service]
Type=simple
User=root
WorkingDirectory=/root/lomogan_projects/Tujulishane-Hub/backend
ExecStart=/usr/bin/java \
    -Dserver.port=8080 \
    -Dspring.profiles.active=prod \
    -jar build/libs/backend-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

# Environment variables
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tujulishane_hub
Environment=SPRING_DATASOURCE_USERNAME=tujulishane
Environment=SPRING_DATASOURCE_PASSWORD=your_secure_password
Environment=JWT_SECRET=your_very_long_secret_key_at_least_512_bits
Environment=JWT_EXPIRATION=3600
Environment=SPRING_MAIL_HOST=smtp.gmail.com
Environment=SPRING_MAIL_PORT=587
Environment=SPRING_MAIL_USERNAME=your_email@gmail.com
Environment=SPRING_MAIL_PASSWORD=your_app_password
Environment=SPRING_MAIL_FROM=noreply@tujulishanehub.com
Environment=GEOCODING_API_KEY=your_geocoding_key

[Install]
WantedBy=multi-user.target
```

```bash
systemctl daemon-reload
systemctl enable tujulishane-backend
systemctl start tujulishane-backend

# Check status
systemctl status tujulishane-backend
journalctl -u tujulishane-backend -f
```

### 4.4 Docker Alternative

```bash
cd /root/lomogan_projects/Tujulishane-Hub/backend

# Build
./gradlew clean bootJar -x test
docker compose build

# Run
export DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/tujulishane_hub
export JWT_SECRET=your_secret_key
export MAIL_USERNAME=your_email
export MAIL_PASSWORD=your_password
docker compose up -d

# Logs
docker logs -f tujulishane-backend
```

### 4.5 Verify Backend is Running

```bash
curl http://localhost:8080/api/projects
# Should return JSON (empty array or project list)
```

---

## 5. Frontend Deployment

The frontend has **no build step**. Just serve the files with nginx.

### 5.1 Frontend Config

**IMPORTANT**: `frontend/config.js` must have `USE_PROD = true` on the VPS:

```bash
# On VPS — set production mode
sed -i 's/const USE_PROD = false;/const USE_PROD = true;/' \
    /root/lomogan_projects/Tujulishane-Hub/frontend/config.js
```

This switches API calls from `http://localhost:8080` to `/tujulishane-hub` (relative path proxied by nginx).

### 5.2 After Every Git Pull

Always re-check `config.js` after pulling changes — local development may have flipped `USE_PROD` back to `false`:

```bash
grep "USE_PROD" /root/lomogan_projects/Tujulishane-Hub/frontend/config.js
# Should show: const USE_PROD = true;
```

---

## 6. Nginx Configuration

### 6.1 The Config

In `/etc/nginx/sites-available/lomogan.africa`:

```nginx
server {
    listen 80;
    server_name lomogan.africa www.lomogan.africa;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name lomogan.africa www.lomogan.africa;

    ssl_certificate     /etc/letsencrypt/live/lomogan.africa/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/lomogan.africa/privkey.pem;

    # ... other locations ...

    # Redirect /tujulishane-hub (no slash) to /tujulishane-hub/
    location = /tujulishane-hub {
        return 301 /tujulishane-hub/;
    }

    # Serve frontend static files
    location /tujulishane-hub/ {
        alias /root/lomogan_projects/Tujulishane-Hub/frontend/;
        index index.html;
    }

    # Proxy API requests to Spring Boot
    location /tujulishane-hub/api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 6.2 Key Nginx Rules

**DO NOT use `try_files` with `alias`**. This is a well-known nginx bug where `try_files` resolves paths using `root` instead of `alias`, causing 404s. Keep it simple:

```nginx
# GOOD - works reliably
location /tujulishane-hub/ {
    alias /root/lomogan_projects/Tujulishane-Hub/frontend/;
    index index.html;
}

# BAD - broken with alias
location /tujulishane-hub/ {
    alias /root/lomogan_projects/Tujulishane-Hub/frontend/;
    try_files $uri $uri/ /index.html;  # WILL BREAK
}
```

If you need SPA-style routing (all paths serve index.html), use `error_page` instead:

```nginx
location /tujulishane-hub/ {
    alias /root/lomogan_projects/Tujulishane-Hub/frontend/;
    index index.html;
    error_page 404 = /tujulishane-hub/index.html;
}
```

### 6.3 Permissions

Nginx runs as `www-data`. It needs **execute permission** on every directory in the path to traverse it:

```bash
chmod o+x /root
chmod o+x /root/lomogan_projects
chmod o+x /root/lomogan_projects/Tujulishane-Hub
chmod o+x /root/lomogan_projects/Tujulishane-Hub/frontend
```

Verify:

```bash
sudo -u www-data ls /root/lomogan_projects/Tujulishane-Hub/frontend/
# Should list files without "Permission denied"
```

Note: `sudo -u www-data ls /root/` may say "Permission denied" — that's fine. Nginx only needs **traverse** (execute) permission, not **list** (read) permission on parent directories.

### 6.4 Apply and Reload

```bash
# Symlink if not already done
ln -sf /etc/nginx/sites-available/lomogan.africa /etc/nginx/sites-enabled/

# Test config
nginx -t

# Reload
systemctl reload nginx
```

---

## 7. SSL with Let's Encrypt

```bash
# First-time setup
certbot --nginx -d lomogan.africa -d www.lomogan.africa

# Auto-renewal (certbot sets up a timer automatically)
systemctl status certbot.timer

# Manual renewal test
certbot renew --dry-run
```

---

## 8. Troubleshooting Checklist

### "404 Not Found" Flowchart

```
Is the 404 from YOUR nginx?
├── Check: curl -sI https://lomogan.africa/tujulishane-hub/ | grep server
│   ├── "server: nginx" → It's your server, check nginx config
│   └── No "server" header or different server → DNS problem (see Section 1)
│
Is the backend reachable?
├── curl http://localhost:8080/api/projects
│   ├── Connection refused → Backend not running
│   │   └── systemctl status tujulishane-backend
│   │   └── journalctl -u tujulishane-backend -f
│   └── Returns JSON → Backend is fine, check nginx proxy config
│
Is nginx serving the frontend?
├── curl -Ik https://localhost/tujulishane-hub/
│   ├── 200 → nginx is fine, check DNS
│   ├── 403 → Permission issue (see Section 6.3)
│   └── 404 → Check alias path and config
```

### Quick Diagnostic Commands

```bash
# DNS: What IP does the world see?
dig lomogan.africa +short

# Is it YOUR server responding?
curl -sI https://lomogan.africa/ | grep server

# Backend health
curl http://localhost:8080/api/projects

# Nginx config check
nginx -t

# Full nginx config dump
nginx -T

# Nginx error log
tail -20 /var/log/nginx/error.log

# Backend logs
journalctl -u tujulishane-backend --since "10 minutes ago"

# Frontend config check
grep "USE_PROD" /root/lomogan_projects/Tujulishane-Hub/frontend/config.js

# Test as nginx user
sudo -u www-data ls /root/lomogan_projects/Tujulishane-Hub/frontend/

# What's listening on port 8080?
ss -tlnp | grep 8080
```

### Common Gotchas

| Symptom | Cause | Fix |
|---------|-------|-----|
| 404 from browser but curl from VPS works | `/etc/hosts` override on VPS masks DNS issue | Fix DNS, remove `/etc/hosts` entry |
| 404 with no nginx error log entry | Request hitting a different server entirely | Check `dig` output, fix DNS |
| 403 Forbidden | nginx can't traverse `/root/` directory | `chmod o+x /root` |
| API calls go to `localhost:8080` in browser | `config.js` has `USE_PROD = false` | Set to `true` on VPS |
| `try_files` returns 404 with `alias` | Known nginx bug | Remove `try_files`, use `error_page` instead |
| Backend starts then crashes | Missing env vars or DB connection | Check `journalctl` for stack trace |
| Flyway migration error | Schema conflict with existing data | Check migration SQL files in `db/migration/` |

---

## 9. Deployment Workflow (Updating the App)

```bash
# On VPS
cd /root/lomogan_projects/Tujulishane-Hub

# Pull latest code
git pull origin main

# Re-set frontend to production mode (git pull may overwrite this)
sed -i 's/const USE_PROD = false;/const USE_PROD = true;/' frontend/config.js

# Rebuild backend
cd backend
./gradlew clean bootJar -x test

# Restart backend
systemctl restart tujulishane-backend

# Reload nginx (only if nginx config changed)
nginx -t && systemctl reload nginx

# Verify
curl http://localhost:8080/api/projects
curl -Ik https://localhost/tujulishane-hub/
```

---

## 10. Environment Variables Reference

| Variable | Required | Example | Notes |
|----------|----------|---------|-------|
| `SPRING_PROFILES_ACTIVE` | Yes | `prod` | Must be `prod` on VPS |
| `SPRING_DATASOURCE_URL` | Yes | `jdbc:postgresql://localhost:5432/tujulishane_hub` | |
| `SPRING_DATASOURCE_USERNAME` | Yes | `tujulishane` | |
| `SPRING_DATASOURCE_PASSWORD` | Yes | `secure_password` | |
| `JWT_SECRET` | Yes | `(64+ random chars)` | Min 512-bit for HS512 |
| `JWT_EXPIRATION` | No | `3600` | Seconds, default 1 hour |
| `SPRING_MAIL_HOST` | For email | `smtp.gmail.com` | |
| `SPRING_MAIL_PORT` | For email | `587` | |
| `SPRING_MAIL_USERNAME` | For email | `you@gmail.com` | |
| `SPRING_MAIL_PASSWORD` | For email | `app_password` | Gmail app password, not account password |
| `SPRING_MAIL_FROM` | For email | `noreply@tujulishanehub.com` | |
| `GEOCODING_API_KEY` | For maps | `your_key` | |
| `BOOTSTRAP_SECRET` | One-time | `random_string` | For initial super-admin creation |

---

## 11. Useful Paths on VPS

```
/root/lomogan_projects/Tujulishane-Hub/          # Project root
/root/lomogan_projects/Tujulishane-Hub/frontend/  # Static files (served by nginx)
/root/lomogan_projects/Tujulishane-Hub/backend/   # Spring Boot app

/etc/nginx/sites-available/lomogan.africa         # Nginx site config
/etc/nginx/sites-enabled/                         # Symlinks to active sites
/var/log/nginx/error.log                          # Nginx errors
/var/log/nginx/access.log                         # Nginx access log

/etc/systemd/system/tujulishane-backend.service   # Backend service file
/etc/letsencrypt/live/lomogan.africa/             # SSL certificates
```

---

## 12. First-Time Bootstrap (Creating Super Admin)

After the backend is running for the first time:

```bash
curl -X POST http://localhost:8080/api/auth/bootstrap/super-admin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@your-domain.com",
    "name": "System Administrator",
    "secretKey": "YOUR_BOOTSTRAP_SECRET"
  }'
```

This endpoint only works once. Change the `BOOTSTRAP_SECRET` env var afterward.
