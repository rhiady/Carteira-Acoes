## ADDED Requirements

### Requirement: Operações administrativas do catálogo
As operações de criação, alteração e exclusão de ações e corretoras MUST ser permitidas somente a usuários autenticados com `ROLE_ADMIN`. Consultas de catálogo que já forem públicas MUST permanecer públicas, sem conceder permissão de escrita.

#### Scenario: Usuário comum cria ação
- **WHEN** um usuário autenticado com `ROLE_USER` envia `POST /acoes`
- **THEN** o sistema responde `403 Forbidden` e não persiste a ação

#### Scenario: Administrador cria ação
- **WHEN** um usuário autenticado com `ROLE_ADMIN` envia `POST /acoes` com dados válidos
- **THEN** o sistema permite a criação conforme as validações atuais

#### Scenario: Usuário comum altera ou exclui ação
- **WHEN** um usuário autenticado com `ROLE_USER` envia `PUT`, `PATCH` ou `DELETE` para uma ação
- **THEN** o sistema responde `403 Forbidden` e não altera o catálogo

#### Scenario: Administrador altera ou exclui ação
- **WHEN** um usuário autenticado com `ROLE_ADMIN` envia uma alteração ou exclusão válida de ação
- **THEN** o sistema permite a operação conforme as validações atuais

