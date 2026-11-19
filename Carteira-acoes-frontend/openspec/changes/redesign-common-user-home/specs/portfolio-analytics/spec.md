## MODIFIED Requirements

### Requirement: Dashboard financeiro conservador
The canonical common-user home SHALL prioritize selected portfolio context, current value, lucro/prejuízo, asset count and asset-based charts over route shortcuts or administrative counts. It MUST render “—” or a localized empty/unavailable state for each unavailable authoritative metric and MUST NOT synthesize financial values. Metrics and charts SHALL appear in a clear investment-oriented hierarchy with explicit currency sections, a visible portfolio selector when needed and contextual purchase/open-portfolio actions.

#### Scenario: Métrica indisponível
- **WHEN** the selected portfolio lacks an authoritative result or value
- **THEN** the related summary region omits a numeric value, presents an unavailable state and leaves other valid data visible

#### Scenario: Dashboard com múltiplas moedas
- **WHEN** the selected portfolio contains BRL and USD analytics
- **THEN** the home presents separate currency sections and does not combine, convert or imply a consolidated total

#### Scenario: Gráfico disponível
- **WHEN** authoritative positions are available
- **THEN** composition and result charts are visible in the canonical home, have contextual titles and retain accessible textual alternatives

#### Scenario: Carteira em foco muda
- **WHEN** the person selects another portfolio
- **THEN** summary metrics, chart data, labels and contextual actions update together and do not mix data from different portfolios

