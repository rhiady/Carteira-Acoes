## MODIFIED Requirements

### Requirement: Formulários permanecem operáveis em diferentes larguras e tecnologias assistivas

O sistema SHALL reorganizar tanto a página quanto o diálogo administrativo de criação de carteira em uma coluna em telas estreitas e preservar grupos, rótulos, instruções, erros, controles e ações sem corte ou rolagem horizontal. As duas superfícies SHALL usar a mesma composição visual e comportamental para administrador e usuário comum; controles interativos MUST possuir foco visível, nome acessível e área de interação utilizável; estados de sucesso, carregamento e erro MUST ser compreensíveis sem depender exclusivamente de cor.

#### Scenario: Pessoa cria carteira em 375 px
- **WHEN** uma pessoa navega pelo formulário com teclado em uma largura de 375 px
- **THEN** nome, corretora, mensagens e ações ficam alcançáveis em ordem lógica, sem overflow horizontal, sobreposição ou ação primária oculta

#### Scenario: Administrador e usuário comum abrem criação
- **WHEN** um administrador e um usuário comum abrem a criação de carteira
- **THEN** encontram a mesma hierarquia, campos e estados, com diferenças somente no contexto de autorização e ownership exigido pelo contrato

#### Scenario: Nenhum fluxo expõe escolha de proprietário
- **WHEN** um administrador ou usuário comum abre a criação de carteira pela página ou pelo diálogo administrativo
- **THEN** não existe campo ou lista de usuário; o proprietário é derivado exclusivamente da identidade autenticada

#### Scenario: Dependência de corretora falha
- **WHEN** a consulta de corretoras falha ou retorna uma coleção vazia
- **THEN** o formulário explica a dependência, mantém o contexto e impede submissão inválida sem inventar uma corretora

#### Scenario: Pessoa usa teclado em uma tela estreita
- **WHEN** uma pessoa navega por um formulário de criação ou operação com teclado em uma largura de 375 px
- **THEN** todos os campos e ações são alcançáveis em ordem lógica, o foco permanece visível e a ação primária não fica oculta ou sobreposta

#### Scenario: Envio falha
- **WHEN** uma solicitação de criação ou operação falha
- **THEN** o sistema mantém os valores não enviados, anuncia uma mensagem de erro contextual e permite que a pessoa corrija ou tente novamente

### Requirement: Registration and wallet creation respect implicit ownership

The registration form SHALL contain name, email, password and confirmation only. Wallet creation SHALL not allow choosing an arbitrary owner in either the common-user page or the administrative dialog; it SHALL preserve required broker selection, send the required `usuarioId` populated from the current authenticated identity according to the published `POST /api/carteiras` contract, and work for both `ROLE_USER` and authorized `ROLE_ADMIN` entry contexts. The backend remains responsible for resolving the JWT identity and ignoring ownership substitution.

#### Scenario: Usuário comum autenticado cria carteira
- **WHEN** a pessoa com `ROLE_USER` abre e envia uma carteira válida
- **THEN** o sistema não exibe seletor de usuário e envia `nome`, `usuarioId` da identidade autenticada e `corretoraId` em um payload compatível com `POST /api/carteiras`

#### Scenario: Administrador cria carteira
- **WHEN** uma pessoa com `ROLE_ADMIN` abre a criação por uma entrada administrativa permitida
- **THEN** o formulário preserva a seleção obrigatória de corretora, envia o `usuarioId` da identidade autenticada do administrador e não exibe seletor ou lista de usuários
- **THEN** o formulário preserva a seleção obrigatória de corretora, usa a identidade autenticada do administrador e não exibe seletor ou lista de usuários

#### Scenario: Identidade autenticada não está disponível
- **WHEN** a criação é aberta sem identidade autenticada válida
- **THEN** a submissão é impedida, o erro é apresentado de forma acionável e nenhum payload de carteira é enviado

#### Scenario: Authenticated person creates a wallet
- **WHEN** a person opens wallet creation while authenticated
- **THEN** the form does not offer a user selector and submits the current user's `usuarioId` together with name and broker through the existing wallet contract

#### Scenario: Registration validation fails
- **WHEN** a required registration field is empty, email is invalid or passwords differ
- **THEN** submission is prevented and each correction is identified accessibly without logging sensitive values
