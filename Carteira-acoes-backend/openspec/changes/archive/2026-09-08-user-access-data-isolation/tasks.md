## 1. Inspect existing contracts

- [x] 1.1 Inspect the existing login endpoint, request/response DTOs, authentication mechanism, password encoder, and exception handling.
- [x] 1.2 Map every controller/service/repository path using `usuarioId`, `carteiraId`, or `operacaoId`, including positions, purchases, sales, summaries, and global listings.
- [x] 1.3 Confirm current user registration/update behavior, duplicate-email handling, and whether any persisted passwords require compatibility treatment.

## 2. Credential safety and authenticated identity

- [x] 2.1 Preserve the existing email/password login contract and ensure invalid/structurally invalid credentials produce the established 401/400 responses without account enumeration.
- [x] 2.2 Ensure registration and user update encode new passwords, preserve an omitted password on update, and exclude password/hash fields from every response and log.
- [x] 2.3 Ensure duplicate email registration is rejected with the existing 409/error convention.
- [x] 2.4 Implement or reuse a small service-level resolution of the authenticated user from the current authentication context.

## 3. Ownership enforcement

- [x] 3.1 Centralize portfolio ownership lookup/guard and define the consistent not-found/denied behavior for third-party resources.
- [x] 3.2 Restrict user-scoped portfolio listing to the authenticated user regardless of the requested `usuarioId`.
- [x] 3.3 Prevent portfolio creation from assigning ownership based on a conflicting client-supplied `usuarioId`.
- [x] 3.4 Protect portfolio-by-ID and any global portfolio listing from exposing another user's portfolios.
- [x] 3.5 Apply the ownership guard to portfolio-derived summaries, analytics, and other financial data endpoints.

## 4. Positions and operations

- [x] 4.1 Validate portfolio ownership before returning positions and reject ID manipulation without returning third-party data.
- [x] 4.2 Validate portfolio ownership before listing operations and validate indirect ownership for operation-by-ID reads.
- [x] 4.3 Preserve existing domain validation and error responses while ensuring known access failures do not become 500 responses.

## 5. Purchase and sale protection

- [x] 5.1 Add the ownership check before any purchase write and keep the purchase flow transactional.
- [x] 5.2 Add the ownership check before any sale write and keep the sale flow transactional.
- [x] 5.3 Verify denied purchase and sale attempts leave operation, position, and related financial state unchanged. Ownership is checked before position/operation writes and the methods are transactional.

## 6. Verification and documentation

- [x] 6.1 Existing authentication and catalog integration tests cover valid/invalid login, registration, duplicate email, password update, and password omission from responses.
- [x] 6.2 Add integration coverage for own/third-party portfolio access, manipulated `usuarioId`, manipulated `carteiraId`, and global listings.
- [x] 6.3 Add integration coverage for third-party positions, operations, purchase denial, and sale denial with no partial persistence.
- [x] 6.4 Update affected OpenAPI annotations/documentation and error responses without changing frontend or deployment artifacts.
- [x] 6.5 Run `./mvnw test` and build the backend successfully; validate the runtime PostgreSQL container with a read-only query. HTTP smoke testing from this sandbox is unavailable because socket creation is restricted.
- [x] 6.6 Confirm the change implementation touched only backend Java code and OpenAPI annotations; no Angular, `deploy`, Docker Compose, Nginx, or frontend Dockerfile files were edited by this change.
