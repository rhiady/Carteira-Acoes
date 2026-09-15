# financial-analysis-presentation Specification

## Purpose

Define how the frontend presents real portfolio and position data without fabricating unsupported financial metrics, while making authoritative and derived values easy to interpret.

## Requirements

### Requirement: Authoritative financial values
The frontend MUST present backend-provided price, value and result fields as authoritative and MUST NOT replace them with locally recalculated totals.

#### Scenario: Backend provides position results
- **WHEN** a position includes average price, current value, realized result, unrealized result or total result
- **THEN** the interface presents those values with their correct sign, currency and semantic label

### Requirement: Safe derived metrics
The frontend MUST calculate presentation metrics only when their operands are available and their formula is defined, such as variation against average price or position weight over a complete position set.

#### Scenario: Position variation is displayed
- **WHEN** average price is greater than zero and current price is available
- **THEN** the interface may show `(current - average) / average × 100` without changing the authoritative result fields

### Requirement: Financial semantics
The interface MUST distinguish total, realized and unrealized results when those fields exist, MUST preserve BRL and USD as separate currencies, and MUST show a null average sale price as unavailable rather than zero.

#### Scenario: Position has no sale history
- **WHEN** `precoMedioVenda` is null
- **THEN** the interface displays an unavailable state such as `—` and does not display `R$ 0,00`

### Requirement: Unsupported metrics are omitted
The frontend MUST NOT present historical profitability, benchmarks, dividends, volatility, Sharpe, drawdown, projections or other metrics unless the required real data is supplied by the backend.

#### Scenario: Data for a metric is unavailable
- **WHEN** the backend does not provide the operands required for a financial metric
- **THEN** the interface omits that metric or clearly presents the supported data without inventing a value
