## 1. Administrative user access

- [x] 1.1 Trace and correct the protected `/usuarios` route, user-list request and page states so an authenticated administrator can list users and retry failures.
- [x] 1.2 Verify that user-management navigation stays available only to `ROLE_ADMIN` and retains accessible loading, empty and error states.

## 2. Global action catalog

- [x] 2.1 Add an administrator-only create/import action form to the existing action catalog with ticker and market validation, processing and backend feedback.
- [x] 2.2 Submit only `{ ticker, mercado }` to `POST /acoes`, incorporate the returned global action into the catalog and never create a carteira, posição or operação.

## 3. Verification

- [x] 3.1 Add focused route/page tests for user-list rendering, error/retry state and role protection.
- [x] 3.2 Add focused action-catalog tests for form validation, the exact `POST /acoes` body and no carteira/posição/operação side effect.
- [x] 3.3 Run focused tests, full TypeScript/build checks, relevant AXE checks and `openspec validate --type change enable-admin-stock-catalog-and-users`.
