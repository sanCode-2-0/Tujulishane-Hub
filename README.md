# Tujulishane Hub API

Backend API for managing development projects in Kenya with automatic geocoding.

## 🚀 Live API
```
https://api-tujulishane-hub.onrender.com
```

## 📋 Project Endpoints

### Get Projects
```bash
GET /api/projects
GET /api/projects/{id}
GET /api/projects/search?query=water
GET /api/projects/status/active
GET /api/projects/county/Nairobi
```

### Create Project
```bash
POST /api/projects
Content-Type: application/json

{
  "partner": "USAID Kenya",
  "title": "Water Access Project", 
  "maps_address": "Nairobi, Kenya"
}
```

### Update/Delete
```bash
PUT /api/projects/{id}
DELETE /api/projects/{id}
```

## 🌍 Geographic Endpoints

```bash
GET /api/projects/with-coordinates
GET /api/projects/without-coordinates
GET /api/projects/coordinates?minLat=-1.5&maxLat=-1.0&minLng=36.5&maxLng=37.0
POST /api/projects/batch-geocode
```

## 📊 Statistics

```bash
GET /api/projects/statistics
```

Response:
```json
{
  "totalProjects": 25,
  "statusCounts": [["active", 10], ["pending", 8]],
  "countyCounts": [["Nairobi", 8], ["Mombasa", 5]],
  "coordinatesCoveragePercentage": 85.5
}
```

## 👤 Authentication

```bash
# Register
POST /api/auth/register
{"name": "John Doe", "email": "john@example.com"}

# Verify OTP
POST /api/auth/verify-otp  
{"email": "john@example.com", "otp": "123456"}

# Login
POST /api/auth/login
{"email": "john@example.com"}
```

## 🔧 Project Status Values
- `pending` - Awaiting approval
- `active` - Currently running  
- `completed` - Finished
- `stalled` - Temporarily halted
- `abandoned` - Cancelled

## 🧪 Quick Test

```bash
# Get all projects
curl "https://api-tujulishane-hub.onrender.com/api/projects"

# Create project  
curl -X POST "https://api-tujulishane-hub.onrender.com/api/projects" \
  -H "Content-Type: application/json" \
  -d '{"partner": "Test Org", "title": "Test Project"}'
```

## 🛠️ Tech Stack
- Java 21 + Spring Boot 3.2.0
- PostgreSQL (Production) / H2 (Development)  
- JWT Authentication
- Automatic Geocoding
- Docker Ready

## 📝 Response Format
```json
{
  "status": 200,
  "message": "Success message", 
  "data": { ... }
}
```