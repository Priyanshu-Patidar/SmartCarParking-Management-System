# Smart Parking Security Audit & Report

## 1. Authentication Architecture (JWT)
- **Mechanism:** The application utilizes JSON Web Tokens (JWT) for stateless authentication.
- **Rotation & Expiration:** Access tokens are short-lived. A `refresh-token` strategy is fully implemented. The frontend `axios` interceptor automatically catches `401 Unauthorized` responses, attempts to refresh the token using the stored refresh token, and seamlessly retries the original request.
- **Persistence:** User tokens are stored in `localStorage` under the `smartpark_auth` key.

## 2. Role-Based Access Control (RBAC)
- **Frontend Authorization:** Route protection is enforced via the `selectIsAdmin` Redux selector (`state.auth.user?.roles?.includes('ROLE_ADMIN')`). Standard users are routed to `/dashboard`, while administrators are routed to `/admin`.
- **Backend Authorization:** Spring Security enforces role requirements on protected endpoints. If a user attempts to access an unauthorized endpoint (e.g., an admin-only stats endpoint), the system returns a `403 Forbidden`. The frontend degrades gracefully when encountering 403s on individual dashboard widgets.

## 3. Threat Mitigation
- **SQL Injection (SQLi):** Mitigated by utilizing Spring Data JPA and Hibernate, ensuring all database interactions use parameterized queries.
- **DDoS & Brute Force:** API endpoints are protected against excessive traffic using Bucket4j Rate Limiting at the Security Layer.
- **Cross-Site Scripting (XSS):** React naturally escapes embedded data during rendering.
