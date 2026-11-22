## MODIFIED Requirements

### Requirement: Layout usa espaço e densidade de forma consistente

The system SHALL use a coherent spacing scale, containers appropriate to each screen, typographic hierarchy and groupings that reduce redundant cards, borders and boxes without compressing financial content. The wallet page and administrative wallet dialog SHALL receive the same intentional redesign: editorial heading/context, grouped fields, readable helper/error states, stable actions and a one-column mobile composition. Neither surface SHALL expose a user selector. Analytics SHALL place the market filter, selected currency, primary metrics, charts and textual alternatives in a scanable hierarchy, with a single visually dominant action where an action exists.

#### Scenario: Criação de carteira em desktop e mobile
- **WHEN** a pessoa abre a criação em viewport amplo ou estreito
- **THEN** the form preserves the same reading order, proportions and primary action, stacking without horizontal overflow on mobile

#### Scenario: Analytics em desktop
- **WHEN** a financial analytics page is displayed in a wide viewport
- **THEN** market selection and the selected currency are clear before charts, and primary values and results precede secondary details

#### Scenario: Analytics em mobile
- **WHEN** a financial analytics page is displayed in a narrow viewport
- **THEN** filters, charts, legends and textual summaries stack in reading order without clipped labels or inaccessible controls

#### Scenario: Dashboard em desktop
- **WHEN** o dashboard é exibido em viewport amplo
- **THEN** patrimônio, resultado, capital investido, posições principais e ações rápidas ocupam a hierarquia visual antes de métricas secundárias

#### Scenario: Compra em desktop
- **WHEN** uma pessoa abre a compra em viewport amplo
- **THEN** identifica a referência do ativo, os dados da ordem, o total estimado e a confirmação em regiões proporcionais e legíveis

#### Scenario: Compra em mobile
- **WHEN** uma pessoa abre a compra em viewport estreito
- **THEN** as mesmas regiões se organizam em uma única sequência vertical sem sobreposição, corte ou rolagem horizontal

#### Scenario: Carteiras em viewport estreito
- **WHEN** uma pessoa consulta suas carteiras em viewport estreito
- **THEN** nome, valores por moeda, resultados, ativos e ação para abrir a carteira permanecem em ordem de leitura e sem rolagem horizontal

### Requirement: Navegação e tabelas são escaneáveis

The navigation SHALL use clear names, identifiable active state and an equivalent desktop/mobile strategy. Financial tables and chart summaries SHALL align identifiers to the left, numbers to the right, use discreet separators and provide perceptible hover/focus without excessive lines.

#### Scenario: Pessoa consulta operações filtradas
- **WHEN** a pessoa consulta operações por mercado
- **THEN** the selected filter, market, currency, ticker and numeric values are visible in a consistent reading order and no technical identifier dominates the task

#### Scenario: Usuário navega no mobile
- **WHEN** a pessoa usa a navegação e consulta uma tabela em viewport estreito
- **THEN** encontra a rota ativa, ações principais e dados essenciais sem overflow horizontal acidental ou controles sem nome

### Requirement: Estados e dialogs são consistentes e orientam continuidade

Loading, empty, error, success, disabled, forms and dialogs SHALL preserve layout, explain the next step when applicable, maintain focus and remain connected to the active theme. Wallet creation and analytics SHALL retain their heading/context during dependency errors, unavailable metrics and retryable failures.

#### Scenario: Dados analíticos indisponíveis
- **WHEN** a market filter has no authoritative data or a dependency fails
- **THEN** the page keeps its context, states what is unavailable and offers a safe next step without stale or fabricated values

#### Scenario: Coleção vazia
- **WHEN** não há carteiras, posições ou resultados
- **THEN** a interface explica a situação e apresenta a próxima ação relevante quando ela existir
