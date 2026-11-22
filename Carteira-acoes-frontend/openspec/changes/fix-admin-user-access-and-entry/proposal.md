## Why

A área administrativa não permite consultar os usuários de forma confiável e o destino `Administração` leva para a mesma página de `Usuários`, removendo a distinção entre entrada administrativa e gestão de pessoas. Isso impede que administradores entendam onde estão e concluam uma tarefa de gestão básica.

## What Changes

- Corrigir a consulta paginada de usuários na área administrativa usando exclusivamente o recurso administrativo publicado — que retorna `id`, `login`, `papel` e `ativo` — e apresentar estados de carregamento, vazio, falha e nova tentativa.
- Garantir que falhas de autorização ou contrato na listagem sejam explicadas de forma acionável, preservando a sessão quando a API retornar `403`.
- Tornar `/admin` uma entrada administrativa própria, distinta de `/usuarios`, com navegação para os recursos administrativos já publicados.
- Manter `Usuários` como a tela especializada para listar e abrir os usuários retornados pela API; apresentar somente os campos publicados e não oferecer edição baseada em nome, e-mail ou datas que o contrato não retorna.
- Não alterar a rota pública de auto-cadastro `/cadastro`, nem seu fluxo de autenticação, redirecionamentos, payload ou contrato.
- Preservar guardas por papel, rotas lazy, tema, responsividade, foco e navegação por teclado.

## Capabilities

### New Capabilities

- `administrator-management-experience`: entrada administrativa distinta e acesso confiável à gestão de usuários autorizada.

### Modified Capabilities

- `investment-api-integration`: esclarecer o consumo e o tratamento de falhas da listagem administrativa de usuários pelo contrato publicado.
- `frontend-usability-and-visual-consistency`: exigir contextos distintos entre entrada administrativa e gestão de usuários, com estados acessíveis e continuidade clara.

## Impact

- UI/routes: `AdminPage`, rotas `/admin` e `/usuarios`, navegação administrativa, página de usuários e estados compartilhados.
- Serviços: `UsuarioService` e a normalização de erros já existente, sem criar endpoint, payload ou modelo novo.
- Rotas públicas: `/cadastro` e seu fluxo de auto-cadastro permanecem inalterados.
- Verificação: testes de rota, serviço, página de usuários, estados de erro/acesso e AXE em temas claro/escuro.
- Backend: reutiliza somente o endpoint administrativo publicado para usuários e a autorização Bearer existente.
