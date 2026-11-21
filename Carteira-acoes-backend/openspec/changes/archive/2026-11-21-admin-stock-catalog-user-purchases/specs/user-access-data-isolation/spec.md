## MODIFIED Requirements

### Requirement: Operações financeiras do usuário comum
Usuários autenticados com `ROLE_USER` MUST poder criar e manter somente suas próprias carteiras e operações de compra e venda. O sistema MUST validar ownership antes de qualquer leitura ou mutação e MUST rejeitar acesso a dados de terceiros sem alterar persistência. Para compra, o usuário comum MUST selecionar uma ação global previamente disponibilizada por administrador por `acaoId`; ele MUST NOT criar, importar ou indicar ação por ticker e mercado.

#### Scenario: Usuário comum compra ação do catálogo em carteira própria
- **WHEN** um usuário autenticado realiza uma compra para carteira que lhe pertence com `acaoId` de ação existente
- **THEN** o sistema permite a operação conforme as validações financeiras existentes

#### Scenario: Usuário comum tenta introduzir ticker
- **WHEN** um usuário autenticado envia uma compra com ticker ou mercado em vez de somente `acaoId`
- **THEN** o sistema rejeita a solicitação sem criar ação nem alterar dados financeiros

#### Scenario: Usuário comum acessa carteira de terceiro
- **WHEN** um usuário autenticado informa o identificador de carteira pertencente a outro usuário
- **THEN** o sistema rejeita a solicitação sem retornar ou alterar dados financeiros do terceiro

#### Scenario: Usuário anônimo acessa operação financeira
- **WHEN** uma requisição sem JWT acessa carteira, posição ou operação protegida
- **THEN** o sistema responde `401 Unauthorized` e não executa a operação

#### Scenario: Usuário comum compra em carteira própria
- **WHEN** um usuário autenticado realiza uma compra com `acaoId` de ação existente em carteira que lhe pertence
- **THEN** o sistema permite a operação conforme as validações financeiras existentes

#### Scenario: Usuário comum vincula ação em carteira própria
- **WHEN** um usuário autenticado cria ou atualiza um vínculo de ação global existente em carteira própria
- **THEN** o sistema permite a alteração conforme as regras de carteira existentes
