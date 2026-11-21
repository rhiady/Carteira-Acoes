## MODIFIED Requirements

### Requirement: Authenticated identity scopes application data
The application SHALL use the authenticated user's `id`, `nome` and `email` as its sole frontend identity context. A common user's wallet listings SHALL query only that person's user-scoped resource, and Dashboard, wallets, wallet details, purchase, sale, operations, analytics and `Minha conta` SHALL not present another user's data through global user selection, administrative navigation or client-side filtering. An administrator SHALL access the protected user-management listing through the published administrative resource. Administrative resources SHALL remain outside the common-user experience and SHALL be protected by role-aware routes.

#### Scenario: Authenticated person opens wallets
- **WHEN** a common user with authenticated user id `7` opens the wallet list
- **THEN** the frontend requests the user-scoped wallet resource for user `7` and renders only that response

#### Scenario: Administrator opens user management
- **WHEN** an administrator opens the protected user-management route
- **THEN** the frontend renders the published user listing or its accessible loading, empty or error state

#### Scenario: Financial flow opens
- **WHEN** a common user starts a purchase, sale, operation history or analytics flow
- **THEN** available wallets and positions are limited to the authenticated person's context and no redundant user selector is presented

#### Scenario: Common user opens an administrative resource
- **WHEN** a person with `ROLE_USER` attempts to open users, brokers or administrator-only maintenance
- **THEN** the navigation and route guard prevent the administrative experience from being rendered and preserve the person's own investment context
