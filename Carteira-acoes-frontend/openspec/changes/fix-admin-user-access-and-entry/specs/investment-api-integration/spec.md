## MODIFIED Requirements

### Requirement: Consultas de recursos disponíveis

The application SHALL provide paginated listings and detail retrieval by identifier for usuários, corretoras, carteiras and ações. An administrator SHALL retrieve the published paginated user listing using the authenticated administrative session and render only the returned page, its pagination metadata, or an accessible loading, empty or error state. The user-list projection SHALL use only its published `id`, `login`, `papel` and `ativo` fields; it MUST NOT assume name, email or timestamp fields. A `403` from that resource SHALL preserve the authenticated session and present an access-denied state; other failures SHALL present the API `message` when available or the safe fallback. An administrator MAY create or import a global action with `POST /acoes` using only `ticker` and `mercado`; this request MUST NOT include carteira, posição or operação data. It SHALL provide corretora lookup by CNPJ, ação lookup by normalized ticker, carteiras for a usuário, and posições for a carteira through their published endpoints. When displaying a position that lacks stock details, it SHALL obtain the corresponding action by `acaoId` only for UI enrichment and shall not persist duplicated action data.

#### Scenario: Administrador consulta usuários
- **WHEN** an authenticated administrator opens the user-management route
- **THEN** the application requests the paginated published user listing and renders its users or an accessible loading, empty or error state

#### Scenario: Administrador não tem acesso à listagem
- **WHEN** the published user listing returns HTTP 403
- **THEN** the application preserves the session and presents an accessible access-denied state without showing stale or invented users

#### Scenario: Administrador cadastra ação global
- **WHEN** an administrator submits a valid ticker and mercado
- **THEN** the application sends only `{ ticker, mercado }` to `POST /acoes`, confirms the returned global action and does not create a carteira, posição or operação

#### Scenario: Carteira apresenta posições
- **WHEN** the user opens a carteira detail
- **THEN** the application requests `/carteiras/{id}/acoes` and presents each returned posição using id, carteiraId, acaoId, ticker, quantidade and version

#### Scenario: Corretora é consultada pelo CNPJ
- **WHEN** the user searches with a CNPJ
- **THEN** the application requests `/corretoras/cnpj/{cnpj}` and shows the returned broker information, including nullable complementary fields as unavailable when absent

#### Scenario: Busca de ação é confirmada
- **WHEN** the user confirms a ticker search
- **THEN** the application trims and uppercases the ticker before requesting `/acoes/ticker/{ticker}` and does not issue a request for every keystroke
