# Core Sequence Flows

## 1. Smart Booking Flow
How the system handles price discovery, booking, and real-time synchronization.

```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant API
    participant PricingEngine
    participant Database
    participant WebSocket

    User->>Frontend: Select Slot & Duration
    Frontend->>API: GET /estimate-breakdown
    API->>PricingEngine: Calculate (Occupancy, Peak, Weekend)
    PricingEngine-->>API: Breakdown Details
    API-->>Frontend: Price Details (₹)
    User->>Frontend: Click Confirm Booking
    Frontend->>API: POST /prebook
    API->>Database: Save Booking (RESERVED status)
    API->>Database: Create Payment Record
    API->>API: Publish BookingCreatedEvent
    API-->>Frontend: Return Booking Details & QR
    API->>WebSocket: Broadcast Slot Update
    WebSocket-->>Frontend: Update Interactive Layout
```

## 2. Real-Time Occupancy Flow
How multi-floor layout stays synchronized across multiple clients.

```mermaid
sequenceDiagram
    participant Operator
    participant API
    participant Database
    participant EventBus
    participant WebSocket
    participant ClientA
    participant ClientB

    Operator->>API: Check-in Vehicle (QR Scan)
    API->>Database: Update Slot (OCCUPIED)
    API->>EventBus: Publish OccupancyEvent
    EventBus->>WebSocket: Trigger Broadcast
    WebSocket-->>ClientA: Update Layout Grid
    WebSocket-->>ClientB: Update Layout Grid
```

## 3. Asynchronous Notification Flow
How internal system events trigger user communications without blocking.

```mermaid
sequenceDiagram
    participant Service
    participant EventBus
    participant Listener
    participant NotifyService
    participant Database

    Service->>Database: Perform Primary Action (e.g., Register)
    Service->>EventBus: Publish UserRegisteredEvent
    Service-->>User: Immediate Response (Success)
    
    Note over EventBus,Listener: Asynchronous Hand-off
    
    EventBus->>Listener: handleUserRegistered()
    Listener->>NotifyService: createNotification()
    NotifyService->>Database: Save Notification
    NotifyService->>WebSocket: Send Toast Alert
```

## 4. Security & JWT Rotation
How the system maintains stateless security with automatic refresh.

```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant SecurityFilter
    participant JwtService
    participant Database

    User->>Frontend: Make Authorized Request
    Frontend->>SecurityFilter: Header: Bearer [Expired Token]
    SecurityFilter-->>Frontend: 401 Unauthorized
    Frontend->>SecurityFilter: POST /refresh-token
    SecurityFilter->>Database: Validate Refresh Token
    Database-->>SecurityFilter: Valid
    SecurityFilter->>JwtService: Generate New Access Token
    JwtService-->>Frontend: Return Token Pair
    Frontend->>SecurityFilter: Retry Original Request
    SecurityFilter-->>Frontend: 200 OK
```
