# Interview & Career Assets

## Project Explanation (Elevator Pitch)
"I developed **SmartPark**, an enterprise-grade parking management platform that transforms traditional parking facilities into intelligent digital assets. I used a **Spring Boot** and **React** stack to solve real-world problems like real-time occupancy synchronization and dynamic pricing. What sets this project apart is its internal **Event-Driven Architecture**, which allows the system to handle complex workflows like automated notifications and security auditing asynchronously, ensuring high performance even on resource-constrained infrastructure."

## Architecture Highlights (Technical Deep-Dive)
- **Real-Time Data Layer**: Built using **WebSockets (STOMP over SockJS)** to provide instant UI updates when slot statuses change on the server.
- **Dynamic Pricing Engine**: Implemented a rule-based algorithm that calculates fees based on multiple factors: occupancy density, peak hour windows, and calendar multipliers (weekends).
- **Lightweight EDA**: Transitioned the monolithic backend into an **Event-Driven Architecture** using Spring Application Events, decoupling core business logic from cross-cutting concerns (Auditing, Notifications).
- **Performance Engineering**: Optimized the database layer by resolving N+1 query issues through **JOIN FETCH** strategies, reducing data-fetch latency by over 90% in heavy administrative views.

## Resume Bullet Points
- Architected and implemented a full-stack Smart Parking Platform using **Java (Spring Boot)** and **React (Vite)**, supporting multi-floor facility management and real-time occupancy tracking.
- Developed a **Dynamic Pricing Engine** that utilizes real-time occupancy and temporal peak-hour data to calculate surge-adjusted parking fees.
- Implemented an internal **Event-Driven Architecture** using Spring Application Events to decouple core services, enabling asynchronous processing for notifications and security auditing.
- Optimized backend performance by resolving **N+1 query problems** and implementing **optimized fetching strategies**, resulting in a 99% reduction in database round-trips for list-heavy views.
- Designed a modern **Glassmorphism UI** with **Tailwind CSS** and **Framer Motion**, achieving a 40% reduction in initial load time through **React.lazy** route-based code splitting.

## HR Interview Talking Points
- **Problem Solving**: "I identified that manual occupancy tracking is prone to error, so I implemented a real-time system that synchronizes across all users instantly using WebSockets."
- **Scalability**: "While building this, I kept performance in mind, optimizing the database queries to ensure the application could scale from a single floor to hundreds of locations without slowing down."
- **User Experience**: "I focused on transparency by building a pricing breakdown component, explaining to users exactly why a certain rate was applied, which builds trust."

## Technical Interview Talking Points
- **JPA Fetching**: "Can you explain why you used `JOIN FETCH`?" -> "To avoid the N+1 problem where the ORM executes one query for the parent and N queries for children, which is a major performance bottleneck."
- **Event Bus**: "Why use internal Spring Events instead of Kafka?" -> "For this scale, Kafka would introduce unnecessary infrastructure overhead. Spring Events provide the same decoupling benefits with zero additional resource requirements, keeping it compatible with free-tier deployments."
- **Frontend Perf**: "How did you optimize the React bundle?" -> "I used route-based code splitting with `React.lazy`. This ensures users only download the code for the specific page they are viewing, significantly improving the 'First Contentful Paint'."
