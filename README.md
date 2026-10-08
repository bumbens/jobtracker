# JobTracker — Backend

REST API for tracking job applications: companies, statuses, recruiter contacts, and status-change history, with a dashboard summary (response rate, average time to first response).

## Tech stack

- Java 17, Spring Boot 4.1.1 (Spring Framework 7, Hibernate 7)
- Spring Data JPA + PostgreSQL
- Spring Security (stateless, JWT-based — no sessions)
- [jjwt](https://github.com/jwtk/jjwt) 0.12.6 for token issuing/validation
- Lombok (entities) / Java `record`s (DTOs)
- Maven

## Getting started

### Prerequisites
- JDK 17
- Docker (for PostgreSQL)

### 1. Start the database

```bash
docker run --name jobtracker-postgres -e POSTGRES_DB=jobtracker \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:16
```

### 2. Configure secrets

Create a `.env` file in the project root (gitignored):

```
DATABASE_URL=jdbc:postgresql://localhost:5432/jobtracker
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JWT_SECRET=<a long random string, at least 256 bits>
```

### 3. Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

## API overview

All endpoints below except `/api/auth/*` require `Authorization: Bearer <token>`.

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account |
| POST | `/api/auth/login` | Log in, get a JWT |
| GET | `/api/applications` | List the caller's job applications (optional `?status=`) |
| POST | `/api/applications` | Create a job application |
| GET | `/api/applications/{id}` | Get one application |
| PUT | `/api/applications/{id}` | Update an application |
| PATCH | `/api/applications/{id}/status` | Change status |
| DELETE | `/api/applications/{id}` | Delete an application |
| GET/POST | `/api/applications/{id}/contacts` | List/create contacts for an application |
| GET/PUT/DELETE | `/api/applications/{id}/contacts/{contactId}` | Manage a single contact |
| GET/POST | `/api/applications/{id}/events` | List/create status-change events |
| GET/PUT/DELETE | `/api/applications/{id}/events/{eventId}` | Manage a single event |
| GET | `/api/dashboard/stats` | Aggregate stats for the caller |

All resources are scoped to the authenticated user. Accessing another user's resource returns `404 Not Found` (not `403`), so a caller can't tell whether a resource exists but belongs to someone else.

## Project structure

```
model/       JPA entities
repository/  Spring Data JPA repositories
dto/         Request/response records (entities are never serialized directly)
service/     Business logic, ownership checks
controller/  REST endpoints
config/      Security config, JWT filter
exception/   Custom exceptions + global exception handler (RFC 7807 ProblemDetail)
```

## Status

Done: auth, full CRUD for applications/contacts/events, consistent error responses, dashboard stats.
In progress: unit and integration tests + frontend (React).
