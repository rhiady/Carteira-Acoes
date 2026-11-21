## MODIFIED Requirements

### Requirement: Catálogo de ações não oferece cadastro direto
The application SHALL list, detail and look up actions by ticker and request a quote refresh through `POST /acoes/{id}/cotacao`. An administrator SHALL create or import a global action through `POST /acoes` with only `ticker` and `mercado`, and MUST prevent duplicate creation while that request is pending. This administrative action MUST NOT create a carteira, posição or operação. The application MUST NOT expose unsupported action-editing commands.

#### Scenario: Ação global é cadastrada
- **WHEN** an administrator provides a valid ticker and mercado
- **THEN** the application sends `{ ticker, mercado }` to `POST /acoes` and confirms the returned global action without a financial operation

#### Scenario: Cotação é atualizada
- **WHEN** the user requests an available action's quote refresh
- **THEN** the application POSTs to `/acoes/{id}/cotacao` and displays the returned quote fields

#### Scenario: Atualização de cotação pendente
- **WHEN** a quote refresh request is in progress
- **THEN** the triggering control indicates loading and cannot submit a duplicate request

### Requirement: Consultas de recursos disponíveis
The application SHALL provide paginated listings and detail retrieval by identifier for usuários, corretoras, carteiras and ações. An administrator SHALL retrieve the published user listing and MAY create or import a global action with `POST /acoes` using only `ticker` and `mercado`; this request MUST NOT include carteira, posição or operação data. It SHALL provide corretora lookup by CNPJ, ação lookup by normalized ticker, carteiras for a usuário, and posições for a carteira through their published endpoints. When displaying a position that lacks stock details, it SHALL obtain the corresponding action by `acaoId` only for UI enrichment and shall not persist duplicated action data.

#### Scenario: Administrador consulta usuários
- **WHEN** an authenticated administrator opens the user-management route
- **THEN** the application requests the paginated published user listing and renders its users or an accessible loading, empty or error state

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
