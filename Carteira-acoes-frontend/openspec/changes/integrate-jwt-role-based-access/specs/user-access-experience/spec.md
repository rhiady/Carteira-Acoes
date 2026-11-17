## MODIFIED Requirements

### Requirement: Authenticated identity scopes application data
The application SHALL use the authenticated `login` and authorities returned by `/api/auth/me` as its identity and access context. User-owned wallet listings SHALL query the user-scoped endpoint when available, and Dashboard, wallets, wallet details, purchase, sale, operations and analytics SHALL not present another user's data through global user selection or client-side filtering. Administrative screens SHALL not substitute for user ownership and SHALL be limited to the administrative resources authorized by the backend.

#### Scenario: Common user opens wallets
- **WHEN** a person authenticated with `ROLE_USER` opens the wallet list
- **THEN** the frontend requests the compatible user-scoped wallet resource and renders only that response

#### Scenario: Authenticated person opens wallets
- **WHEN** a person with authenticated user context opens the wallet list
- **THEN** the frontend requests the user-scoped wallet resource and renders only that response

#### Scenario: Financial flow opens
- **WHEN** the person starts a purchase, sale, operation history or analytics flow
- **THEN** available wallets and positions are limited to the authenticated person's context and no redundant user selector is presented

#### Scenario: Administrator opens the administrative area
- **WHEN** a person authenticated with `ROLE_ADMIN` opens the administrative area
- **THEN** the frontend presents only the published administrative management flows and does not silently load another person's private wallet context

### Requirement: Logout clears the complete user context
Logout SHALL clear the token, authenticated identity and authorities, selected wallet, filters, in-memory financial data and related local persistence before redirecting to `/login`. A later login by a different person SHALL not show data from the previous person.

#### Scenario: Two people use the same browser
- **WHEN** Ana logs out and João logs in afterward
- **THEN** João sees only his permitted area and own wallets, positions, operations and analytics, with no previous session, selection or filter retained
