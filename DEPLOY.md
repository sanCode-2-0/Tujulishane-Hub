1. Frontend Deploy Only (Takes ~2 seconds) Since the frontend has no compilation/build step, this will copy only the static files and apply them to Nginx.

```powershell
.\deploy.ps1
```

2. Full Deploy (Builds Java, uploads JAR, and restarts EC2 service) If you have changed backend Java code and need a full build

```powershell
.\deploy.ps1 -Backend
```

3. Only Restart Backend If you want to trigger a service restart on the EC2 without building or copying anything

```powershell
.\deploy.ps1 -Restart
```
