## ADDED Requirements

### Requirement: Operações financeiras do usuário comum
Usuários autenticados com `ROLE_USER` MUST poder criar e manter somente suas próprias carteiras, vínculos de ações e operações de compra e venda. O sistema MUST validar ownership antes de qualquer leitura ou mutação e MUST rejeitar acesso a dados de terceiros sem alterar persistência.

#### Scenario: Usuário comum compra em carteira própria
- **WHEN** um usuário autenticado realiza uma compra em carteira que lhe pertence
- **THEN** o sistema permite a operação conforme as validações financeiras existentes

#### Scenario: Usuário comum vincula ação em carteira própria
- **WHEN** um usuário autenticado cria ou atualiza um vínculo de ação em carteira própria
- **THEN** o sistema permite a alteração conforme as regras de carteira existentes

#### Scenario: Usuário comum acessa carteira de terceiro
- **WHEN** um usuário autenticado informa o identificador de carteira pertencente a outro usuário
- **THEN** o sistema rejeita a solicitação sem retornar ou alterar dados financeiros do terceiro

#### Scenario: Usuário anônimo acessa operação financeira
- **WHEN** uma requisição sem JWT acessa carteira, posição ou operação protegida
- **THEN** o sistema responde `401 Unauthorized` e não executa a operação

