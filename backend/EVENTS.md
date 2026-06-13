# Event-Driven Architecture Flow Documentation

This document describes the lightweight event-driven architecture implemented using Spring Application Events.

## Domain Events

| Event | Publisher | Description |
|:---|:---|:---|
| `UserRegisteredEvent` | `AuthService` | Triggered when a new user completes registration. |
| `BookingCreatedEvent` | `BookingService` | Triggered when a parking slot is successfully booked. |
| `PaymentCompletedEvent` | `BookingService` | Triggered when payment for a booking is confirmed. |
| `ReservationCreatedEvent`| `WaitlistService` | Triggered when a user joins the waitlist for a facility. |
| `ReservationExpiredEvent`| `SchedulingConfig` | Triggered when a booking time window expires. |
| `NotificationCreatedEvent`| `NotificationService`| Triggered when a system notification is persisted. |

## Event Flow & Listeners

### 1. Notification Flow
**Listeners:** `NotificationListener`
- **UserRegisteredEvent**: Sends a welcome notification to the user.
- **BookingCreatedEvent**: Sends a booking confirmation notification.
- **ReservationCreatedEvent**: Sends a waitlist confirmation notification.
- **ReservationExpiredEvent**: Sends a session completion notification.

### 2. Audit Logging Flow
**Listeners:** `AuditLogListener`
- **UserRegisteredEvent**: Logs the creation of a new user account.
- **BookingCreatedEvent**: Logs the details of the new booking and location.
- **PaymentCompletedEvent**: Logs transaction IDs and financial details.

### 3. Analytics Flow
**Listeners:** `AnalyticsListener`
- **BookingCreatedEvent**: Placeholder for updating occupancy heatmaps and trends.
- **PaymentCompletedEvent**: Placeholder for revenue forecasting and financial reporting.

### 4. System Monitoring
**Listeners:** `NotificationCreatedListener`
- **NotificationCreatedEvent**: Logs system-wide notification dispatch (Integration point for Push services).

## Architectural Benefits
- **Separation of Concerns**: Business services no longer need to know about auditing or notification logic.
- **Improved Performance**: Listeners execute asynchronously (`@Async`), ensuring that the primary user request (e.g., booking a slot) is not delayed by secondary tasks.
- **Extensibility**: New features (like sending an email or updating a cache) can be added by simply creating a new listener without modifying existing service code.
