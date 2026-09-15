## Why

As telas existentes usam dimensões e hierarquias inconsistentes: páginas analíticas ficam estreitas, gráficos e tabelas perdem legibilidade, e formulários e dialogs não aproveitam o espaço adequado ao seu conteúdo. Esta mudança cria uma escala global coerente para que todas as rotas pareçam um produto financeiro desktop moderno sem transformar cada tela em um bloco excessivamente grande.

## What Changes

- Auditar e refatorar todas as rotas reais do frontend, incluindo Home, Dashboard, carteiras, posições, ações, histórico, operações, corretoras e usuários.
- Estabelecer tokens compartilhados de largura, padding, spacing, tipografia, controles, cards, tabelas, dialogs e breakpoints progressivos.
- Ajustar individualmente a composição de páginas analíticas, detalhes, cadastros e operações conforme a densidade e importância do conteúdo.
- Dar escala adequada a Dashboard, Analytics, históricos, tabelas financeiras, formulários de compra/venda, estados vazios, loading, erro, paginação e navegação.
- Tornar `Comprar ação` uma ação textual e contextualizável no Dashboard, carteiras, ativos e estados vazios quando tecnicamente suportado, preservando carteira/ativo de origem.
- Revisar todos os dialogs e validar desktop grande, notebook, tablet e mobile em runtime, sem alterar contratos de API ou inventar dados.

## Capabilities

### New Capabilities

- `global-frontend-layout-scale`: sistema de composição, escala, responsividade e consistência visual aplicado a todas as telas e componentes globais.

### Modified Capabilities

- `creation-form-experience`: formulários e operações passam a usar composição proporcional ao conteúdo, com compra/venda mais explícitas e acessíveis.
- `investment-frontend-experience`: todas as telas, estados, tabelas, gráficos e dialogs passam a cumprir a escala global e a ação contextual de compra.

## Impact

- Componentes Angular em `src/app/core`, `src/app/features` e `src/app/shared/components`, incluindo shell, páginas, tabelas, gráficos, dialogs e estados assíncronos.
- Rotas existentes em `src/app/app.routes.ts`; nenhuma rota ou endpoint novo será presumido.
- Tokens e estilos globais, templates e estilos específicos de cada tela, mantendo Angular Material e a arquitetura atual.
- Testes, build de produção, inspeção visual em runtime, acessibilidade e validação responsiva.
