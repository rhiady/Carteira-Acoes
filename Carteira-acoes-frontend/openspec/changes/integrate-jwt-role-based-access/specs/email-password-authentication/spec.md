## MODIFIED Requirements

### Requirement: Login request and processing state
The system SHALL send `POST /api/auth/login` with `{ "login": "...", "password": "..." }` using the configured API base URL. While the request is pending, the CTA SHALL indicate `Entrando…`, be disabled, and prevent duplicate submissions. On success, the system SHALL store the returned Bearer token only in the session mechanism defined by the application and SHALL call `GET /api/auth/me` to obtain the authenticated login and authorities.

#### Scenario: Valid credentials
- **WHEN** the backend accepts the submitted credentials and returns `token`, `tipo` and `expiresIn`
- **THEN** the frontend stores the token with its expiry metadata, loads `/api/auth/me`, and redirects to the permitted role area without persisting the password

#### Scenario: Invalid credentials
- **WHEN** the backend returns HTTP 401 with `CREDENCIAIS_INVALIDAS`
- **THEN** the form keeps the entered login and shows `Email ou senha inválidos.` near the form without storing a token

#### Scenario: Unexpected login failure
- **WHEN** the login request or authenticated-identity request fails without an authentication-specific response
- **THEN** the frontend shows `Não foi possível entrar. Tente novamente.` without technical details and does not establish a session

### Requirement: Local authenticated user and logout
The system SHALL expose authenticated session state containing the token needed for requests, its expiry information, login and authorities, and SHALL never store the password or expose the token as user-facing identity data. A `Sair` action SHALL remove the token and authenticated identity, clear the in-memory financial context, and redirect to `/login`.

#### Scenario: Refresh with an unexpired stored token
- **WHEN** the application starts with a stored unexpired token
- **THEN** the application calls `GET /api/auth/me`, restores the identity and authorities when valid, and routes to the permitted area

#### Scenario: Refresh with a stored user
- **WHEN** the application starts with valid stored authenticated session data
- **THEN** the authenticated-user state is restored only after the stored token is validated through `/api/auth/me`, without another login request

#### Scenario: Stored token is expired or rejected
- **WHEN** the token expiry is reached or `/api/auth/me` returns HTTP 401
- **THEN** the application clears the complete session and redirects to `/login`

#### Scenario: Person logs out
- **WHEN** the person activates `Sair`
- **THEN** the token, identity, selected wallet, filters and in-memory financial data are removed and the person is redirected to `/login`

### Requirement: Private navigation follows local authentication state
The system SHALL prevent navigation to protected routes when no valid authenticated session exists and SHALL redirect that navigation to `/login`. Routes requiring `ROLE_ADMIN` SHALL additionally require that authority. These frontend guards SHALL NOT be treated as backend security.

#### Scenario: Anonymous navigation
- **WHEN** an unauthenticated person navigates to a protected application route
- **THEN** navigation is redirected to `/login`

#### Scenario: Authenticated common user navigates to an administrative route
- **WHEN** a person with `ROLE_USER` navigates to a route requiring `ROLE_ADMIN`
- **THEN** navigation is blocked and the person is directed to an accessible access-denied state or permitted common area
