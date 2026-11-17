## Purpose

Provide a private account area for common users, isolate administrator resources from their experience, and support safe self-service password changes.

## Requirements

### Requirement: Common-user navigation exposes only permitted resources
The `ROLE_USER` experience SHALL expose navigation for the person's own investments and `Minha conta` only. It MUST NOT render links, menus, buttons or route shortcuts for user management, broker management, stock maintenance, quote administration or other administrator-only operations.

#### Scenario: Common user opens the application shell
- **WHEN** a person with `ROLE_USER` opens any authenticated common-user route
- **THEN** the navigation contains investment links and `Minha conta`, and does not contain `Usuários`, `Corretoras`, administrative stock actions or management shortcuts

#### Scenario: Common user opens an administrative URL directly
- **WHEN** a person with `ROLE_USER` navigates directly to an administrator-only URL
- **THEN** the route is blocked before its administrative screen is rendered and the person receives an accessible access-denied state with a link to a permitted area

### Requirement: Minha conta shows only the authenticated person's account
The system SHALL provide a `Minha conta` route for `ROLE_USER` that displays only the authenticated identity returned by `/api/auth/me` and account actions applicable to that identity. It MUST NOT list, search, select or expose other users.

#### Scenario: Person opens Minha conta
- **WHEN** an authenticated common user opens `Minha conta`
- **THEN** the screen shows that person's login and account controls, without a user list or administrator management actions

#### Scenario: Person changes account data
- **WHEN** the person submits an allowed self-account change
- **THEN** the request is scoped to the authenticated identity and the UI presents the backend result without exposing another user's data

### Requirement: Common user can change their password through the published contract
The system SHALL provide a `Redefinir senha` action in `Minha conta` using `PUT /api/auth/senha` with a `{ password }` payload. The form SHALL collect a new password and confirmation, and MUST NOT display the current password or persist any password.

#### Scenario: Passwords do not match
- **WHEN** the new password and confirmation differ
- **THEN** submission is prevented and the mismatch is announced next to the confirmation field without sending a request

#### Scenario: Password change succeeds
- **WHEN** the endpoint accepts a valid password change
- **THEN** the form clears sensitive fields, confirms success without echoing the password and keeps the authenticated account context

#### Scenario: Backend rejects an invalid password
- **WHEN** `PUT /api/auth/senha` returns a validation error
- **THEN** the form shows the backend message without clearing the authenticated session

### Requirement: Account area is accessible and visually consistent
`Minha conta` and its password form SHALL use the existing Swiss/financial editorial system, have a clear left-aligned hierarchy, hairline rules, existing semantic theme tokens, visible focus, keyboard operation, responsive stacking and error/status text that does not rely only on color.

#### Scenario: Person uses the account area on mobile and dark theme
- **WHEN** the person navigates and submits the account form using keyboard controls at a narrow viewport in dark theme
- **THEN** labels, focus, errors, actions and surfaces remain readable and reachable without horizontal overflow
