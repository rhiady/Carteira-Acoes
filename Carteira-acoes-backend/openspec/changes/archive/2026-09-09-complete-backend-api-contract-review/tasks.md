## 1. Baseline and complete inventory

- [x] 1.1 Capture the initial `git status`, unstaged/staged diffs, test result, package result, and runtime configuration without modifying unrelated worktree changes.
- [x] 1.2 Inventory every production and test package/class by responsibility and reconcile the inventory against `rg --files` so no class is omitted.
- [x] 1.3 Inventory every controller mapping, including methods, paths, path/query parameters, bodies, success statuses, authentication, ownership, pagination, and sorting.
- [x] 1.4 Build the endpoint matrix linking controller → request DTO/validation → service/domain → repository/entity → response DTO → HTTP/error handler → OpenAPI → tests.
- [x] 1.5 Inventory all DTOs, validators, mappers, exceptions/handlers, configs, clients, repositories, entities, and Flyway migrations with counts and review status.

## 2. HTTP contracts and shared behavior

- [x] 2.1 Audit and correct all user and login routes, including password encoding/omission, optional password update, duplicate email, equivalent invalid credentials, session/authentication behavior, statuses, and OpenAPI.
- [x] 2.2 Audit and correct all broker routes, CNPJ handling, external validation, pagination, statuses, errors, response fields, and OpenAPI.
- [x] 2.3 Audit and correct all portfolio routes, owner assignment, global/user-scoped listings, resource lookup, pagination, errors, ownership, and OpenAPI.
- [x] 2.4 Audit and correct position and portfolio-summary routes field by field, documenting origin, type, nullability, currency, precision, formula, sorting, pagination, and ownership.
- [x] 2.5 Audit and correct stock, individual/batch quote, and quote-history routes, including filters, pagination, sorting, timestamps, partial failure, errors, and OpenAPI.
- [x] 2.6 Audit and correct purchase, sale, operation-by-ID, and portfolio-history routes, including identifier exclusivity, validation, statuses, atomicity, ownership, response fields, pagination, and OpenAPI.
- [x] 2.7 Verify every discovered route not explicitly listed in the proposal and add it to the endpoint matrix, tests, and OpenAPI review.
- [x] 2.8 Normalize known exception mappings and the standard error body without exposing stack traces, SQL, credentials, hashes, secrets, or internal details.
- [x] 2.9 Verify all paginated endpoints use coherent defaults/metadata and apply validated public sorting before pagination; add regression tests for invalid page, size, field, and direction.

## 3. Trading and financial rules

- [x] 3.1 Map every critical formula to operands, source, unit, currency, precision, scale, rounding, persistence type, and response field; confirm no critical calculation uses float/double.
- [x] 3.2 Verify first and successive purchases produce weighted acquisition cost using known values 10@30, 10@40, and 5@50.
- [x] 3.3 Verify partial sale preserves remaining acquisition cost, total sale leaves a consistent zero position/history, and oversell is rejected without partial state.
- [x] 3.4 Verify realized result for profit/loss and multiple sales accumulates chronologically rather than using only the latest sale.
- [x] 3.5 Verify weighted `precoMedioVenda`, including multiple sales and `null` when no sale exists.
- [x] 3.6 Verify current value, unrealized result, and total result for above/below/equal/missing quotes and zero positions.
- [x] 3.7 Verify purchase after partial sale and position reopening after total sale preserve historical realized results and calculate the new open cost correctly.
- [x] 3.8 Verify operation costs are included only according to existing domain rules and are documented consistently in gross/net and profit calculations.
- [x] 3.9 Verify BRL/USD separation and USD/BRL response conversion with an authoritative rate and timestamp, without implicit cross-currency sums.
- [x] 3.10 Verify transactional rollback and optimistic/concurrency behavior so concurrent incompatible operations cannot create lost updates or negative positions.

## 4. Persistence and integrations

- [x] 4.1 Audit every entity field and relationship against the final Flyway schema for Java/PostgreSQL type, nullability, precision/scale, cascade, fetch, uniqueness, indexes, and timestamps.
- [x] 4.2 Audit every repository query for joins, ownership filters, pagination, sorting, duplicate results, concurrency, and demonstrated N+1 behavior.
- [x] 4.3 Apply any schema correction exclusively through new Flyway migrations and verify migration ordering and compatibility with existing data.
- [x] 4.4 Recreate/validate the schema on PostgreSQL and compare critical behavior with H2 tests to identify dialect-dependent assumptions.
- [x] 4.5 Audit every external client and adapter for timeout, network/status failures, empty/malformed payloads, missing/invalid fields, logging, and exception translation.
- [x] 4.6 Verify failed stock/quote/FX calls preserve the prior local state and never persist fabricated or partial data.
- [x] 4.7 Verify individual individual and batch quote updates persist valid price/timestamp/history correctly and isolate partial batch failures.
- [x] 4.8 Verify quote-history ordering, inclusive date filters, precision, pagination, duplicates, and empty-result behavior.
- [x] 4.9 Audit date/time types and conversions across clients, entities, services, database, and API for consistent UTC/offset semantics.

## 5. Architecture, quality, and security

- [x] 5.1 Review controller/service/repository responsibility boundaries and refactor only concrete violations that improve correctness, testability, reuse, or atomicity.
- [x] 5.2 Review significant duplication, complex methods, dead code, stale DTOs/imports, and null handling; change only findings with verified benefit and usage evidence.
- [x] 5.3 Audit security for authentication, ownership, IDOR, mass assignment, input validation, SQL injection exposure, sensitive responses/logs, and hardcoded secrets.
- [x] 5.4 Audit datasource, JPA, Flyway, logging, Swagger, H2, profiles, environment variables, and Open Session in View while preserving deploy compatibility.
- [x] 5.5 Review existing test coverage by controller, service, repository, financial rule, exception, ownership, and integration; add focused regressions for every corrected defect.

## 6. Final verification and report

- [x] 6.1 Run focused tests after each correction and then execute the complete `./mvnw test` suite successfully.
- [x] 6.2 Execute `./mvnw clean package` successfully.
- [x] 6.3 Start the backend with valid PostgreSQL configuration and verify Spring, Flyway, datasource, and startup logs.
- [x] 6.4 Execute the endpoint smoke matrix covering success, validation, not found, conflict, authentication/ownership, pagination, and sorting as applicable.
- [x] 6.5 Reconcile the endpoint/class/entity/repository/client/migration inventories and confirm every item has review evidence.
- [x] 6.6 Produce the final CRITICAL/HIGH/MEDIUM/LOW/INFO report with file/class, issue, impact, correction or recommendation, and associated test/evidence.
- [x] 6.7 Review `git status`, `git diff`, and `git diff --cached`; confirm no frontend, Docker, Nginx, deploy, or unrelated worktree files were changed or staged by this change.
