## ADDED Requirements

### Requirement: Política de complexidade para senhas administrativas
O sistema MUST aceitar uma senha em criação administrativa ou troca administrativa somente quando ela tiver no mínimo 8 caracteres, pelo menos uma letra maiúscula e pelo menos uma letra minúscula.

#### Scenario: Administrador informa senha válida
- **WHEN** um administrador cria um usuário ou troca sua senha usando uma senha de pelo menos 8 caracteres contendo letras maiúsculas e minúsculas
- **THEN** o sistema conclui a operação e armazena a senha de forma codificada

#### Scenario: Administrador informa senha inválida
- **WHEN** um administrador cria um usuário ou troca sua senha usando uma senha menor que 8 caracteres, sem letra maiúscula ou sem letra minúscula
- **THEN** o sistema responde HTTP 400 com erro de validação e não altera nem cria o usuário

#### Scenario: Login preserva compatibilidade
- **WHEN** um usuário tenta fazer login com uma senha já armazenada que não atende à nova política, mas informa a credencial correta
- **THEN** o sistema avalia a credencial normalmente e não rejeita o login apenas pela composição da senha
