## MODIFIED Requirements

### Requirement: Composição por ação

The system SHALL show portfolio composition as a donut where every segment represents one stock and its authoritative current position value within the selected market and currency. It MUST NOT group the primary composition by broker, market or operation type, and MUST not render a segment for a missing authoritative value.

#### Scenario: Composição filtrada por mercado
- **WHEN** a pessoa seleciona Brasil ou EUA e existem posições completas nesse mercado
- **THEN** o donut e sua legenda identificam somente os tickers daquele mercado, com proporção baseada no valor atual retornado na moeda correspondente

#### Scenario: Dados insuficientes para composição
- **WHEN** o filtro selecionado não possui posições ou valores atuais autoritativos
- **THEN** a interface mostra estado vazio/indisponível e nenhum percentual ou segmento inventado

#### Scenario: Dados de composição disponíveis
- **WHEN** uma carteira possui posições com valores atuais autoritativos em uma moeda
- **THEN** o donut e sua legenda identificam cada ticker e sua proporção do total representado naquela moeda

### Requirement: Authoritative wallet summary comparisons

For each returned currency and selected market, the wallet SHALL visualize current versus invested value and realized versus unrealized result from the published summary or position fields, alongside textual alternatives. It MUST NOT combine currencies, convert values in the browser or replace missing values with zero.

#### Scenario: Resumo em BRL e USD
- **WHEN** uma carteira retorna resumos em BRL e USD e a pessoa alterna o mercado
- **THEN** cada seleção mostra somente o subtotal compatível, com rótulo da moeda, sem total consolidado

#### Scenario: Resumo sem valor
- **WHEN** um campo autoritativo de comparação é nulo
- **THEN** o gráfico e o texto exibem indisponível, sem desenhar uma barra de zero que pareça um valor real

#### Scenario: Multi-currency summary
- **WHEN** uma wallet returns BRL and USD summaries
- **THEN** each currency has independent comparison and result visualizations

### Requirement: Dashboard financeiro conservador

The dashboard SHALL prioritize portfolio value, lucro/prejuízo, asset count and asset-based charts over administrative counts. It MUST render “—” or an empty state for each unavailable authoritative metric and MUST NOT synthesize financial values. The Dashboard SHALL present these metrics and charts in a clear investment-oriented hierarchy with explicit market/currency sections and a contextual market filter.

#### Scenario: Dashboard com múltiplos mercados
- **WHEN** a carteira contém dados de Brasil e EUA
- **THEN** o dashboard oferece o filtro de mercado e mantém cada seção com sua moeda, sem combinação ou conversão

#### Scenario: Gráfico disponível
- **WHEN** há posições autoritativas para o mercado selecionado
- **THEN** composição e resultado permanecem visíveis, interativos e acompanhados de alternativa textual

#### Scenario: Métrica indisponível
- **WHEN** a carteira selecionada não possui resultado ou valor autoritativo
- **THEN** o card relacionado omite o valor numérico e apresenta um estado indisponível

#### Scenario: Dashboard com múltiplas moedas
- **WHEN** a carteira selecionada contém analytics em BRL e USD
- **THEN** o dashboard apresenta seções separadas por moeda e não combina, converte ou sugere um total consolidado
