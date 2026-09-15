# portfolio-analytics Specification

## Purpose
Define métricas financeiras autoritativas para carteiras e posições e apresentá-las em resumos e visualizações que expliquem concentração e resultado por ativo.
## Requirements
### Requirement: Métricas autoritativas de carteira e posição
The system SHALL consume `GET /carteiras/{id}/acoes` as a page of positions containing `carteiraId`, `acaoId`, `ticker`, `nomeEmpresa`, `mercado`, `moeda`, `quantidade`, `cotacaoAtual`, `dataHoraCotacao`, `valorAtual`, `precoMedioVenda`, `lucroPrejuizoRealizado`, `lucroPrejuizoNaoRealizado` and `lucroPrejuizo`. It MUST present these authoritative values in the position's own currency and MUST NOT recalculate them. Preço médio de venda MUST display “—” when the response value is absent and MUST NOT be represented as zero. A negative result MUST use a leading minus before the formatted currency amount.

#### Scenario: Posição com resultado negativo
- **WHEN** a portfolio response supplies a negative result for a position
- **THEN** the interface displays “- R$ …” or “- US$ …” with an accessible textual label and does not display “R$ -…”.

#### Scenario: Posição sem venda anterior
- **WHEN** the authoritative position response has no average sale price
- **THEN** the interface displays “—” and explains that no sale was registered.

### Requirement: Resumo de carteira por moeda
The system SHALL consume `GET /carteiras/{id}/resumo` with `carteiraId`, `nome`, `quantidadeAtivos` and `resumosPorMoeda`. Each currency summary SHALL present its own `moeda`, `valorAtual` and `lucroPrejuizo` in that currency. When more than one currency is present, the interface MUST render separate subtotals and MUST NOT show a consolidated portfolio total or perform currency conversion.

#### Scenario: Carteira multimoeda
- **WHEN** a portfolio summary contains BRL and USD entries in `resumosPorMoeda`
- **THEN** the interface displays independent BRL and USD value/result subtotals without a combined amount.

### Requirement: Composição por ação
The system SHALL show portfolio composition as a donut where every segment represents one stock and its authoritative current position value. It MUST NOT group the primary composition by broker, market or operation type.

#### Scenario: Dados de composição disponíveis
- **WHEN** a portfolio has positions with authoritative current values in one currency
- **THEN** the donut and its accessible legend identify each ticker and its proportion of that currency's represented total.

#### Scenario: Dados insuficientes para composição
- **WHEN** a portfolio has no positions or no authoritative values
- **THEN** the interface shows a graph empty state and no fabricated segments or percentages.

### Requirement: Resultado por ação
The system SHALL show the result of each stock in a portfolio as a horizontal comparison and distinguish positive and negative values without relying solely on color.

#### Scenario: Resultados mistos
- **WHEN** portfolio positions contain positive and negative results
- **THEN** each item presents its ticker, signed formatted result and a visually distinct positive or negative treatment.

### Requirement: Dashboard financeiro conservador
The dashboard SHALL prioritize portfolio value, lucro/prejuízo, asset count and asset-based charts over administrative counts. It MUST render “—” or an empty state for each unavailable authoritative metric and MUST NOT synthesize financial values. The Dashboard SHALL present these metrics and charts in a clear investment-oriented hierarchy with explicit currency sections and contextual actions.

#### Scenario: Métrica indisponível
- **WHEN** the selected portfolio lacks an authoritative result or value
- **THEN** the related dashboard card omits a numeric value and presents an unavailable state.

#### Scenario: Dashboard com múltiplas moedas
- **WHEN** the selected portfolio contains BRL and USD analytics
- **THEN** the Dashboard presents separate currency sections and does not combine, convert or imply a consolidated total.

#### Scenario: Gráfico disponível
- **WHEN** authoritative positions are available
- **THEN** the composition and result charts remain visible, have contextual titles and retain their accessible textual alternatives.
