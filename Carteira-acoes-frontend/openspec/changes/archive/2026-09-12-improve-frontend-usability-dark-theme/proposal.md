## Why

O frontend já possui suporte a tema escuro, mas a experiência ainda pode parecer carregada, fragmentada e próxima demais do Material padrão. Esta change refina hierarquia, navegação, densidade e superfícies para que o produto financeiro seja mais calmo, escaneável e confortável nos temas claro e escuro.

## What Changes

- Auditar visualmente rotas, estados, dialogs, tabelas, forms, menus e ações antes de ajustar estilos.
- Reduzir cards e bordas redundantes, melhorar largura de containers, spacing, tipografia e hierarquia de CTAs.
- Refinar Dashboard, Carteiras, Ações, Operações, Analytics, cadastros, autenticação e dialogs sem alterar dados ou regras financeiras.
- Recalibrar tokens light/dark para grafite contínuo, contraste sutil, uma única cor de destaque e estados financeiros menos saturados.
- Integrar Angular Material/CDK mantendo acessibilidade, responsividade e overlays no tema ativo.
- Exigir revisão visual real em desktop/mobile e nos dois temas, além de testes e build.

## Capabilities

### New Capabilities

- `frontend-usability-and-visual-consistency`: hierarquia, navegação, densidade, responsividade e consistência visual transversal.

### Modified Capabilities

- `dark-theme-support`: refinamento dos requisitos visuais e de acessibilidade das superfícies, overlays, rotas e controles existentes.

## Impact

Estilos globais, shell, componentes compartilhados e páginas Angular em `src/app`; tokens, Material/CDK overlays, gráficos e testes visuais/acessibilidade. Não altera backend, APIs, banco, autenticação, deploy, layout de dados ou regras financeiras.
