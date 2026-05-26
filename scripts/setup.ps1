# Smart Parking Platform - Windows Setup Script
Write-Host "=== Smart Parking Platform Setup ===" -ForegroundColor Cyan

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

# Backend
Write-Host "`n[1/3] Building backend..." -ForegroundColor Yellow
Set-Location "$root\backend"
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn -q -DskipTests package
    Write-Host "Backend built successfully" -ForegroundColor Green
} else {
    Write-Host "Maven not found. Install Maven and Java 17+" -ForegroundColor Red
}

# Frontend
Write-Host "`n[2/3] Installing frontend dependencies..." -ForegroundColor Yellow
Set-Location "$root\frontend"
if (Get-Command npm -ErrorAction SilentlyContinue) {
    npm install
    Write-Host "Frontend ready" -ForegroundColor Green
} else {
    Write-Host "Node.js not found. Install Node.js 18+" -ForegroundColor Red
}

Set-Location $root
Write-Host "`n[3/3] Setup complete!" -ForegroundColor Green
Write-Host @"

Run the application:
  Backend:  cd backend && mvn spring-boot:run
  Frontend: cd frontend && npm run dev

Demo accounts:
  Admin: admin@smartparking.com / Admin@123
  User:  user@smartparking.com / User@123

API Docs: http://localhost:8080/api/swagger-ui.html
Frontend: http://localhost:5173

"@ -ForegroundColor Cyan
