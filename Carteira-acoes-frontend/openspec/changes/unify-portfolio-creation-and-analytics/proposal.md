## Why

Os fluxos de criação de carteira para administradores e usuários comuns estão visualmente desalinhados, pouco responsivos e o fluxo comum falha em execução. A área administrativa também não oferece uma entrada clara para ações, carteiras e operações, enquanto os gráficos atuais têm leitura difícil, interação limitada e sinais de valores/escala incorretos. Esta mudança consolida a experiência usando somente os contratos já publicados e separa explicitamente BRL e USD.

## What Changes

- Unificar o formulário de criação de carteira da página e do diálogo administrativo para `ROLE_ADMIN` e `ROLE_USER`, sem seletor de usuário em nenhum dos dois fluxos. O frontend enviará o `usuarioId` obrigatório preenchido pela identidade autenticada; o backend continuará sendo a autoridade e ignorará qualquer valor que tente criar carteira para outra pessoa. A seleção de corretora publicada e o mesmo fluxo visual, responsivo, acessível e orientado por estados serão preservados.
- Corrigir o erro do fluxo de criação de carteira no limite entre rota, identidade autenticada, dependências e payload, removendo do diálogo administrativo a carga/listagem de usuários e qualquer possibilidade de selecionar outro proprietário.
- Adicionar à entrada/lateral administrativa atalhos explícitos para ações, carteiras e operações, respeitando as rotas e permissões existentes e sem inventar endpoints administrativos.
- Tornar a área de carteira e o histórico de operações capazes de filtrar análises por mercado `BRASIL`/`EUA`, mantendo os valores na moeda retornada (`BRL`/`USD`) e deixando indisponibilidades explícitas.
- Melhorar os gráficos de carteira e operações com composição responsiva, tooltips e rótulos acessíveis, estados vazios/erro, comparação legível e números derivados exclusivamente dos campos autoritativos retornados pela API.
- Preservar Angular Material, rotas lazy, tema claro/escuro, guardas por papel e os contratos de compra, venda, resumo, posições e operações já publicados.

## Capabilities

### New Capabilities

- `portfolio-market-analytics-experience`: filtro por mercado e apresentação interativa, correta e acessível de análises de carteira e operações.

### Modified Capabilities

- `creation-form-experience`: unificar e corrigir a criação de carteira na página e no `CarteiraDialogComponent`, com ownership implícito nos dois papéis, sem seletor de usuário e com redesign responsivo consistente.
- `investment-product-navigation`: ampliar a navegação administrativa com entradas para ações, carteiras e operações sem expor recursos a usuários comuns.
- `portfolio-analytics`: alinhar filtros, números, estados e interação dos gráficos ao contrato financeiro publicado.
- `trading-analytics-experience`: aplicar filtro de mercado e leitura correta aos gráficos/histórico de operações.
- `frontend-usability-and-visual-consistency`: exigir a mesma hierarquia, responsividade, foco e estados acessíveis nos fluxos e superfícies analíticas envolvidos.

## Impact

- UI/rotas: `CarteiraFormPage`, listagem/detalhe de carteiras, entrada administrativa, navegação compartilhada e páginas de operações/analytics.
- Serviços/modelos/mappers: reutilização e eventual correção de `AuthService`, `CarteiraService`, `OperacaoService`, `AcaoService`, modelos de `Mercado`/`Moeda` e mappers de analytics, sem criar contrato de API novo.
- Componentes compartilhados: painel de gráficos e estados de loading, vazio, erro e retry, mantendo Angular Material e acessibilidade WCAG AA.
- Testes: rotas e ownership por papel, criação com dependências, filtro BR/USD, mappers numéricos, gráficos, responsividade, tema e AXE.
- Backend: nenhuma alteração; se algum dado necessário não estiver publicado, a interface deverá declarar a dependência e não simular o valor.
