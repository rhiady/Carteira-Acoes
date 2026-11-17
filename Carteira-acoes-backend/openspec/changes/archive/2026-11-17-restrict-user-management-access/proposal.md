## Why

Usuários comuns autenticados não devem conseguir visualizar o cadastro de
outros usuários nem acessar operações administrativas de identidade. A API já
possui a separação de papéis, mas a regra precisa permanecer explícita e
testável para evitar exposição de dados cadastrais.

## What Changes

- Garantir que todos os endpoints de `/api/usuarios` sejam acessíveis somente
  com `ROLE_ADMIN`.
- Rejeitar com `403 Forbidden` tentativas de usuários comuns de listar,
  consultar, cadastrar ou alterar usuários.
- Preservar o acesso do usuário autenticado ao próprio contexto por meio de
  `/api/auth/me`, sem expor a listagem administrativa.
- Adicionar cenários de teste para impedir regressões de autorização.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `user-administration`: reforçar que todo o cadastro, consulta e alteração de
  usuários é exclusivo de administradores e que usuários comuns não recebem
  dados cadastrais de terceiros.

## Impact

- Endpoints HTTP `/api/usuarios/**` e suas respostas de autorização.
- Configuração de segurança e testes de integração do backend.
- Frontend: deve ocultar as telas administrativas para usuários sem
  `ROLE_ADMIN`, sem tratar essa ocultação como substituta da autorização do
  backend.
