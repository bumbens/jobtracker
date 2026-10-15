# JobTracker — Frontend

React SPA for the JobTracker API: login/register, a filterable list of job applications with inline status editing, an add-application form, per-application detail (contacts, status history), and a dashboard with a per-status breakdown chart.

## Tech stack

- Vite + React 19, **JavaScript, not TypeScript**
- Tailwind CSS v4 (via the `@tailwindcss/vite` plugin)
- [axios](https://axios-http.com/) for HTTP, with a request interceptor that attaches the JWT from `localStorage`
- [react-router-dom](https://reactrouter.com/) for routing
- [Vitest](https://vitest.dev/) + [React Testing Library](https://testing-library.com/react) for tests

## Getting started

### Prerequisites
- Node.js
- The backend running on `http://localhost:8080` (see [`../jobtracker-backend/README.md`](../jobtracker-backend/README.md))

### Run

```bash
npm install
npm run dev
```

The dev server starts on `http://localhost:5173` — already in the backend's CORS allowlist.

### Other scripts

| Command | Does |
|---|---|
| `npm run build` | Production build to `dist/` |
| `npm run preview` | Serves the `dist/` build locally |
| `npm run lint` | Runs `oxlint` |
| `npm test` | Runs Vitest (watch mode; pass `--run` for a single pass) |

## Routes

| Path | Page | Protected? |
|---|---|---|
| `/login` | Log in | No |
| `/register` | Create an account | No |
| `/dashboard` | Greeting, stats, status breakdown chart | Yes |
| `/applications` | List, filter by status, change status inline, delete | Yes |
| `/add-application` | Create a new application | Yes |
| `/applications/:id` | One application's detail: contacts, status history | Yes |

"Protected" means wrapped in `ProtectedRoute`, which redirects to `/login` if no user is logged in.

## Project structure

```
src/
  api/        axios instance + request interceptor (src/api/client.js)
  components/ small, reusable, data-driven pieces (ProtectedRoute, Spinner, StatusBarChart)
  context/    AuthContext — current user + login/register/logout, backed by localStorage
  pages/      one component per route
  test/       Vitest setup (jest-dom matchers)
```

## Conventions

- Every page that fetches data follows the same shape: `loading` / `error` / data state from `useState`, fetched in a `useEffect` with an empty dependency array (`[]`) unless the fetch depends on a route param (e.g. `/applications/:id`, which depends on `[id]`).
- List updates after a mutation (delete, status change) update local state directly (`.filter()` / `.map()`) instead of refetching the whole list.
- Styling is Tailwind utility classes only, mobile-first (unprefixed classes are the base/mobile layout; `sm:`/`md:` override upward).

## Status

Done: auth, protected routing, applications list (fetch/filter/delete/inline status change), add-application form, application detail page, dashboard with a status breakdown chart.
In progress: adding/editing contacts and events from the UI, broader test coverage (first test written against a pure helper function; component tests next).

See [`../CLAUDE.md`](../CLAUDE.md) for detailed conventions and known environment quirks.
