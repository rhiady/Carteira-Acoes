## ADDED Requirements

### Requirement: Authoritative wallet summary comparisons
For each returned currency, the wallet SHALL visualize current versus invested value and realized versus unrealized result from the published summary fields, alongside textual alternatives. It MUST NOT combine currencies or replace missing values with zero.

#### Scenario: Multi-currency summary
- **WHEN** a wallet returns BRL and USD summaries
- **THEN** each currency has independent comparison and result visualizations.
