## Why

The application needs a cohesive investment-portfolio experience instead of disconnected administrative screens. The frontend must expose every published Spring Boot workflow with a calm financial-product interface while preserving the backend as the authority for data and business rules.

## What Changes

- Establish the Angular application structure, Material-based theme, responsive shell, route layout, shared feedback components, and global HTTP error handling.
- Define strict TypeScript DTOs, centralized API configuration, paginated HTTP access, date/currency formatting, and API-error presentation for the published backend contract.
- Deliver user, broker, portfolio, stock, and operation flows: create/list/detail where available; broker lookup; quote refresh; purchase; sale; and per-portfolio history.
- Add a conservative dashboard assembled only from available endpoints, with empty states and no invented valuations or authentication.
- Require accessible, responsive loading, empty, error, confirmation, and post-operation refresh behavior across the investment flows.
- Explicitly exclude fictional authentication, edit/delete commands, direct stock creation, invented dashboard endpoints, and client-side financial authority.

## Capabilities

### New Capabilities

_None._

### Modified Capabilities

- `investment-api-integration`: Expand the API-contract requirements for all published resources, request validation, pagination defaults, quote refresh, and operation post-success synchronization.
- `modern-ui-experience`: Specify the financial visual system, application shell, dashboard constraints, responsive Material patterns, and accessible contextual actions.

## Impact

Affected areas include Angular routes, layout, feature pages and dialogs, shared UI, DTOs, API services, interceptor, and application styles. The only integration target is the existing backend at `http://localhost:8080`; no backend endpoint or persisted-data contract changes are introduced. Angular Material is the principal component dependency and is visually customized with CSS.
