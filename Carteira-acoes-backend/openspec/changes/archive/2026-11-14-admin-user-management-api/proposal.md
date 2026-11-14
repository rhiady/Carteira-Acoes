## Why

O backend já possui operações de usuário, mas elas ainda misturam autoatendimento com administração e não correspondem ao contrato administrativo definido para a API. Esta mudança fecha o ponto de parada do serviço de usuários, garantindo que credenciais sejam sempre codificadas antes da persistência e que a administração de identidades seja protegida de forma uniforme.

## What Changes

- **BREAKING**: padronizar todos os endpoints HTTP de negócio sob o prefixo `/api` (`/api/acoes`, `/api/carteiras`, `/api/corretoras`, `/api/operacoes` e `/api/usuarios`), mantendo autenticação em `/api/auth`.
- **BREAKING**: proteger toda a classe `UsuarioResource` com `ROLE_ADMIN`.
- Consolidar no `UsuarioService` a persistência, transformação, validação de username e codificação BCrypt de senhas.
- Implementar criação, consulta, listagem, alteração cadastral, alteração de papel e definição de nova senha no contrato administrativo.
- Alterar cadastro somente em `username` e `ativo`; não permitir que o update altere senha ou papel.
- Preservar usuários desativados para auditoria e impedir o login de contas com `ativo=false`.
- Remover da API administrativa a operação DELETE e manter a alteração de papel em endpoint separado e protegido.
- Garantir respostas sem senha ou hash e manter unicidade de username.

## Capabilities

### New Capabilities

- `user-administration`: gerenciamento administrativo de identidades, incluindo CRUD sem DELETE, alteração de senha e autorização exclusiva para administradores.

### Modified Capabilities

- `user-authentication`: usuários desativados devem ser tratados como contas desabilitadas e não podem autenticar.

## Impact

Afeta `UsuarioService`, `UsuarioResource`, `UsuarioDetailsService`, DTOs, mapper, repositório, todos os resources de negócio, configuração de segurança, testes de integração e documentação da API. A alteração dos prefixos e da autorização é incompatível com clientes que usam as rotas sem `/api` ou `/usuarios` para autoatendimento; a entidade e os registros existentes serão preservados, sem DELETE ou migração destrutiva.
