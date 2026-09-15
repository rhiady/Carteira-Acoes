## Why

The backend has grown across authentication, portfolio management, market data, financial calculations, persistence, and external integrations, but there is no single end-to-end audit proving that every HTTP contract and critical rule matches the implemented behavior. This change establishes exhaustive, evidence-backed review and correction criteria before further API evolution.

## What Changes

- Inventory every production class and HTTP endpoint, tracing each route from controller and validation through domain, persistence, response, errors, and OpenAPI.
- Audit and correct user, login, broker, portfolio, position, stock, quote, history, purchase, sale, and operation contracts without introducing frontend-driven or parallel APIs.
- Verify pagination, sorting, authentication, ownership, status codes, standardized errors, response safety, and OpenAPI against runtime behavior.
- Mathematically verify all critical portfolio calculations, decimal precision, currencies, rounding, zero/reopened positions, costs, concurrency, and transaction atomicity with known-value tests.
- Audit every entity, repository, Flyway migration, transaction boundary, external client, quote/FX flow, and temporal field against PostgreSQL behavior.
- Review backend architecture, duplication, nullability, exceptions, logging, configuration, dead code, tests, startup, and smoke coverage; refactor only where a concrete correctness or maintainability benefit is demonstrated.
- Produce a severity-classified final audit report that links each finding to its impact, correction or recommendation, and verification evidence.
- Keep all implementation work strictly within the backend; frontend, Docker, Nginx, and deployment configuration remain unchanged.

## Capabilities

### New Capabilities

- `trading-financial-rules`: Defines auditable mathematical, currency, precision, concurrency, and atomicity rules for positions, purchases, sales, and financial indicators.
- `persistence-integrations`: Defines complete persistence-schema and external-integration review requirements, including PostgreSQL, Flyway, repositories, quotes, history, FX, failures, and transactions.
- `backend-quality-validation`: Defines exhaustive backend class review, security/configuration checks, test/build/startup/smoke validation, and the final severity-based report.

### Modified Capabilities

- `backend-api-contracts`: Expands the API contract requirements from selected shared conventions to an exhaustive endpoint inventory and end-to-end contract verification covering every existing backend route.

## Impact

- Potentially affects all backend controllers, DTOs, validators, services, entities, repositories, mappers, exception handling, configurations, external clients, Flyway additions, tests, and OpenAPI annotations.
- Existing public behavior is preserved unless evidence shows a defect or inconsistency; any externally observable correction must be documented and tested.
- No Angular, Docker, Nginx, or deployment files may be changed. Deployment configuration may only be read to validate compatibility with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.
