## MODIFIED Requirements

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
