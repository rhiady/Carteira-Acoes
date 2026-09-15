## Context

See `proposal.md` and the `broker-validation` delta spec. The `corretora` table currently declares address fields as `NOT NULL`, while the service maps values directly from the CNPJ provider. The application uses Flyway against PostgreSQL in production and H2 in tests.

## Goals / Non-Goals

**Goals:**

- Make all business fields of `corretora`, except `cnpj` and `razao_social`, nullable in both supported database engines.
- Preserve the CNPJ uniqueness constraint, system-managed audit timestamps, and the CNPJ/CVM validation flow.
- Fail before persistence when the provider does not supply a non-blank razão social.

**Non-Goals:**

- Removing CNPJ or CVM validation.
- Changing required fields in other tables, foreign-key constraints, or audit columns.
- Backfilling missing information for existing corretoras.

## Decisions

### Add a forward-only Flyway migration

Create a new migration that drops `NOT NULL` from the existing optional business columns rather than editing `V2__create_table_corretora.sql`. This preserves schema-history integrity for already deployed databases and applies safely to fresh and existing environments.

Alternative considered: edit the original migration. Rejected because Flyway validates previously applied migrations and production databases would retain the old constraints.

### Keep the required-field boundary in the application and database

Keep `cnpj` and `razao_social` non-null at the database level, normalize provider values before mapping, and explicitly reject a missing or blank razão social with the existing standardized validation-error mechanism. Treat all remaining provider fields as nullable when mapping and serializing responses.

Alternative considered: rely only on the database constraint. Rejected because it produces a less useful persistence error and may attempt a partial write after external validation.

### Preserve system fields and external validation constraints

`created_at` and `updated_at` remain mandatory system-managed fields; “only CNPJ and nome” applies to business registration fields. The CVM record and CNPJ source remain required validations even though their optional details no longer block persistence.

## Risks / Trade-offs

- [Clients assume non-null address fields] → Document the breaking response contract and add tests for null-valued response fields.
- [A provider returns whitespace-only values] → Trim and validate razão social before persisting; map other blank optional values to absent values consistently.
- [Database syntax diverges between H2 and PostgreSQL] → Validate the Flyway migration through the existing integration test profile and a PostgreSQL-compatible migration syntax.

## Migration Plan

1. Add and apply the Flyway migration that relaxes non-essential `corretora` columns.
2. Update the service/model mapping and tests for partial provider data and missing razão social.
3. Run migration-backed integration tests and the full suite.

Rollback is a new forward migration that restores `NOT NULL` only after any existing null values have been remediated; a direct rollback is unsafe while nulls are present.
