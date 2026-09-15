# investment-api-integration Specification

## Purpose
Define the frontend contract for consuming published investment resources, validating requests and presenting authoritative API results without inventing backend capabilities.

## Requirements

### Requirement: Authentication uses the published frontend API contract
The application SHALL model the authentication request and response as typed frontend data, call `POST /auth/login` through the centrally configured API URL, and map the successful response to the non-sensitive user identity fields `id`, `nome`, and `email`. It SHALL preserve the existing safe error normalization policy.

#### Scenario: Authentication response is accepted
- **WHEN** the endpoint returns a successful user response
- **THEN** the frontend exposes only the typed identity fields needed for local authentication and does not persist the submitted password

#### Scenario: Endpoint returns an API error message
- **WHEN** authentication fails with an API error payload
- **THEN** the login flow selects the credential-specific 401 message or the defined friendly fallback without exposing stack traces

### Requirement: Configuração e respostas seguem o contrato local
The application SHALL send all investment API requests to the centrally configured `http://localhost:8080` base URL. It MUST use typed request and response DTOs, `HttpParams` for pagination, and the Spring page envelope (`content`, `totalElements`, `totalPages`, `size`, `number`, `first`, `last`, `numberOfElements`, `empty`). Paginated listings MUST default to `page=0`, `size=20`, and `sort=id,desc`. It MUST present an API error's `message` when supplied and otherwise present a safe generic failure message, without exposing stack traces.

#### Scenario: Listagem paginada é recebida
- **WHEN** a list endpoint returns a page envelope
- **THEN** the application renders `content` and uses its metadata to expose valid page navigation

#### Scenario: API devolve erro de negócio
- **WHEN** the API responds with an error payload containing `status`, `error` and `message`
- **THEN** the user receives the returned message in the context of the failed action

#### Scenario: Erro sem mensagem utilizável
- **WHEN** an API failure has no usable response message
- **THEN** the application displays “Não foi possível concluir a operação.” without technical details

### Requirement: Cadastros respeitam dependências do fluxo
The application SHALL allow creating a usuário with nome, valid email and senha; a corretora with CNPJ only; and a carteira with nome, usuarioId and corretoraId. It MUST require a successful usuário and corretora selection before enabling carteira creation and retain the returned identifiers for subsequent requests. It MUST NOT display creation fields that are populated by the broker backend integration.

#### Scenario: Pessoa conclui o cadastro inicial
- **WHEN** the user creates a usuário, then a corretora, then a carteira with their returned identifiers
- **THEN** the application sends POST requests to `/usuarios`, `/corretoras` and `/carteiras` in that order and confirms each successful response

#### Scenario: Campo de cadastro obrigatório não é informado
- **WHEN** a required field is empty or an email is invalid
- **THEN** the application prevents submission and identifies the correction required

#### Scenario: Cadastro de corretora consulta dados externos
- **WHEN** the user submits a CNPJ
- **THEN** the application sends only the CNPJ to `/corretoras`, disables duplicate submission while pending, and reports the backend outcome

### Requirement: Consultas de recursos disponíveis
The application SHALL provide paginated listings and detail retrieval by identifier for usuários, corretoras, carteiras and ações. It SHALL provide corretora lookup by CNPJ, ação lookup by normalized ticker, carteiras for a usuário, and posições for a carteira through their published endpoints. When displaying a position that lacks stock details, it SHALL obtain the corresponding action by `acaoId` only for UI enrichment and shall not persist duplicated action data.

#### Scenario: Carteira apresenta posições
- **WHEN** the user opens a carteira detail
- **THEN** the application requests `/carteiras/{id}/acoes` and presents each returned posição using id, carteiraId, acaoId, ticker, quantidade and version

#### Scenario: Corretora é consultada pelo CNPJ
- **WHEN** the user searches with a CNPJ
- **THEN** the application requests `/corretoras/cnpj/{cnpj}` and shows the returned broker information, including nullable complementary fields as unavailable when absent

#### Scenario: Busca de ação é confirmada
- **WHEN** the user confirms a ticker search
- **THEN** the application trims and uppercases the ticker before requesting `/acoes/ticker/{ticker}` and does not issue a request for every keystroke

### Requirement: Compra cria ou reutiliza o ativo conforme o contrato
The application SHALL submit purchases to `/operacoes/compras` with carteiraId and quantidade of at least `0.0001`. It MUST send exactly one asset reference: either acaoId, or ticker together with mercado `BRASIL` or `EUA`. It SHALL include precoUnitario only when it is at least `0.0001` and charges only when each supplied charge is non-negative. It MUST prevent submission without a carteira and MUST treat the returned `OperacaoResponse` amounts as official rather than any client estimate.

#### Scenario: Compra de ativo ainda inexistente
- **WHEN** the user enters carteiraId, ticker, mercado and quantidade without acaoId
- **THEN** the application submits that asset locator to the purchase endpoint and confirms the returned operation

#### Scenario: Compra possui referências de ativo conflitantes
- **WHEN** the user provides acaoId together with ticker or mercado
- **THEN** the application prevents submission and explains that only one asset reference is permitted

#### Scenario: Compra concluída atualiza a carteira
- **WHEN** a purchase returns `201 Created`
- **THEN** the application closes the operation form, confirms success, and refreshes that carteira's positions and operation history

### Requirement: Venda e histórico de operações seguem o contrato
The application SHALL submit a sale to `/operacoes/vendas` with carteiraId, acaoId and quantidade of at least `0.0001`, adding charges only when supplied and non-negative. It SHALL display a carteira operation history from `/operacoes/carteiras/{carteiraId}` and retrieve a specific operation from `/operacoes/{id}`. The sale interface MUST use an existing position and prevent an entered quantity above the displayed available quantity, while the backend remains authoritative.

#### Scenario: Venda válida é registrada
- **WHEN** the user provides carteiraId, acaoId and a positive quantidade
- **THEN** the application sends the sale request and presents the returned operation result

#### Scenario: Histórico possui várias páginas
- **WHEN** the history endpoint returns more than one page
- **THEN** the user can navigate the valid operation pages without losing the carteira context

#### Scenario: Venda concluída atualiza a carteira
- **WHEN** a sale returns `201 Created`
- **THEN** the application refreshes the carteira's positions and operation history and shows the successful operation feedback

### Requirement: Catálogo de ações não oferece cadastro direto
The application SHALL list, detail and look up actions by ticker and request a quote refresh through `POST /acoes/{id}/cotacao`. It MUST prevent duplicate quote-refresh requests while one is pending and MUST NOT expose direct action creation, editing, or deletion commands because the API does not publish those operations.

#### Scenario: Cotação é atualizada
- **WHEN** the user requests an available action's quote refresh
- **THEN** the application POSTs to `/acoes/{id}/cotacao` and displays the returned quote fields

#### Scenario: Atualização de cotação pendente
- **WHEN** a quote refresh request is in progress
- **THEN** the triggering control indicates loading and cannot submit a duplicate request

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
