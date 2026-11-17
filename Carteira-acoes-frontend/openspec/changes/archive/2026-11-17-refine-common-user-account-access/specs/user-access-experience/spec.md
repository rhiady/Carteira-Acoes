## MODIFIED Requirements

### Requirement: Authenticated identity scopes application data
The application SHALL use the authenticated user's `id`, `nome` and `email` as its sole frontend identity context. User-owned wallet listings SHALL query the user-scoped endpoint when available, and Dashboard, wallets, wallet details, purchase, sale, operations, analytics and `Minha conta` SHALL not present another user's data through global user selection, administrative navigation or client-side filtering. Administrative resources SHALL remain outside the common-user experience and SHALL be protected by role-aware routes.

#### Scenario: Authenticated person opens wallets
- **WHEN** a person with authenticated user id `7` opens the wallet list
- **THEN** the frontend requests the user-scoped wallet resource for user `7` and renders only that response

#### Scenario: Financial flow opens
- **WHEN** the person starts a purchase, sale, operation history or analytics flow
- **THEN** available wallets and positions are limited to the authenticated person's context and no redundant user selector is presented

#### Scenario: Common user opens an administrative resource
- **WHEN** a person with `ROLE_USER` attempts to open users, brokers or administrator-only maintenance
- **THEN** the navigation and route guard prevent the administrative experience from being rendered and preserve the person's own investment context
