# Data Architecture

## Entity Relationship Diagram
The schema is designed for transactional integrity and analytical performance.

```mermaid
erDiagram
    USER ||--o{ BOOKING : places
    USER ||--o{ FAVORITE_LOCATION : saves
    USER ||--o{ REFRESH_TOKEN : owns
    USER ||--o{ NOTIFICATION : receives
    USER }|--|{ ROLE : assigned

    PARKING_LOCATION ||--o{ PARKING_FLOOR : contains
    PARKING_LOCATION ||--o{ REVIEW : receives
    PARKING_LOCATION ||--o{ FAVORITE_LOCATION : bookmarked-as

    PARKING_FLOOR ||--o{ PARKING_SLOT : has

    PARKING_SLOT ||--o{ BOOKING : reserved-for

    BOOKING ||--|| PAYMENT : has
    BOOKING ||--|| PARKING_SESSION : tracked-by

    USER {
        long id PK
        string email UK
        string password
        string full_name
        boolean blocked
        datetime last_login_at
    }

    PARKING_LOCATION {
        long id PK
        string name
        string city
        decimal hourly_rate
        decimal latitude
        decimal longitude
    }

    PARKING_SLOT {
        long id PK
        string slot_number
        string vehicle_type
        string status
        boolean ev_charging
    }

    BOOKING {
        long id PK
        string booking_code UK
        datetime start_time
        datetime end_time
        decimal estimated_fee
        string status
    }

    PAYMENT {
        long id PK
        decimal amount
        string status
        string transaction_id UK
    }

    AUDIT_LOG {
        long id PK
        string user_email
        string action
        string details
        datetime created_at
    }
```

## Key Constraints & Optimization
- **Unique Indexes**: Implemented on `user(email)`, `booking(booking_code)`, and `payment(transaction_id)` to ensure data consistency.
- **Foreign Key Constraints**: Cascading deletes are strictly controlled (e.g., `PARKING_FLOOR` -> `PARKING_SLOT`) to maintain referential integrity.
- **Data Types**: Correct use of `DECIMAL(19,2)` for financial fields to avoid rounding errors.
