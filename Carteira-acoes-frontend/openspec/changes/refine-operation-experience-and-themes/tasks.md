## 1. Foundation de temas

- [x] 1.1 Inventariar consumidores dos tokens existentes e consolidar em `src/styles.css` uma base clara e overrides escuros sem declarações conflitantes.
- [x] 1.2 Preservar `ThemeService` e o alternador atual, verificando preferência persistida, `color-scheme` e propagação do tema para o container de overlays.
- [x] 1.3 Aplicar tokens semânticos aos controles e superfícies do Angular Material (botões, campos, select, tabela, dialog, menu, snackbar e foco), com estados default, hover, active, focus-visible e disabled legíveis nos dois temas.

## 2. Fluxos de operação

- [x] 2.1 Refinar a composição existente de compra para apresentar contexto do ativo/carteira, ordem, custos opcionais, revisão estimada e confirmação em sequência clara, mantendo os controles, validações e payload atuais.
- [x] 2.2 Refinar a composição existente de venda para apresentar dados autoritativos disponíveis da posição antes da ordem, custos, estimativa líquida e confirmação, sem inferir dados ausentes.
- [x] 2.3 Aplicar o review rail responsivo: revisão contextual persistente em viewport amplo e ordem semântica vertical em viewport estreito, sem criar uma nova rota ou árvore de componentes.
- [x] 2.4 Reorganizar os itens do histórico, detalhe e diálogo de operação para destacar tipo, ativo, valores, custos e data, preservando moeda, campos autoritativos, `—` e a ação de inspeção existente.

## 3. Acessibilidade e regressão

- [x] 3.1 Atualizar testes de tema para cobrir claro, escuro, persistência, overlay e retenção de estado dos formulários durante a alternância.
- [x] 3.2 Atualizar testes focados de compra, venda, histórico e detalhe para a hierarquia revisada, estimativas não autoritativas, indisponibilidade e ausência de mudança nos requests.
- [x] 3.3 Executar testes Angular/Vitest relevantes, build e checagem de tipos; corrigir regressões sem alterar modelos, endpoints, rotas ou cálculos financeiros.
- [x] 3.4 Executar AXE e inspeção visual em claro/escuro, desktop/mobile, incluindo foco por teclado, erro, pending, disabled, menus e dialogs; confirmar contraste WCAG AA e ausência de overflow horizontal.
