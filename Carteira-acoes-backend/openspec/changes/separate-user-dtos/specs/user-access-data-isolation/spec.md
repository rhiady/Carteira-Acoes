## MODIFIED Requirements

### Requirement: Account update preserves credential safety
O sistema MUST manter a atualização cadastral, a alteração de senha e a alteração de papel separadas. `PUT /usuarios/{id}` SHALL aceitar somente `login` e `ativo`, `PUT /usuarios/{id}/senha` SHALL aceitar somente uma nova senha e `PUT /usuarios/{id}/papel` SHALL exigir `ROLE_ADMIN`. A senha MUST ser codificada antes da persistência, e nenhuma resposta SHALL expor senha ou hash.

#### Scenario: Atualização cadastral sem senha
- **WHEN** um usuário autorizado atualiza `login`, `papel` e `ativo` sem informar senha
- **THEN** os dados cadastrais são salvos e a senha anterior permanece utilizável

#### Scenario: Atualização de senha
- **WHEN** um usuário autorizado envia uma nova senha para `PUT /usuarios/{id}/senha`
- **THEN** a nova senha é armazenada codificada e pode ser usada no login subsequente

#### Scenario: Senha não pode ser trocada por atualização cadastral
- **WHEN** um usuário envia uma senha junto com `PUT /usuarios/{id}`
- **THEN** a senha não é alterada pela operação cadastral

#### Scenario: Update profile without password
- **WHEN** um usuário existente atualiza `login`, `papel` e `ativo` sem fornecer senha
- **THEN** as alterações cadastrais são salvas e a senha anterior permanece utilizável

#### Scenario: Update password
- **WHEN** um usuário existente fornece uma nova senha em `PUT /usuarios/{id}/senha`
- **THEN** a nova senha é armazenada codificada e pode ser usada no login subsequente

#### Scenario: Resposta segura
- **WHEN** uma atualização cadastral ou de senha é concluída
- **THEN** a resposta não contém `senha`, `password` nem o hash persistido
