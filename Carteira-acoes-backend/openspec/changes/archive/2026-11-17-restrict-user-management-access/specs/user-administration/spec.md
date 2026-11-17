## MODIFIED Requirements

### Requirement: Autorização administrativa dos endpoints de usuário
O sistema MUST expor a administração de usuários em `/api/usuarios` e MUST
exigir `ROLE_ADMIN` para todos os endpoints dessa classe, sem exceções para
criação, leitura ou alteração. Usuários com `ROLE_USER` MUST NOT conseguir
listar, consultar, cadastrar ou alterar qualquer usuário por esses endpoints.

#### Scenario: Administrador acessa a API
- **WHEN** uma requisição para `/api/usuarios` ou `/api/usuarios/{id}` contém um token com `ROLE_ADMIN`
- **THEN** o sistema MUST processar a operação solicitada

#### Scenario: Usuário sem papel administrativo acessa a API
- **WHEN** uma requisição para qualquer endpoint de `/api/usuarios` não contém `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT executar a operação

#### Scenario: Usuário comum tenta listar usuários
- **WHEN** uma requisição `GET /api/usuarios` contém um token sem `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT retornar dados de usuários

#### Scenario: Usuário comum tenta consultar outro usuário
- **WHEN** uma requisição `GET /api/usuarios/{id}` contém um token sem `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT revelar se o usuário solicitado existe

#### Scenario: Usuário comum tenta alterar ou cadastrar usuário
- **WHEN** uma requisição para qualquer endpoint de criação ou alteração em `/api/usuarios` não contém `ROLE_ADMIN`
- **THEN** o sistema MUST responder `403 Forbidden` e MUST NOT executar a operação
