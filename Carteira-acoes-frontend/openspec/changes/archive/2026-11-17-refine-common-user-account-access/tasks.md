## 1. Common-user route and navigation isolation

- [x] 1.1 Audit all common-user shell links and actions against the published role contract, removing users, brokers, maintenance and administrator-only quote controls from `ROLE_USER` navigation.
- [x] 1.2 Add a lazy `Minha conta` route protected by authentication and `ROLE_USER`, while keeping every administrator route protected by `ROLE_ADMIN`.
- [x] 1.3 Add direct-navigation tests proving a common user cannot render user lists, broker management, stock maintenance or edit-user screens, and that the administrator navigation remains available to `ROLE_ADMIN`.

## 2. Minha conta experience

- [x] 2.1 Create the account page using only the authenticated `/api/auth/me` identity, with login/account context and no global user lookup, selector or fabricated profile fields.
- [x] 2.2 Add `Minha conta` to the common-user navigation and remove duplicated management links from the common-user shell without changing the administrator shell.
- [x] 2.3 Apply the existing Swiss direction: left-aligned hierarchy, current semantic tokens, hairline rules, one primary action, visible focus, dark-theme compatibility and single-column mobile layout.

## 3. Password-change contract and form

- [x] 3.1 Confirm the backend publishes `PUT /api/auth/senha` with Bearer authentication, `{ password }`, `204 No Content`, and 8–72 character validation; do not use administrator-only `PUT /api/usuarios/{id}` as a substitute.
- [x] 3.2 Add typed password-change models/service integration aligned with `PUT /api/auth/senha` and its `{ password }` payload.
- [x] 3.3 Implement `Redefinir senha` with new/confirmation fields required by the contract, matching validation, duplicate-submit prevention, safe backend errors and clearing of sensitive fields after success.
- [x] 3.4 Keep the implementation contract-bound: no guessed route or administrator fallback is sent.

## 4. Validation and accessibility

- [x] 4.1 Add component tests for account identity scoping, absence of other users, password validation, loading/success/error states and session preservation.
- [x] 4.2 Add AXE and keyboard coverage for `Minha conta` and the password form in light/dark themes, including narrow responsive layout and visible focus.
- [x] 4.3 Run focused Angular/Vitest tests, TypeScript/build checks and strict OpenSpec validation; responsive visual verification remains environment-dependent.
