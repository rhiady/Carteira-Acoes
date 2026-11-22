## Context

See [proposal.md](proposal.md) and the delta specs. The router currently maps `/admin` directly to `/usuarios`, while the administrative navigation exposes both labels. This makes two visible navigation destinations lead to the same collection page. `UsuarioService` represents the published paginated user resource; an authenticated administrator response confirms that its projection contains only `id`, `login`, `papel` and `ativo`, so the user page must not assume profile fields such as name, email or timestamps.

The established boundaries remain: `ROLE_ADMIN` is derived from `AuthService`, `authGuard` and `roleGuard` protect routes, `UsuarioService` owns HTTP access, and shared UI-state components own generic presentation. The backend contract is authoritative: this frontend must not create a summary endpoint or synthesize administrative records.

## Goals / Non-Goals

**Goals:**

- Make `/admin` a real administrative entry route and keep `/usuarios` focused on its published collection.
- Make administrative user loading diagnose and present successful, empty, denied and failed requests predictably.
- Preserve lazy standalone routes, role guards, typed services, shared states and visual tokens.

**Non-Goals:**

- Add endpoints, alter roles, bypass backend authorization or fabricate administrative metrics.
- Duplicate the user table or user-management actions in the entry route.
- Render or edit name, email or registration dates from the paginated user resource when those fields are not returned by the published contract.
- Change common-user navigation, private portfolio ownership, user payloads or financial calculations.
- Change the public self-registration route `/cadastro`, its login/redirect flow, registration payload or authentication contract.

## Decisions

### Route `/admin` to the existing dedicated entry component

Replace the redirect with the existing lazy `AdminPage`, protected by the same administrator guard as management routes. Keep `/usuarios` as the separate user-management page and preserve its existing route and links.

This reuses the page that already models the entry context and avoids an unnecessary feature folder. Creating a dashboard or cloning the user list into it was rejected because no authoritative administrative summary contract exists.

### Retain the published paginated user resource as the only data source

Keep user retrieval in `UsuarioService`, with the existing typed page envelope and Bearer interceptor. Model the published listing projection as `id`, `login`, `papel` and `ativo`; render only those fields after a successful response and omit the incompatible edit action. Use `id,asc` for ordering because it is an accepted backend sort field while `login` is not. Validate its URL, pagination parameters and error normalization with HTTP tests.

This localizes the diagnosis at the service boundary and prevents components from inventing profile fields or fallback records. Client-side filtering, alternate ownership endpoints or an untyped retry path were rejected because they can mask authorization/contract failures.

### Preserve context across list states

The user page keeps its heading and administrative context while using the existing loading, empty and error components. Retry reloads the current requested page only after a retriable failure; a `403` remains an access-denied state that preserves session rather than an automatic logout.

This meets the existing safe error convention. Clearing the page into a blank result or silently redirecting on every failure was rejected because it hides the reason the collection is unavailable.

### Verify against an authenticated administrative session

Use a test administrator session and the running backend to exercise `/admin` and `/usuarios`; test successful, empty, failure and forbidden responses at service/page boundaries. Inspect the routes in both themes and responsive widths, including active navigation and focus.

## Risks / Trade-offs

- [The backend grants no user-list permission to the test administrator] → retain the access-denied state and document the required backend authority rather than weakening the guard or using fabricated data.
- [The route change changes bookmarked `/admin` behavior] → it remains an administrator-only route but now delivers the intended entry context; `/usuarios` remains stable for direct links.
- [A generic error hides an API contract mismatch] → assert request URL, query parameters and returned page metadata in service tests.
- [Navigation appears duplicated after the route change] → test active state and distinct headings for both destinations at desktop and mobile widths.

## Migration Plan

1. Add failing route, service and page tests for distinct administrative entry and user-list states.
2. Point `/admin` to `AdminPage`, retain guards/lazy loading, and refine entry links as needed.
3. Correct the user-list request/error handling and render only the published listing projection at the existing service/page boundary.
4. Run API-contract, route, page, AXE, theme and responsive checks with an administrator session.
5. Confirm `/cadastro` still resolves to the existing public registration flow, without a route, redirect, payload or contract change.
6. Roll back by restoring the former route mapping and page/service changes; no backend or persisted-data migration is required.
