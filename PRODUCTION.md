# SmartPark Production Configuration

## Mandatory Environment Variables

| Variable | Description | Example |
|:---|:---|:---|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` | `prod` |
| `DATABASE_URL` | MySQL Connection String | `jdbc:mysql://db.example.com:3306/smartparking` |
| `DATABASE_USERNAME` | Production DB User | `smartpark_app` |
| `DATABASE_PASSWORD` | Production DB Password | `[REDACTED]` |
| `JWT_SECRET` | 512-bit Secure Secret | `[GENERATE_LONG_RANDOM_STRING]` |
| `CORS_ORIGINS` | Allowed Frontend URL | `https://smartpark.vercel.app` |
| `FRONTEND_URL` | Frontend URL for links | `https://smartpark.vercel.app` |
| `MAIL_USERNAME` | SMTP User | `support@smartparking.com` |
| `MAIL_PASSWORD` | SMTP Password | `[REDACTED]` |
| `MAIL_HOST` | SMTP Host | `smtp.gmail.com` |

## Security Hardening
- **Authentication**: Argon2id hashing enabled.
- **Sessions**: Secure, HttpOnly, SameSite=Strict cookies.
- **Database**: Versioned migrations via Flyway.
- **Monitoring**: Actuator enabled on port 8081.
- **Containers**: Non-root Alpine execution.

## Deployment Steps
1. Provision a MySQL 8.0 database.
2. Build the backend: `mvn clean package -Pprod`.
3. Build the frontend: `npm run build`.
4. Deploy using the provided `Dockerfile` or to a PaaS like Render/Vercel.
