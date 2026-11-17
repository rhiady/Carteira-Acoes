## Why

A área comum ainda expõe caminhos e ações de gestão que pertencem ao administrador, e a navegação não oferece um espaço claro para a pessoa consultar seus próprios dados. Isso mistura responsabilidades, permite que usuários comuns encontrem outros usuários pela interface e deixa a redefinição de senha sem um fluxo de conta próprio.

## What Changes

- Remover da navegação, telas e ações da área `ROLE_USER` os recursos administrativos, incluindo usuários, corretoras, cadastro/exclusão de ações e demais comandos de manutenção.
- Criar uma aba/tela `Minha conta` exclusiva para o usuário autenticado, exibindo somente sua identidade e ações permitidas para sua própria conta.
- Adicionar o fluxo acessível de redefinição de senha, sem exibir, recuperar ou persistir a senha atual.
- Manter a área administrativa separada e preservar os fluxos financeiros próprios do usuário comum.
- Cobrir rotas, navegação, tentativa de acesso direto, estados de formulário, erros seguros e acessibilidade nos temas claro e escuro.
- Manter explícita a dependência de um endpoint backend de autoalteração de senha; nenhum endpoint será inventado no frontend.

## Capabilities

### New Capabilities

- `common-user-account-experience`: área Minha conta, isolamento visual de recursos administrativos e fluxo de redefinição de senha para a própria identidade autenticada.

### Modified Capabilities

- `user-access-experience`: restringir a experiência comum aos recursos do próprio usuário e adicionar navegação para Minha conta.
- `user-edit-experience`: separar a edição administrativa de usuários do fluxo de alteração da senha da própria conta, preservando privacidade e validação.

## Impact

- Affected UI: shell/navegação global, rotas protegidas por papel, nova tela de Minha conta e formulário de senha.
- Affected code: `app.html`, `app.routes.ts`, guardas por papel, componentes de acesso, `AuthService` e um service/model de conta somente se necessário.
- Backend dependency: consumir o contrato publicado `PUT /api/auth/senha` com Bearer, `{ password }`, retorno `204` e validação de 8–72 caracteres; não usar `PUT /api/usuarios/{id}`.
- No new dependency: reutilizar Angular Router, Reactive Forms/Signal Forms, Angular Material, tokens Swiss existentes e componentes compartilhados.
