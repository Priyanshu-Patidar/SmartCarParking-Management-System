# Technical Architecture

## System Overview
The SmartPark Platform is built on a modern, decoupled architecture designed for high availability, real-time interactivity, and enterprise-grade security.

```mermaid
flowchart TB
    subgraph Client["Frontend (React / Vite)"]
        UI[Glassmorphism UI]
        Redux[Redux Toolkit State]
        SocketClient[STOMP/SockJS Client]
        LazyRoutes[React.lazy Route Splitting]
    end

    subgraph Security["Security Layer"]
        JWT[JWT Authentication]
        RateLimit[Bucket4j Rate Limiter]
        RBAC[Role-Based Access Control]
    end

    subgraph API["Backend (Spring Boot Monolith)"]
        Controllers[REST Controllers]
        Services[Service Layer]
        PricingEngine[Dynamic Pricing Engine]
        AnalyticsService[Aggregation Analytics]
        EventBus[Spring Application Events]
    end

    subgraph Messaging["Real-time & Events"]
        WS[WebSocket /topic]
        AsyncListeners[Async Event Listeners]
    end

    subgraph Persistence["Data Layer"]
        MySQL[(MySQL Database)]
        JPA[Hibernate / Optimized Fetching]
    end

    UI --> Redux
    Redux -->|HTTPS / JWT| Security
    Security --> Controllers
    Controllers --> Services
    Services --> PricingEngine
    Services --> AnalyticsService
    Services --> EventBus
    EventBus -->|Async| AsyncListeners
    AsyncListeners --> WS
    Services --> JPA
    JPA --> MySQL
    WS -.->|Live Updates| SocketClient
    SocketClient -.-> UI
```

## Architectural Highlights

### 1. Hybrid Monolithic Design
We preserve the simplicity of a monolith for deployment efficiency while implementing **Event-Driven Architecture** internally. This allows asynchronous processing of non-blocking tasks like notifications and auditing.

### 2. Real-Time Interactivity
Integrated **WebSockets (STOMP)** provide instant synchronization between the facility occupancy state and the user's view, eliminating manual refreshes.

### 3. Enterprise Security
Multiple layers of defense:
- **Rate Limiting**: Protects against DDoS and brute force.
- **JWT Rotation**: Secure, stateless authentication with refresh tokens.
- **SQLi Prevention**: Leveraging Spring Data JPA and parameterized queries.

### 4. Performance Optimized
- **Backend**: Resolved N+1 problems via optimized `JOIN FETCH` queries.
- **Frontend**: Reduced bundle size via route-based code splitting.
