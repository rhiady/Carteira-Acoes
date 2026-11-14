## Why

O domínio `Usuario` agora possui o papel `Papel` (`ADMIN` ou `USER`), mas o backend ainda não usa essa informação para autenticar requisições nem para autorizar operações. Esta mudança estabelece a base de segurança necessária para que os recursos protegidos identifiquem o usuário autenticado e apliquem permissões de forma consistente.

## What Changes

- Evoluir o login existente para criar uma sessão autenticada segura, sem expor senha ou hash.
- Configurar a segurança HTTP para permitir apenas as rotas públicas necessárias, incluindo cadastro e login, e exigir autenticação nas demais rotas protegidas.
- Usar o `Papel` persistido do usuário para autorização, reservando operações administrativas a `ADMIN` e mantendo operações de carteira restritas ao proprietário.
- Persistir o papel com migração compatível, atribuindo `USER` por padrão aos novos cadastros e tratando dados existentes sem papel.
- Impedir que o cliente informe ou eleve o próprio papel durante cadastro e atualização.
- Cobrir login, sessão, acesso anônimo, autorização por papel, isolamento por usuário e ausência de segredos nas respostas com testes de integração.
- Manter o contrato de erro existente e a compatibilidade dos endpoints de negócio que já dependem do usuário atual.
- Não adicionar JWT, refresh token, OAuth, MFA, recuperação de senha ou confirmação de email nesta etapa.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `user-authentication`: substituir o escopo sem autenticação global/roles por autenticação de sessão e autorização baseada no `Papel` persistido.

## Impact

Afeta `Usuario`/`Papel`, DTOs e mapper de usuário, cadastro e login, `CurrentUserService`, `SecurityConfig`, tratamento de sessão e autorização dos resources, migrações Flyway, testes de integração e documentação OpenAPI quando os status de segurança forem alterados. A principal compatibilidade a preservar é o isolamento já implementado entre usuários e a não exposição da senha.
