## Context

See [proposal.md](proposal.md). The current project already defines an administrator-only user-management route, a typed user service, paginated API conventions, shared loading states, and role-aware navigation. The defect must be corrected within those boundaries without making the browser authoritative for authorization or user data.

## Goals / Non-Goals

**Goals:**

- Trace the existing administrative Users route through its route, page, service call, and response-state rendering.
- Restore the existing paginated listing for an authenticated administrator using the published contract and shared UI patterns.
- Cover the regression at the route/page and service boundary that caused the listing to be absent.

**Non-Goals:**

- Changing user permissions, JWT/session behavior, API endpoints, payloads, or pagination defaults.
- Creating a new user-management design, duplicate page, client-side user filtering, or new shared abstraction.

## Decisions

### Repair the existing route-to-service path

Inspect the existing route, page and `UsuarioService` before editing, then correct the broken boundary that prevents the published listing from reaching the page. Reusing this path preserves one source of truth for pagination, normalization and backend errors. Creating a replacement administration page or a second service method would add divergent behavior without solving the root cause.

### Render only authoritative response states

Use the existing paginated response as the list source and retain its loading, empty, error and retry/pagination states. Do not cache or synthesize users locally: the backend response remains authoritative, and the route guard plus backend authorization remain the access controls.

### Add focused regression tests at the failure boundary

Test the administrator route/page with a successful response and relevant empty/error responses, plus the existing typed service request when needed to expose the root defect. This is smaller and more durable than an end-to-end test for a known boundary break.

## Risks / Trade-offs

- [The missing list originates from a backend authorization or contract mismatch rather than rendering] → Preserve and surface the normalized backend message; do not mask a `401` or `403` with an empty state.
- [A fix could expose the route to a common user] → Retain the existing role guard and test that the page is not rendered without administrator authority.
- [A pageable response shape differs from the established Spring envelope] → Reuse the current shared pagination model and validate against the published contract before altering DTOs.

## Migration Plan

1. Reproduce and trace the administrator Users route through route guard, page state and service request.
2. Apply the smallest repair at the responsible existing boundary and add focused regression tests.
3. Validate the administrator success, empty, error and unauthorized paths, then run the existing build/type checks.
4. Roll back by reverting the focused route/page/service repair; no persistence or backend migration is involved.
