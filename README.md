# SmartPark — Enterprise Smart Parking Management Platform

A high-performance, full-stack Smart Parking SaaS platform featuring real-time occupancy synchronization, dynamic surge pricing, and an event-driven backend.

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?style=for-the-badge)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge)
![Tailwind](https://img.shields.io/badge/Tailwind-3.4-38bdf8?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge)

## 🚀 Project Overview
SmartPark transforms traditional parking facilities into intelligent, data-driven assets. Built with a focus on high availability and seamless user experience, the platform provides facility operators with deep operational insights while offering drivers a frictionless, contactless booking experience.

## ✨ Core Features

### 🏢 Intelligent Facility Management
- **Multi-Floor Visualization**: Interactive, grid-based floor layouts with real-time occupancy tracking.
- **Real-Time Synchronization**: Live slot updates powered by **WebSockets (STOMP)**.
- **Support for Multi-Vehicle Categories**: Managed slots for Cars, Bikes, and EV Charging stations.

### 💰 Enterprise Pricing Engine
- **Dynamic Surge Pricing**: Rule-based engine that adjusts rates based on occupancy levels (70% High / 90% Critical).
- **Temporal & Calendar Rules**: Automated peak-hour surcharges and weekend premiums (15%).
- **Transparent Cost Breakdown**: High-fidelity UI explaining surge factors to the end-user.

### 📊 Advanced Analytics & Control
- **Operator Command Center**: Live system health monitoring (API, DB, WebSockets).
- **Premium Analytics**: Heatmaps for slot utilization, revenue trends, and peak usage analytics.
- **Audit Explorer**: Searchable security and business transaction logs for total accountability.

### 🔐 Security & Identity
- **Stateless Auth**: JWT with secure refresh token rotation.
- **Defensive Engineering**: Rate limiting (Bucket4j), BCrypt hashing, and parameterized JPA queries.

## 🏗️ Technical Architecture

SmartPark utilizes a **Lightweight Event-Driven Architecture (EDA)** within a Spring Boot monolith. This hybrid design allows the system to remain highly efficient on free-tier infrastructure while achieving the decoupling benefits of microservices.

### Backend Strategy
- **Event Bus**: Internal Spring Application Events decouple core logic from cross-cutting concerns.
- **Async Processing**: Notifications, auditing, and analytics updates are handled asynchronously to maximize API throughput.
- **Database Optimization**: Resolved N+1 query problems using **JOIN FETCH** strategies, reducing round-trips by up to 99%.

### Frontend Strategy
- **Design System**: A modern **Glassmorphism** UI built with Tailwind CSS and Framer Motion.
- **Performance**: 40% reduction in initial bundle size through **React.lazy** route-based code splitting.
- **State Management**: Predictable data flow using **Redux Toolkit**.

> [!TIP]
> View the full architecture breakdown in [ARCHITECTURE.md](./ARCHITECTURE.md) and sequence flows in [SEQUENCES.md](./SEQUENCES.md).

## 🛠️ Tech Stack

| Layer | Technologies |
|:---|:---|
| **Backend** | Java 17, Spring Boot 3.2, Spring Security, JPA/Hibernate, MySQL |
| **Frontend** | React 18, Vite, Tailwind CSS, Redux Toolkit, Framer Motion, Recharts |
| **Real-time** | WebSockets (STOMP over SockJS) |
| **Events** | Spring Application Events (Asynchronous) |
| **Deployment** | Docker, Docker Compose, GitHub Actions |

## 📖 API Documentation

The platform provides a fully documented REST API. Access the **Swagger UI** at:
`http://localhost:8080/api/swagger-ui.html`

| Method | Endpoint | Description |
|:---|:---|:---|
| POST | `/api/auth/register` | User onboarding & event trigger |
| GET | `/api/parking/{id}/slots` | Fetch live layout / available slots |
| GET | `/api/parking/estimate-breakdown` | Real-time dynamic pricing calculation |
| GET | `/api/dashboard/analytics` | Premium metric aggregation (Admin only) |
| GET | `/api/admin/health` | Infrastructure vitals & metrics |

## 🚀 Quick Start (Development)

### 1. Requirements
- JDK 17+
- Node.js 18+
- MySQL 8.0

### 2. Environment Setup
Create a `.env` file in the root:
```env
MYSQL_URL=jdbc:mysql://localhost:3306/smartparking
MYSQL_USER=root
MYSQL_PASSWORD=your_password
JWT_SECRET=your_ultra_secure_long_random_secret_key
```

### 3. Execution
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend
cd frontend && npm install && npm run dev
```

## 📦 Deployment Guide

The platform is designed for **Cloud-Native** deployment on free-tier providers:

- **Frontend**: Deploy `frontend/` to **Vercel** or **Netlify**.
- **Backend**: Deploy `backend/` as a Web Service on **Render** or **Railway**.
- **Database**: Use a managed MySQL instance (e.g., **Railway MySQL** or **Aiven**).

For containerized environments:
```bash
docker-compose up --build
```

---

## 🎓 Portfolio & Interview Assets
If you are reviewing this project for a technical role, please refer to:
- [**INTERVIEW.md**](./INTERVIEW.md): Contains resume bullet points, architecture deep-dives, and technical talking points.
- [**PERFORMANCE.md**](./PERFORMANCE.md): Detailed report on optimization strategies and efficiency gains.

---

## 📜 License
SmartPark is released under the [MIT License](./LICENSE). Feel free to use it for portfolio and commercial projects.
