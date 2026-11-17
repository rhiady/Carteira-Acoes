## Context

See `proposal.md` for motivation and the delta specs for the observable contract. The completed role-based access change already has `/admin` and `/usuario`, role guards, and a global shell that switches navigation by authority. The common-user shell now needs a dedicated account destination and must consume the backend's published self-service password operation.

Frontend-design direction: retain the existing **Swiss** anchor because this is a dense financial product and the current interface already uses its neutral surfaces, sans typography, restrained blue, red rule and hairline grid. The differentiator is a compact account rail: identity at the top, account actions below, and no management catalog masquerading as user navigation.

## Goals / Non-Goals

**Goals:**

- Make the common-user shell contain only investment navigation and `Minha conta`.
- Add route-level defense in depth so direct administrator URLs cannot render for `ROLE_USER`.
- Reuse the authenticated `/api/auth/me` identity to render the account screen without loading a global user list.
- Define the password form, validation, privacy and accessibility contract while integrating only a backend endpoint that is explicitly published.
- Keep the Swiss visual system in light/dark themes with visible focus, 1px rules, left-aligned hierarchy and responsive stacking.

**Non-Goals:**

- No frontend-only password reset, password recovery email, token decoding or local password storage.
- No reuse of administrator `PUT /usuarios/{id}` for common users while the backend classifies `/api/usuarios/**` as administrative.
- No changes to portfolio, quote, operation or financial calculation behavior.
- No user directory, global user search or admin navigation in the common-user area.

## Decisions

### Make `Minha conta` a role-user route and keep management routes admin-only

Add a lazy route protected by the existing authenticated and `ROLE_USER` guards. Keep administrator routes protected independently even when a link is hidden. This addresses both accidental navigation and direct URL access; hiding links alone was rejected because it is not a security boundary.

### Use the existing authenticated identity, not `UsuarioService.list`

Render `id` and `login` from `AuthService.currentUser()` and avoid a request for the user directory. This keeps the account screen scoped to the session and prevents other users from appearing. Loading a global user page and filtering client-side was rejected because it exposes unnecessary data and violates the ownership contract.

### Use the published self-service password contract

Use `PUT /api/auth/senha` with Bearer authentication and `{ password }`, validated from 8 to 72 characters and returning `204 No Content`. The existing admin `PUT /usuarios/{id}` cannot be used as a substitute. The UI keeps confirmation validation local and displays normalized backend errors without persisting password data.

### Use a compact Swiss account rail

Use existing semantic surface, text, border and focus tokens; one blue primary action; red only for the existing rule/error semantics; 1px separators; no cards full of fabricated profile metadata. The account page will show real identity fields and standard labels such as `Minha conta`, `Redefinir senha`, `Salvar` and `Cancelar`.

### Keep administrator editing separate

Leave the existing Users-area edit dialog as an administrator-only capability. Do not add a user selector or adapt its global `nome/email/senha` form for self-service, because account ownership, endpoint authorization and error semantics differ.

## Risks / Trade-offs

- [The backend contract changes] → Keep the route and payload typed in `AuthService` and update the change before altering the request shape; never call an admin route from `ROLE_USER`.
- [A hidden link can be reached by URL] → Apply role metadata and guards to every administrator route, and test direct navigation with a common-user authority.
- [Existing shell code can regress when navigation is split] → Reuse the current role-aware shell and add route/component tests for both role navigation sets.
- [Password validation can leak sensitive state] → Keep fields write-only, never log values, clear them after success, and avoid putting password data into route state, storage or error text.
- [Dark theme or narrow layouts reduce readability] → Reuse existing semantic tokens, visible focus styles and single-column mobile layout; validate AXE and keyboard behavior in both themes.

## Migration Plan

1. Add the common-user account route, replace common-user management links with `Minha conta`, and preserve administrator links only for `ROLE_ADMIN`.
2. Add account identity rendering and password-form validation against the published contract.
3. Add the typed self-service call, success/error handling and focused tests.
4. Run route, component, form, AXE, OpenSpec and build checks. Roll back by reverting the account route, shell navigation and related tests; no data migration is required.
