# JobTracker

Full-stack job application tracker: Spring Boot REST API + React SPA.

## Repo layout

- `jobtracker-backend/` — Spring Boot API. Own git repo, pushed to GitHub (`bumbens/jobtracker`, branch `main`).
- `jobtracker-frontend/` — React SPA. No git repo yet.

## How we work together

- **"Ja koduje, ty prowadzisz."** The user types the code themselves. Claude explains design decisions, hands over code to transcribe (or a checklist when asked for less hand-holding), reviews what gets written, and catches bugs before/after compile. Claude runs infra/tooling directly (docker, maven, npm, file moves, compiling, curl testing) — that's not "coding" in the pedagogical sense the user cares about.
- The user is learning backend fundamentals as we go (Spring/JPA/Hibernate concepts explained from first principles each time something new comes up). For frontend, the user knows React/JS only from "vibe coding" — wants detailed, line-by-line explanations of *why*, not just working snippets.
- Communicate in Polish by default.
- When something surprising breaks, explain the actual root cause (don't just patch symptoms) — this project has a history of root-causing real framework/environment quirks rather than guessing.

## Backend (`jobtracker-backend/`)

### Stack
- Spring Boot 4.1.1, Spring Framework 7.0.9, Hibernate 7.4.5, Java 17
- PostgreSQL via Docker container `jobtracker-postgres` (postgres:16, port 5432, db/user/pass = `jobtracker`/`postgres`/`postgres`). A second unrelated container `jobtracker-db` also exists on the same port — don't start both at once; `jobtracker-postgres` is the one this project actually uses.
- jjwt 0.12.6 for JWT — chosen over Spring's OAuth2-resource-server/JOSE stack as overkill for a self-issued-token setup.
- Lombok `@Data` on entities; DTOs are plain Java `record`s (immutable, no Lombok needed there).
- Secrets via `.env` (gitignored) + `spring.config.import=optional:file:.env[.properties]` in `application.properties`. **Not** `spring-dotenv` — that library is incompatible with Boot 4 (silently fails to resolve placeholders) and was removed.

### Package structure (`com.jobtracker.jobtracker_backend.*`)
- `model/` — JPA entities: `User`, `JobApplication`, `Contact`, `ApplicationEvent`, `ApplicationStatus` enum.
- `repository/` — Spring Data JPA interfaces.
- `dto/` — request/response records. Entities are never serialized directly over the API.
- `service/` — business logic + ownership checks.
- `controller/` — REST endpoints.
- `config/` — `SecurityConfig`, `JwtAuthFilter`.
- `exception/` — `ResourceNotFoundException`, `EmailAlreadyExistsException`, `InvalidCredentialsException`, `GlobalExceptionHandler`.

All of these must live under `com.jobtracker.jobtracker_backend.*` (matching the `@SpringBootApplication` class's package) — anything in a sibling package like `com.jobtracker.*` is invisible to component-scan and JPA-repository-scan. This bit us twice already (model/ and repository/ were both created in the wrong package initially).

### Conventions established along the way
- JWT principal is the raw `UUID` (user id only, no DB hit per request) — read via `@AuthenticationPrincipal UUID userId` in controllers. An unannotated `UUID` parameter would instead be treated as an implicit `@RequestParam` (Spring MVC special-cases `UUID` as a "simple type") — always annotate it.
- Nested-resource ownership pattern (`Contact`/`ApplicationEvent` under a `JobApplication`): `findOwnedJobApplication` (404 `ResourceNotFoundException` if parent missing, 403 `AccessDeniedException` if parent exists but isn't the caller's) → `findOwnedX` (404 if child missing, **404 — not 403** — if the child belongs to a *different* parent, so as not to confirm the child's existence to a non-owner).
- `AccessDeniedException` is Spring Security's own class (`org.springframework.security.access`) — don't shadow it with a custom class of the same name in the same package (happened once; same-package classes resolve before imports, so the wrong one silently wins). `GlobalExceptionHandler` has its own `@ExceptionHandler` for it (via `@RestControllerAdvice` + `ProblemDetail`) so it gets a consistent JSON body instead of Spring Security's bare 403.
- Bidirectional JPA relations always get `@ToString.Exclude` + `@EqualsAndHashCode.Exclude` on both sides of the relation — Lombok `@Data` + circular references otherwise means `StackOverflowError` on `toString()`/`equals()`/logging/debugger variable views.
- `@CreationTimestamp`/`@UpdateTimestamp` fields are **not** populated on the in-memory Java object until flush (Hibernate 7's generator SPI runs "on execution", not at `persist()`-time) — use `saveAndFlush(...)` / `repository.flush()` right after mutating if the same request's response DTO needs the fresh value.
- `JobApplication.status` always starts at `APPLIED` on create (server-enforced, not client-settable); changed only via `PATCH /api/applications/{id}/status`.
- `Locale.setDefault(Locale.ENGLISH)` is set explicitly in `main()`. Jakarta Bean Validation's default message interpolator ignores Spring's `spring.mvc.locale-resolver` entirely and falls back to the JVM/OS default locale — this machine's OS locale produced Danish validation messages until pinned.
- `UserDetailsServiceAutoConfiguration` is excluded in `@SpringBootApplication(exclude = ...)` — auth bypasses Spring Security's `UserDetailsService` entirely (custom `JwtAuthFilter` + `AuthService`), so the auto-generated random-password user was dead weight, just noisy at startup.

### Known environment quirks — don't re-debug these from scratch
- **OneDrive sync lag.** The whole project lives under OneDrive-synced `Documents`. This repeatedly caused: phantom "file not found" immediately after creating/moving a file, Maven compiling against stale content seconds after a save, and mass "cannot find symbol" cascades on unrelated Lombok-generated getters/setters. In every case so far the real cause was **one genuine error** (a typo, a type mismatch, a missing return) that aborted the annotation-processing round before Lombok ran for the rest of the batch — when you see a huge cascade of missing-method errors across unrelated classes, look for the one real error (often in a DTO or a file you just touched), don't assume Lombok itself broke. If a compile fails in a way that makes no sense against the file content you just read, just recompile once or twice before trusting the error.
- Spring Boot 4 split the old monolithic `spring-boot-starter-test` into narrower starters (`-data-jpa-test`, `-security-test`, `-validation-test`, `-webmvc-test`). JUnit 5, Mockito, and AssertJ still arrive transitively via `spring-boot-starter-data-jpa-test` → `spring-boot-starter-test` — no extra test dependencies needed.
- Several familiar Boot/Security classes moved packages in this version — e.g. `UserDetailsServiceAutoConfiguration` is now `org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration`, not the old `org.springframework.boot.autoconfigure.security.servlet` path. If an import for a well-known Boot class can't be found, search the actual jars in `~/.m2` rather than guessing the old path.

### Status
- **Done:** auth (register/login, JWT issue+validate), full CRUD for `JobApplication`/`Contact`/`ApplicationEvent` with ownership checks, `GlobalExceptionHandler` (`ProblemDetail`-based, consistent error bodies, correct 400/401/403/404/409), `DashboardController` (`GET /api/dashboard/stats`: per-status counts, response rate, average days-to-first-response).
- **In progress:** unit tests. `JwtServiceTest` started (no Spring context needed — construct `JwtService` directly). Plan: unit tests first (`JwtService`, `AuthService` with Mockito, ownership logic in the three nested-resource services), then a handful of integration tests (`@SpringBootTest` + MockMvc + Testcontainers Postgres) for the auth flow and 403/404 enforcement — mirroring what was already verified by hand via curl.
- All backend work through the dashboard endpoint is committed and pushed to `main` on GitHub.

## Frontend (`jobtracker-frontend/`)

### Stack
- Vite + React, **JavaScript, not TypeScript** (deliberate — user knows React/JS but not TS; adding a second new thing on top of a new framework would slow things down, not help).
- Tailwind CSS v4 via the `@tailwindcss/vite` plugin (not the older PostCSS-config approach — `index.css` is just `@import "tailwindcss";`).
- `axios` for HTTP, `react-router-dom` for routing.
- No git repo yet.

### Conventions so far
- `src/api/client.js` — single axios instance (`baseURL: http://localhost:8080/api`); a request interceptor reads `token` from `localStorage` and sets `Authorization: Bearer <token>` on every outgoing request.
- Auth state (`AuthContext`) persists `token` + user info (`userId`, `email`, `displayName`) to `localStorage` (not just React state) so a page refresh doesn't silently log the user out.
- Dev server runs on port 5173, which already matches the backend's CORS allowlist in `SecurityConfig`.

### Status
- Project scaffolded (`npm create vite@latest ... --template react`), demo boilerplate stripped, Tailwind wired in, dev server verified working.
- `src/api/client.js` given in full. `src/context/AuthContext.jsx` given in full but **not yet written/saved by the user** — currently walking through both files concept-by-concept (ES modules, arrow functions, axios interceptors, `localStorage`, template literals, and next: React Context/Provider pattern, `useState`/`useContext` hooks, `async`/`await`, object destructuring) since the user wants to actually understand this, not just paste it.
- Next after the AuthContext walkthrough: wire `AuthProvider` + `BrowserRouter` into `main.jsx`, build a `ProtectedRoute` component, then pages one at a time (Login, Register, Dashboard, Applications list, Application detail with nested Contacts/Events) — same deliberate, one-piece-at-a-time pace used for the backend.
