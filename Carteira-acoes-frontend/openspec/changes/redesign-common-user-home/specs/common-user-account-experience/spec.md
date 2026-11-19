## MODIFIED Requirements

### Requirement: Common-user navigation exposes only permitted resources
The `ROLE_USER` experience SHALL expose primary investment navigation for `Início`, `Carteiras`, `Operações` and `Ações`, plus a distinct personal utility area containing the authenticated identity, `Minha conta` and `Sair`. It MUST NOT render a separate `/usuario` destination, duplicate `Visão geral` navigation, links for user management, broker management, stock maintenance, quote administration or other administrator-only operations.

#### Scenario: Common user opens the application shell
- **WHEN** a person with `ROLE_USER` opens any authenticated common-user route
- **THEN** the primary navigation contains the four investment destinations, the utility area contains `Minha conta` and `Sair`, and no administrator-only destination is rendered

#### Scenario: Common user opens an administrative URL directly
- **WHEN** a person with `ROLE_USER` navigates directly to an administrator-only URL
- **THEN** the route is blocked before its administrative screen is rendered and the person receives an accessible access-denied state with a link to `/inicio`

### Requirement: Minha conta shows only the authenticated person's account
The system SHALL provide `/minha-conta` for `ROLE_USER` as the canonical personal destination. It SHALL display only the authenticated identity returned by `/api/auth/me`, security controls applicable to that identity and a clearly separated session area with `Sair`. It MUST NOT list, search, select or expose other users.

#### Scenario: Person opens Minha conta
- **WHEN** an authenticated common user opens `Minha conta`
- **THEN** the screen shows that person's login, account identifier, password controls and session/logout action without a user list or administrator management actions

#### Scenario: Person changes account data
- **WHEN** the person submits an allowed self-account change
- **THEN** the request is scoped to the authenticated identity and the UI presents the backend result without exposing another user's data

#### Scenario: Person logs out from Minha conta
- **WHEN** the person activates `Sair` in the session area
- **THEN** the complete session and financial context are cleared and the application redirects to `/login`

### Requirement: Account area is accessible and visually consistent
`Minha conta` and its password form SHALL use the existing Swiss/financial editorial system with a task-oriented account composition, clear section headings without decorative numbering, existing semantic theme tokens, visible focus, keyboard operation, responsive stacking and error/status text that does not rely only on color. Identity, security and session actions SHALL remain visually distinct without being presented as repetitive equal-weight cards.

#### Scenario: Person uses the account area on mobile and dark theme
- **WHEN** the person navigates, submits the password form or logs out using keyboard controls at a narrow viewport in dark theme
- **THEN** identity, labels, focus, errors, actions and surfaces remain readable and reachable without horizontal overflow

