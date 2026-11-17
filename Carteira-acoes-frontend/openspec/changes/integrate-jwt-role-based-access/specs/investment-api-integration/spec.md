## MODIFIED Requirements

### Requirement: Authentication uses the published frontend API contract
The application SHALL model the authentication request and response as typed frontend data, call `POST /api/auth/login` through the centrally configured API URL, store the returned Bearer token only for authenticated requests, and call `GET /api/auth/me` to obtain `login` and `authorities`. It SHALL preserve the existing safe error normalization policy and SHALL not persist the submitted password.

#### Scenario: Authentication response is accepted
- **WHEN** the endpoint returns `token`, `tipo` and `expiresIn`
- **THEN** the frontend stores the session token metadata, requests the authenticated identity, and exposes only the typed login and authority data needed for access decisions

#### Scenario: Authentication identity is accepted
- **WHEN** `GET /api/auth/me` returns `login` and a supported authority
- **THEN** the frontend establishes the session and chooses the corresponding administrative or common area

#### Scenario: Endpoint returns an API error message
- **WHEN** authentication fails with an API error payload
- **THEN** the login flow selects the credential-specific 401 message or the defined friendly fallback without exposing stack traces or persisting a partial session

### Requirement: User access consumes existing published resources
The frontend SHALL use the published authenticated resources with the Bearer token and SHALL use `POST /usuarios` for account creation and `GET /usuarios/{usuarioId}/carteiras` for common-user wallet listing when the current contract provides those resources. It SHALL preserve typed requests, the configured API base URL and normalized backend messages, and SHALL not invent `/auth/register` or another ownership endpoint.

#### Scenario: Authenticated request carries the token
- **WHEN** an authenticated person requests a protected resource
- **THEN** the request includes `Authorization: Bearer <token>` and the login request itself does not receive that header

#### Scenario: Authenticated wallet request
- **WHEN** the current authenticated user context identifies user `7` and the compatible resource is available
- **THEN** the frontend requests `/usuarios/7/carteiras` instead of loading all wallets and filtering in the component

#### Scenario: Account creation request
- **WHEN** a valid registration form is submitted
- **THEN** the frontend sends only `nome`, `email` and `senha` to `POST /usuarios`

### Requirement: User-scoped failures remain safe and actionable
The frontend SHALL present an API `message` when appropriate, map duplicate-account and ownership failures to friendly UI states, and SHALL not expose raw JSON, stack traces or Java exceptions. A `401` SHALL clear the session and redirect to `/login`; a `403` SHALL preserve the session and show an access-denied message.

#### Scenario: Expired session response
- **WHEN** a protected request returns HTTP 401
- **THEN** the token and authenticated context are cleared and the person is redirected to `/login`

#### Scenario: Insufficient authority response
- **WHEN** a protected request returns HTTP 403
- **THEN** the session remains active and the UI shows a friendly access-denied state with a permitted navigation path

#### Scenario: Ownership request fails
- **WHEN** a wallet or user-scoped resource returns an unavailable or denied response
- **THEN** the UI shows a friendly unavailable state and preserves the authenticated context
