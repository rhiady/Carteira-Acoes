## 1. Schema migration

- [x] 1.1 Add a forward-only Flyway migration that removes `NOT NULL` constraints from every non-essential business column of `corretora`, while retaining `cnpj`, `razao_social`, audit columns, primary key, and CNPJ uniqueness constraints.
- [x] 1.2 Verify the new migration applies to the H2 test database and remains compatible with PostgreSQL.

## 2. Broker registration handling

- [x] 2.1 Update the broker-registration mapping so all provider fields other than razão social are normalized as optional, including address and CVM-detail fields.
- [x] 2.2 Keep CNPJ and non-blank razão social validation explicit and return the existing standardized validation error without persisting an incomplete broker.
- [x] 2.3 Ensure broker response mapping preserves absent optional values as `null`.

## 3. Regression coverage

- [x] 3.1 Add service and/or resource tests proving a CVM-validated broker with only CNPJ and razão social persists successfully and returns null optional fields.
- [x] 3.2 Add a test proving a missing or blank razão social is rejected and nothing is persisted.
- [x] 3.3 Run the Flyway-backed test suite and the complete Maven test suite successfully.
