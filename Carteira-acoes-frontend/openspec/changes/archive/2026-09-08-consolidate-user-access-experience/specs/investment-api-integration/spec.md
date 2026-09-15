## ADDED Requirements

### Requirement: User access consumes existing published resources
The frontend SHALL use `POST /usuarios` for account creation and `GET /usuarios/{usuarioId}/carteiras` for authenticated wallet listing when the current contract provides those resources. It SHALL preserve typed requests, the configured API base URL and normalized backend messages, and SHALL not invent `/auth/register` or another ownership endpoint.

#### Scenario: Account creation request
- **WHEN** a valid registration form is submitted
- **THEN** the frontend sends only `nome`, `email` and `senha` to `POST /usuarios`

#### Scenario: Authenticated wallet request
- **WHEN** the current user has id `7` and the wallet screen loads
- **THEN** the frontend requests `/usuarios/7/carteiras` instead of loading all wallets and filtering in the component

### Requirement: User-scoped failures remain safe and actionable
The frontend SHALL present an API `message` when appropriate, map duplicate-account and ownership failures to friendly UI states, and SHALL not expose raw JSON, stack traces or Java exceptions.

#### Scenario: Ownership request fails
- **WHEN** a wallet or user-scoped resource returns an unavailable or denied response
- **THEN** the UI shows a friendly unavailable state and preserves the authenticated context
