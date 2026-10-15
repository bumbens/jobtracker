# JobTracker

[![CI](https://github.com/bumbens/jobtracker/actions/workflows/ci.yml/badge.svg)](https://github.com/bumbens/jobtracker/actions/workflows/ci.yml)

Full-stack job application tracker — Spring Boot REST API + React SPA, in a single monorepo.

Track job applications end to end: company, position, status, recruiter contacts per application, a timeline of status-change events, and a dashboard summarizing where things stand (counts per status, response rate, average time to first response).

## Structure

```
jobtracker-backend/   Spring Boot REST API (Java 17, PostgreSQL, JWT auth)
jobtracker-frontend/  React SPA (Vite, Tailwind CSS v4)
```

The backend has its own [README](jobtracker-backend/README.md) with stack details, setup, and the full API reference. This file is the entry point — start here, then dive into whichever side you're working on.

## Quick start

### Backend

```bash
cd jobtracker-backend
docker run --name jobtracker-postgres -e POSTGRES_DB=jobtracker \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:16
# create a .env file — see jobtracker-backend/README.md for the required variables
./mvnw spring-boot:run
```

Runs on `http://localhost:8080`.

### Frontend

```bash
cd jobtracker-frontend
npm install
npm run dev
```

Runs on `http://localhost:5173` (already allowed by the backend's CORS config).

## Testing

```bash
cd jobtracker-backend && ./mvnw test   # JUnit, against a real Postgres
cd jobtracker-frontend && npm test     # Vitest, watch mode by default (--run for a single pass)
```

## CI

`.github/workflows/ci.yml` runs on every push/PR to `main`: backend tests against a real Postgres service container, frontend lint + build.

## Status

- **Backend:** auth, full CRUD for applications/contacts/events, consistent error responses, dashboard stats. Unit tests started; integration tests still to come.
- **Frontend:** auth, a filterable applications list with inline status editing, an add-application form, application detail page with contacts/events, and a dashboard with a per-status breakdown chart. Contact/event management from the UI and a broader test suite are in progress.

See [`CLAUDE.md`](CLAUDE.md) for detailed conventions and known environment quirks (AI-assistant-facing, but useful background for anyone picking up the project).
