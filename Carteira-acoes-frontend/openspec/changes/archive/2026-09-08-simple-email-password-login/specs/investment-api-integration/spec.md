## ADDED Requirements

### Requirement: Authentication uses the published frontend API contract
The application SHALL model the authentication request and response as typed frontend data, call `POST /auth/login` through the centrally configured API URL, and map the successful response to the non-sensitive user identity fields `id`, `nome`, and `email`. It SHALL preserve the existing safe error normalization policy.

#### Scenario: Authentication response is accepted
- **WHEN** the endpoint returns a successful user response
- **THEN** the frontend exposes only the typed identity fields needed for local authentication and does not persist the submitted password

#### Scenario: Endpoint returns an API error message
- **WHEN** authentication fails with an API error payload
- **THEN** the login flow selects the credential-specific 401 message or the defined friendly fallback without exposing stack traces
