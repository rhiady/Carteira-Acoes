## ADDED Requirements

### Requirement: Operation cash-flow visualization
The operation experience SHALL visualize complete published operation history as monthly cash flow and traded volume by asset, grouped by currency and using `valorLiquido`. Purchases SHALL be represented as outflow and sales as inflow; incomplete history SHALL be identified as partial.

#### Scenario: Operations by currency
- **WHEN** a wallet has BRL and USD operations
- **THEN** the cash-flow and volume views remain separate and provide accessible textual summaries.
