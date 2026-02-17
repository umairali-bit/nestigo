# 🏨 Nestigo – Production-Grade Hotel Booking & Dynamic Pricing Platform

> A production-style, concurrency-safe, dynamically priced hotel booking backend system built with Spring Boot 3, Stripe Payments, and JWT-based security.

Nestigo is not a CRUD demo project.  
It is a **real-world backend architecture simulation** focused on:

- Clean domain modeling
- Inventory consistency under concurrency
- Pluggable pricing engine (Strategy + Decorator)
- Secure stateless authentication
- Payment lifecycle management
- Search correctness across date ranges
- Production-ready design patterns

---

# 🚀 Tech Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT (Stateless) |
| Database | PostgreSQL |
| ORM | Hibernate + Spring Data JPA |
| Payments | Stripe API |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Mapping | ModelMapper |
| Concurrency | Pessimistic Locking |
| Containerization | Docker |
| Architecture | Layered + Clean Separation |

---

# 🧠 System Design Focus

This project emphasizes **backend architecture quality**, not UI.

Key backend concerns solved:

- Preventing overbooking under concurrent requests
- Correct inventory availability across multi-day searches
- Extensible dynamic pricing engine
- Role-based authorization enforcement
- Payment lifecycle synchronization
- Idempotent pricing logic
- Transactional consistency

---

# 🔐 Authentication & Authorization

## JWT-Based Stateless Security

- Access Token (short-lived)
- Refresh Token (long-lived)
- Stateless session management
- Custom `JwtAuthFilter`
- Role-based route protection

### Roles

- `HOTEL_MANAGER`
- `GUEST`

### Route Protection

/admin/** → HOTEL_MANAGER only
/bookings/** → Authenticated
/users/** → Authenticated


Security design principles applied:

- No session storage
- Token-based identity propagation
- Custom AccessDeniedHandler
- Exception resolver integration

---

# 🏨 Domain Model

## Core Entities

- User
- Hotel
- Room
- Inventory
- Booking
- Payment
- Guest

### Inventory Model

The system does not store availability as a single counter.

Instead:

Each room generates **365 inventory rows per year**.

Room A
├── 2026-01-01
├── 2026-01-02
├── 2026-01-03
└── ...


Each row tracks:

- bookedCount
- reservedCount
- totalCount
- surgeFactor
- closed
- dynamic price

This enables:

- Accurate per-day pricing
- Multi-day availability validation
- Surge & occupancy-based pricing

---

# 💰 Dynamic Pricing Engine

Implemented using **Decorator + Strategy Pattern**

Pricing Flow:

BasePriceStrategy
↓
SurgePricingStrategy
↓
OccupancyPricingStrategy
↓
UrgencyPricingStrategy
↓
HolidayPricingStrategy


### Design Benefits

- Open/Closed Principle compliant
- Easy addition of new pricing rules
- Idempotent calculations
- No state mutation inside strategies

### Pricing Factors

- Base price
- Surge multiplier
- Occupancy percentage
- Booking urgency (within 7 days)
- Holiday premium

This mimics real-world pricing engines used by:

- Airbnb
- Uber
- Booking.com

---

# 🔍 Smart Search Engine

Search supports:

- City
- Date range
- Room count
- Pagination

Ensures:

- Availability exists for **every date in range**
- Inventory is not closed
- Sufficient capacity exists
- Hotel is active

Uses:

```sql
GROUP BY hotel
HAVING COUNT(date) = requestedDays
This prevents partial availability errors.
```

# 💳 Stripe Payment Integration

Full payment lifecycle integration using Stripe.

## 🔁 Payment Flow

- User selects dates
- Dynamic pricing calculated
- Stripe PaymentIntent created
- Payment confirmation
- Booking + Payment status updated
- Inventory `bookedCount` incremented

## 🧾 PaymentEntity Tracks

- `transactionID`
- `paymentStatus`
- `amount`
- `booking` reference

## 🧠 Design Considerations

- Secure backend validation
- No trust in client-side amount
- Transactional updates
- Prevents booking without payment

---

# 🔒 Concurrency & Overbooking Protection

Critical section protected using:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

### Applied When:

- Reserving inventory
- Updating room capacity
- Booking confirmation

### Prevents:

- Race conditions
- Double booking
- Inventory corruption

---

# 👥 Guest Management

Users can:

- Create guests
- Update guests
- Retrieve personal guests

### Enforced:

- Ownership validation
- Access control

---

# 📘 API Documentation

### Swagger UI

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

### OpenAPI JSON

```
http://localhost:8080/api/v1/v3/api-docs
```

Fully documented endpoints with summaries and tags.

---

# 🐳 Docker Support

## Build Image

```bash
docker build -t nestigo .
```

## Run Container

```bash
docker run -p 8080:8080 nestigo
```

Ready for containerized deployment.

---

# 🛠 How to Run Locally

## 1️⃣ Clone Repository

```bash
git clone https://github.com/umairali-bit/nestigo.git
cd nestigo
```

## 2️⃣ Configure `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nestigo
    username: postgres
    password: postgres

jwt:
  secretKey: your-256-bit-secret-key

stripe:
  secretKey: your-stripe-secret-key
```

## 3️⃣ Run Application

```bash
mvn clean install
mvn spring-boot:run
```

---

# 🧪 Example Search Request

**POST** `/api/v1/hotels/search`

```json
{
  "city": "Miami",
  "startDate": "2025-12-15",
  "endDate": "2025-12-18",
  "roomCount": 1,
  "page": 0,
  "pageSize": 5
}
```

---

# 🏗 Architectural Highlights

- Layered Architecture (Controller → Service → Repository)
- DTO Separation
- Domain-Driven Modeling
- Decorator Pattern (Pricing)
- Strategy Pattern
- Builder Pattern
- Repository Pattern
- Stateless Security (JWT)
- Transactional Boundaries
- Pessimistic Locking
- Stripe Integration
- OpenAPI Documentation
- Dockerized Deployment

---

# 📈 What This Demonstrates to Recruiters

This project demonstrates:

- Production-level backend thinking
- Real concurrency handling
- Secure authentication implementation
- Stripe integration capability
- Complex search query modeling
- Extensible pricing system
- Clean architecture principles
- Understanding of state consistency
- Defensive backend validation

> This is not tutorial-level CRUD.  
> This is backend system design practice.

---

### 🔮 Future Enhancements
- **Frontend apps**: Admin dashboard for hotel managers + guest booking UI
- **Redis caching**: Cache hotel search results, pricing snapshots, and hot inventory reads
- **Rate limiting**: API throttling per IP/user (e.g., Bucket4j or Spring Gateway)
- **Event-driven workflows**: Booking confirmation, payment success webhook handling, async notifications (Kafka/RabbitMQ)
- **Search at scale**: ElasticSearch for geo + full-text search, filters, and ranking
- **Microservices decomposition**: Split into Auth, Booking, Inventory, Pricing, Payments with clear contracts
- **CI/CD**: Automated tests, security scans, Docker build/push, blue-green or canary deploy
- **Observability**: Metrics + logs + tracing (Prometheus/Grafana + OpenTelemetry/Jaeger)
- **AI/ML (specific use cases)**:
  - Demand forecasting to adjust surge baseline
  - Fraud/risk scoring for payments/bookings
  - Recommendation ranking (“similar hotels”, “best value”) using embeddings

---

# 👨‍💻 Author

**Umair Ali**  
Backend Engineer | System Design Focused | Production-Oriented Java Developer
