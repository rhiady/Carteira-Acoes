## Context

See `proposal.md` for motivation and the delta specs for the observable contract. The frontend already has a root `AuthService`, an authentication guard, centralized API configuration, lazy standalone feature routes, and a Swiss/financial visual system. The published backend contract is the source of truth for `/api/auth/login`, `/api/auth/me`, Bearer authentication, `ROLE_ADMIN`, and `ROLE_USER`.

## Goals / Non-Goals

**Goals:**

- Replace the current local-identity authentication flow with one typed JWT session shared by the HTTP interceptor, guards and navigation.
- Make session restoration validate the token through `/api/auth/me`, honor `expiresIn`, and clear all session/financial context on logout or `401`.
- Introduce the smallest route-level separation: an administrative shell/entry area and a common-user shell/entry area, reusing existing feature pages and shared components where possible.
- Keep the existing Swiss visual language: neutral layered surfaces, Helvetica-style sans typography, hairline rules, restrained blue action color, visible focus, and no fabricated admin metrics.
- Preserve lazy loading and add focused tests at service, interceptor, guard, route, page and accessibility boundaries.

**Non-Goals:**

- No JWT decoding library, backend changes, token refresh endpoint, new financial calculations, or client-side substitute for backend authorization.
- No broad rewrite of existing feature folders or duplicate copies of portfolio pages for each role.
- No new admin capabilities beyond the endpoints documented by the integration specification.

## Decisions

### Use the published auth contract without decoding JWT

Model login as `{ login, password }` and its response as `{ token, tipo, expiresIn }`; use `/api/auth/me` for identity and authorities. This avoids duplicating backend token claims and follows the explicit contract. Decoding JWT in the browser was rejected because it adds a dependency and would still not establish authorization.

### Keep the token in session storage and identity in memory

Persist only the token and expiry metadata in `sessionStorage` so a refresh in the same browser tab can recover the session while a closed tab does not retain a long-lived credential. Keep the authenticated identity signal in `AuthService`, restore it with `/api/auth/me`, and remove both on expiry, logout or `401`. Storing the whole identity in local storage was rejected because it can become stale and was part of the old contract.

### Centralize Bearer injection and 401/403 behavior

Use the existing HTTP interceptor boundary to omit auth for `/api/auth/login`, add `Authorization` when a token exists, clear and navigate on `401`, and preserve the session while surfacing access denial on `403`. Components will not repeat token handling or status branching.

### Use route data plus a single role guard

Keep `authGuard` responsible for an authenticated session and add role metadata to administrative routes, with one role-aware guard reading route data and the `AuthService` signal. The default route resolves to a role-specific entry route after authentication. This is smaller and less error-prone than maintaining separate auth services or duplicating every existing route tree.

### Separate navigation before separating financial screens

Create the two visible entry shells/navigation sets and classify existing routes by backend responsibility: user-owned investment routes remain in the common area; user, broker and other documented management commands are exposed in the admin area only where the API contract authorizes them. Reuse existing pages rather than cloning them, and treat backend `403` as the final protection.

### Retain the Swiss design direction

The frontend-design direction remains Swiss because the existing product is a dense financial surface. The differentiator is a role-specific entry rail with explicit authority label and a compact route index, using current tokens and real navigation labels only; no decorative dashboard data is introduced.

## Risks / Trade-offs

- [A token may expire while a request is in flight] → Treat the first `401` as the session boundary, clear state once, and redirect to `/login` without retrying with stale credentials.
- [Session storage is tab-scoped] → Restore sessions only within the same tab; require login again after a tab/browser session ends, which limits credential persistence.
- [A route may be visually hidden but still reachable by URL] → Apply the role guard to every administrative route and retain backend `403` handling; navigation visibility is not the security boundary.
- [Existing archived auth specs describe an older payload] → Update only the active change's delta and verify implementation against `ESPECIFICACAO-INTEGRACAO-FRONTEND.md` before coding; do not silently invent compatibility fields.
- [Admin endpoints may not have complete frontend pages] → Expose only published operations and use existing shared loading/error/access-denied states until a separate capability is specified.

## Migration Plan

1. Add typed JWT/session models and update `AuthService`, then update the interceptor and guards.
2. Update login and route configuration, introduce the two role entry experiences, and classify existing routes without duplicating feature pages.
3. Update logout/context clearing and API error handling, then run focused tests plus AXE checks in light/dark and mobile/desktop viewports.
4. Roll back by reverting the auth/session, guard/interceptor, route and entry-shell changes; no backend or persistence migration is required.
