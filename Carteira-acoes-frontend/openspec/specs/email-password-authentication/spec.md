# email-password-authentication Specification

## Purpose

Define a minimal frontend authentication flow that lets a person enter the investment application with email and password while keeping only non-sensitive user identity data locally.

## Requirements

### Requirement: Login accepts only email and password
The system SHALL expose a `/login` route with a form containing only email and password fields and the `Entrar` action. Email SHALL be required and valid, and password SHALL be required; the password SHALL be hidden by default and the form SHALL support keyboard submission.

#### Scenario: Incomplete or invalid form
- **WHEN** the person submits without an email, with an invalid email, or without a password
- **THEN** the request is not sent and the corresponding field presents an understandable validation message

#### Scenario: Password visibility is toggled
- **WHEN** the person activates the password visibility control
- **THEN** the password changes between hidden and visible presentation, the control has an accessible name, and the entered value is preserved

### Requirement: Login request and processing state
The system SHALL send `POST /auth/login` with `{ "email": "...", "senha": "..." }` using the configured API base URL. While the request is pending, the CTA SHALL indicate `Entrando…`, be disabled, and prevent duplicate submissions.

#### Scenario: Valid credentials
- **WHEN** the backend accepts the submitted credentials
- **THEN** the frontend stores only `id`, `nome`, and `email` and redirects the person to `/inicio`

#### Scenario: Invalid credentials
- **WHEN** the backend returns HTTP 401
- **THEN** the form keeps the entered email and shows `Email ou senha inválidos.` near the form

#### Scenario: Unexpected login failure
- **WHEN** the login request fails without an authentication-specific response
- **THEN** the frontend shows `Não foi possível entrar. Tente novamente.` without technical details

### Requirement: Local authenticated user and logout
The system SHALL expose a simple authenticated-user state that can be restored after refresh from local storage and SHALL never store the password, password hash, token, or other secret. A `Sair` action SHALL remove the stored user, clear the in-memory state, and redirect to `/login`.

#### Scenario: Refresh with a stored user
- **WHEN** the application starts with valid stored `id`, `nome`, and `email`
- **THEN** the authenticated-user state is restored without another login request

#### Scenario: Person logs out
- **WHEN** the person activates `Sair`
- **THEN** local user data is removed and the person is redirected to `/login`

### Requirement: Private navigation follows local authentication state
The system SHALL prevent navigation to the main application routes when no local authenticated user exists and SHALL redirect that navigation to `/login`. This navigation guard is only a frontend flow aid and SHALL NOT be treated as backend security.

#### Scenario: Anonymous navigation
- **WHEN** an unauthenticated person navigates to a protected application route
- **THEN** navigation is redirected to `/login`
