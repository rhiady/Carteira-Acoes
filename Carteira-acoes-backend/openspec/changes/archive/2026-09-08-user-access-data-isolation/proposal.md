## Why

The backend already supports email/password login, but authenticated identity and ownership enforcement must be made reliable across financial resources. Without consistent backend checks, manipulating user, portfolio, position, or operation IDs could expose or mutate another user's data.

## What Changes

- Preserve and verify the existing email/password login and user-registration contracts.
- Ensure passwords are encoded at creation and update, and never exposed in responses or logs.
- Establish a simple authenticated-user context using the existing authentication mechanism.
- Enforce portfolio ownership for listing, reading, creating, and all derived financial data.
- Enforce ownership before position and operation reads, purchases, and sales, with no partial persistence on denial.
- Reject duplicate email registration using the project's existing error format.
- Add backend tests for valid/invalid login, registration, ID manipulation, ownership, and atomic purchase/sale denial.
- Update affected OpenAPI documentation without changing frontend or deployment files.

## Capabilities

### New Capabilities

- `user-access-data-isolation`: Authenticated user access, credential handling, and ownership isolation for portfolios and financial operations.

### Modified Capabilities

<!-- No existing main capability specs are present in this repository. -->

## Impact

- Affects authentication, user, portfolio, position, operation, purchase, and sale services/controllers/repositories and their tests.
- May refine request/response and error documentation in OpenAPI while preserving existing public contracts where possible.
- Does not affect the Angular frontend, `deploy`, Docker Compose, Nginx, or frontend Dockerfile.
