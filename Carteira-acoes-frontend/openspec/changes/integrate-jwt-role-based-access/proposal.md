## Why

O frontend ainda está alinhado a um fluxo antigo de autenticação baseado em identidade local, enquanto o backend publicado fornece sessão JWT e permissões em `GET /api/auth/me`. Isso impede proteger corretamente operações administrativas e mistura, na mesma experiência, o acesso de administrador com o fluxo de usuário comum.

## What Changes

- **BREAKING**: alinhar login, sessão, logout, interceptor e guardas ao contrato `POST /api/auth/login` + `GET /api/auth/me`, armazenando o token apenas para a sessão e removendo-o em logout, expiração ou `401`.
- Modelar a identidade autenticada e suas `authorities` (`ROLE_ADMIN` ou `ROLE_USER`) com tipos explícitos.
- Separar a navegação em uma tela/área administrativa para `ROLE_ADMIN` e uma tela/área de uso comum para `ROLE_USER`.
- Proteger rotas administrativas com autorização por papel no frontend, mantendo o backend como autoridade final e tratando `403` como acesso negado sem apagar a sessão.
- Direcionar cada papel para sua área inicial e impedir que um usuário comum veja comandos administrativos, mantendo os fluxos financeiros próprios disponíveis conforme o contrato existente.
- Atualizar testes de autenticação, interceptor, guardas, rotas e acessibilidade das duas áreas.

## Capabilities

### New Capabilities

- `role-based-access-experience`: separação de áreas, navegação inicial e proteção de interface para administradores e usuários comuns.

### Modified Capabilities

- `email-password-authentication`: substituir o fluxo de identidade local pelo contrato JWT, recuperação de sessão, `/api/auth/me`, expiração e limpeza de token.
- `user-access-experience`: manter os dados financeiros do usuário comum isolados e remover qualquer possibilidade de acesso visual a comandos administrativos.
- `investment-api-integration`: aplicar o contrato publicado `/api/auth/*`, Bearer automático e tratamento distinto de `401` e `403`.

## Impact

- Affected code: `AuthService`, modelos de autenticação, `authGuard`, novos guardas/rotas por papel se necessários, interceptor HTTP, `app.routes`, navegação e telas de entrada das áreas administrativa e comum.
- Affected verification: specs de autenticação, rotas, interceptor, login e novos testes de redirecionamento por papel e acessibilidade.
- Backend dependency: requer que o backend entregue exatamente o contrato documentado em `openspec/specs/ESPECIFICACAO-INTEGRACAO-FRONTEND.md`; não cria endpoints nem replica JWT no frontend.
- No new dependency: a implementação deve reutilizar Angular Router, HttpClient, sinais e componentes/tokens visuais existentes.
