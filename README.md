# SmartPark — Enterprise Smart Parking Management Platform

A production-ready, full-stack Smart Parking SaaS platform transformed from the basic [Car-Parking-Management-System](https://github.com/Priyanshu-Patidar/Car-Parking-Management-System) into an enterprise-grade solution with JWT security, real-time availability, map-based search, smart booking, QR entry, analytics, and admin management.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![React](https://img.shields.io/badge/React-18-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

## Features

### User Features
- Secure registration & JWT authentication with refresh tokens
- Search parking by city, address, or GPS location
- Interactive OpenStreetMap + Leaflet map with live markers
- Interactive Floor Layout Visualization with real-time slot occupancy
- Enterprise Dynamic Pricing with real-time fee breakdown
- Real-time slot availability (WebSocket)
- Smart pre-booking with fee estimation & QR code entry
- Booking history, receipts, favorites
- Reviews & ratings
- Dark/light mode responsive UI

### Admin Features
- Parking location / floor / slot management
- User management (block/unblock/delete)
- Booking oversight & audit logs
- Premium Analytics Platform (Revenue, Occupancy, Vehicle Trends, Heatmaps)
- Revenue & occupancy analytics dashboard
- Peak-hour demand analytics (AI-inspired dynamic pricing)

### Security
- BCrypt password hashing
- Role-Based Access Control (ADMIN, USER)
- JWT + refresh token rotation
- API rate limiting (Bucket4j)
- CORS, input validation, global exception handling
- SQL injection prevention via JPA

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Backend | Java 17, Spring Boot 3, Spring Security, JPA, MySQL/H2, JWT, Swagger, WebSocket |
| Frontend | React 18, Vite, Tailwind CSS, Redux Toolkit, Leaflet, Recharts, Framer Motion |
| Maps | OpenStreetMap + Leaflet.js (free) |
| DevOps | Docker, Docker Compose, GitHub Actions |

## Architecture

```mermaid
flowchart TB
    subgraph Client["React Frontend (Vercel)"]
        UI[Pages & Components]
        Redux[Redux Store]
        Map[Leaflet Map]
    end

    subgraph API["Spring Boot API (Render/Railway)"]
        Auth[JWT Auth Layer]
        Controllers[REST Controllers]
        Services[Service Layer]
        Analytics[Fee & Dashboard Analytics]
    end

    subgraph Data["Database (Railway MySQL)"]
        MySQL[(MySQL / H2 Dev)]
    end

    UI --> Redux
    Redux -->|HTTPS + JWT| Auth
    Auth --> Controllers
    Controllers --> Services
    Services --> Analytics
    Services --> MySQL
    Map -->|OSM Tiles| OSM[OpenStreetMap]
    Services -->|WebSocket| UI
```

## Project Structure

```
smart-parking-platform/
├── backend/                 # Spring Boot API
│   └── src/main/java/com/smartparking/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── config/
│       ├── security/
│       ├── exception/
│       ├── util/
│       ├── mapper/
│       └── analytics/
├── frontend/                # React SPA
│   └── src/
│       ├── pages/
│       ├── components/
│       ├── store/
│       └── api/
├── docker-compose.yml
├── .github/workflows/ci.yml
└── README.md
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/refresh-token` | Refresh JWT |
| GET | `/api/parking/search?location=` | Search by location |
| GET | `/api/parking/nearby?lat=&lng=` | Nearby parking |
| GET | `/api/parking/{id}` | Parking details |
| POST | `/api/parking/prebook` | Pre-book slot |
| PUT | `/api/parking/cancel/{id}` | Cancel booking |
| GET | `/api/bookings/user` | User bookings |
| GET | `/api/bookings/admin` | All bookings (admin) |
| POST | `/api/admin/parking` | Create parking (admin) |
| GET | `/api/dashboard/stats` | Analytics |

**Swagger UI:** `http://localhost:8080/api/swagger-ui.html`

## Quick Start (Local)

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+

### 1. Clone & setup
```powershell
cd C:\Users\priya\Projects\smart-parking-platform
.\scripts\setup.ps1
```

### 2. Start backend
```powershell
cd backend
mvn spring-boot:run
```

### 3. Start frontend
```powershell
cd frontend
npm run dev
```

Open **http://localhost:5173**

### Demo Credentials
| Role | Email | Password |
|------|-------|----------|
| Admin | admin@smartparking.com | Admin@123 |
| User | user@smartparking.com | User@123 |

## Docker

```bash
docker-compose up --build
```

- Frontend: http://localhost:5173
- Backend: http://localhost:8080/api
- MySQL: localhost:3306

## Deployment (Free Tier)

### Frontend → Vercel
1. Import `frontend/` folder to Vercel
2. Set env: `VITE_API_BASE_URL=https://your-api.onrender.com/api`
3. Deploy

### Backend → Render
1. Create Web Service from `backend/`
2. Build: `mvn -DskipTests package`
3. Start: `java -jar target/smart-parking-platform-1.0.0.jar`
4. Env vars: `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_URL`, `JWT_SECRET`, `CORS_ORIGINS`

### Database → Railway MySQL
1. Create MySQL instance on Railway
2. Copy connection URL to `DATABASE_URL`

## Screenshots

> 
> - Home page
> - <img width="1919" height="862" alt="image" src="https://github.com/user-attachments/assets/89d2a2b6-5324-4353-bfc8-8e691bd2a2d2" />

> - Map view
> - <img width="1919" height="873" alt="image" src="https://github.com/user-attachments/assets/12db0a2b-9b6c-4554-a372-e1c0f409cae0" />

> - Booking confirmation with QR
> - <img width="1919" height="863" alt="image" src="https://github.com/user-attachments/assets/5d717645-0a1f-46df-8656-850e32a2af90" />
> - <img width="1919" height="869" alt="image" src="https://github.com/user-attachments/assets/adcf217e-e2b1-4a5e-82b6-eb52c638fa60" />

> - Admin dashboard
> - <img width="1919" height="871" alt="image" src="https://github.com/user-attachments/assets/e34700fb-0cf1-4266-b859-133b2b45b006" />


## Testing

```bash
cd backend && mvn test
```

## Future Enhancements

- [ ] Mobile app (React Native)
- [ ] Payment gateway (Razorpay/Stripe)
- [ ] License plate OCR entry
- [ ] ML-based occupancy prediction
- [ ] Multi-tenant operator support

## License

MIT License — free for portfolio and commercial use.

---
