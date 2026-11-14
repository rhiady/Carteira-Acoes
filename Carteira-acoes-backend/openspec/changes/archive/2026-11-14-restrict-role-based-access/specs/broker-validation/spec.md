## ADDED Requirements

### Requirement: Operações administrativas de corretoras
As operações de criação, alteração e exclusão de corretoras MUST ser permitidas somente a usuários autenticados com `ROLE_ADMIN`. Consultas de corretoras que já forem públicas MUST permanecer públicas.

#### Scenario: Usuário comum cria corretora
- **WHEN** um usuário autenticado com `ROLE_USER` envia `POST /corretoras`
- **THEN** o sistema responde `403 Forbidden` e não persiste a corretora

#### Scenario: Administrador cria corretora
- **WHEN** um usuário autenticado com `ROLE_ADMIN` envia `POST /corretoras` com CNPJ válido
- **THEN** o sistema permite a criação após as validações cadastrais existentes

#### Scenario: Usuário comum exclui corretora
- **WHEN** um usuário autenticado com `ROLE_USER` envia `DELETE /corretoras/{id}`
- **THEN** o sistema responde `403 Forbidden` e preserva a corretora e seus vínculos

#### Scenario: Administrador exclui corretora
- **WHEN** um usuário autenticado com `ROLE_ADMIN` exclui uma corretora sem vínculos impeditivos
- **THEN** o sistema permite a exclusão conforme as regras de integridade existentes

