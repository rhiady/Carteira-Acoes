## ADDED Requirements

### Requirement: Política de complexidade para senhas de usuário
O sistema MUST aceitar uma nova senha em cadastro ou atualização cadastral somente quando ela tiver no mínimo 8 caracteres, pelo menos uma letra maiúscula e pelo menos uma letra minúscula.

#### Scenario: Cadastro com senha válida
- **WHEN** um cliente cadastra um usuário com senha de pelo menos 8 caracteres contendo letras maiúsculas e minúsculas
- **THEN** o sistema cria o usuário e armazena a senha de forma codificada

#### Scenario: Cadastro com senha fora da política
- **WHEN** um cliente cadastra um usuário com senha menor que 8 caracteres, sem letra maiúscula ou sem letra minúscula
- **THEN** o sistema responde HTTP 400 com erro de validação e não cria o usuário

#### Scenario: Atualização com nova senha fora da política
- **WHEN** um cliente informa uma nova senha que não atende ao tamanho mínimo ou à exigência de maiúsculas e minúsculas
- **THEN** o sistema responde HTTP 400 com erro de validação e preserva a senha anterior
