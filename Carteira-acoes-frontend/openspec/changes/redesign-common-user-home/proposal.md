## Why

A experiência de `ROLE_USER` está fragmentada entre uma rota intermediária (`/usuario`), uma home de carteiras (`/inicio`) e um dashboard separado (`/dashboard`), fazendo conteúdo financeiro e gráficos parecerem escondidos. A navegação, `Minha conta` e `Sair` também não formam uma área pessoal coerente, reduzindo a clareza e o valor visual do produto.

## What Changes

- Tornar `/inicio` a home canônica do usuário comum, reunindo seleção de carteira, resumo financeiro por moeda, ações contextuais e gráficos autoritativos na primeira experiência útil após o login.
- Redirecionar o login de `ROLE_USER` diretamente para `/inicio`, preservar `/usuario` como redirect de compatibilidade para `/minha-conta` e eliminar a duplicidade visual entre `/inicio` e `/dashboard` por meio de redirect ou consolidação.
- Simplificar a navegação comum para `Início`, `Carteiras`, `Operações` e `Ações`, com `Minha conta`, identidade autenticada e `Sair` agrupados como utilidades pessoais persistentes.
- Redesenhar `Minha conta` como área pessoal completa: identidade, segurança, feedback do formulário e encerramento de sessão, sem índices decorativos ou links administrativos.
- Tornar o sistema mais visual usando os dados reais já disponíveis: hierarquia financeira forte, gráficos maiores e legíveis, comparação de composição e resultado, legendas textuais e estados vazios/indisponíveis úteis.
- Preservar separação BRL/USD, alternativas textuais dos gráficos, responsividade, tema claro/escuro, foco e navegação por teclado.
- Não criar novos endpoints, métricas sintéticas, conversão cambial, feed de atividade, cálculo financeiro ou dependência de visualização.

## Capabilities

### New Capabilities

- `common-user-home-experience`: entrada canônica, composição visual, navegação e continuidade da experiência financeira do usuário comum.

### Modified Capabilities

- `common-user-account-experience`: integrar `Minha conta` e logout à navegação pessoal e tornar a conta o destino compatível da antiga rota `/usuario`.
- `investment-product-navigation`: consolidar `/inicio` como entrada principal e remover a duplicidade de navegação com `/dashboard`.
- `portfolio-analytics`: tornar resumo e gráficos autoritativos parte visível e prioritária da home comum.
- `frontend-usability-and-visual-consistency`: reforçar hierarquia visual, densidade, estados e responsividade da home, gráficos e conta.

## Impact

- Affected UI/routes: login redirect, `app.routes`, shell/navegação, `InicioPage`, `DashboardPage`, `UserAccessPage`, `MyAccountPage`, gráficos e estilos compartilhados relacionados.
- Affected verification: rotas e redirects, navegação por papel, home com/sem carteiras, analytics por moeda, conta/logout, temas, AXE e viewports mobile/tablet/desktop.
- API/backend: reutiliza somente `/api/auth/me`, carteiras do usuário, resumo e posições já publicados; nenhum contrato backend muda.
- Dependencies: nenhuma dependência nova; reutilizar Angular Material, ApexCharts, serviços, mappers, formatters e componentes atuais.
