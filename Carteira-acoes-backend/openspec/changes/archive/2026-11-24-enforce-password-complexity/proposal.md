## Why

As senhas atualmente validam apenas o tamanho mínimo, permitindo credenciais fracas sem distinção entre letras maiúsculas e minúsculas. A regra deve ser aplicada de forma consistente em todos os fluxos que criam ou alteram senhas, sem alterar o comportamento do login de credenciais já existentes.

## What Changes

- Exigir pelo menos 8 caracteres em novas senhas.
- Exigir ao menos uma letra maiúscula e uma letra minúscula em novas senhas.
- Aplicar a regra no cadastro, na criação administrativa e nas trocas de senha disponíveis pela API.
- Retornar erro de validação padronizado e não persistir a alteração quando a senha não atender à política.
- Manter o login responsável apenas por autenticar a senha informada, sem rejeitar senhas antigas somente por sua composição.
- Acrescentar testes de aceitação para limites de tamanho, ausência de maiúscula, ausência de minúscula e entradas válidas.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `portfolio-catalog`: exigir a política de complexidade em senhas de cadastro e atualização cadastral quando uma nova senha for informada.
- `user-administration`: exigir a política de complexidade em criação e troca administrativa de senha.

## Impact

- DTOs de entrada e validações dos fluxos de usuário.
- Endpoints de cadastro, administração e alteração de senha.
- Testes unitários e de integração dos contratos de usuários.
- Nenhuma alteração de schema de banco ou de formato de senha já armazenada.
