## Why

Investors need a clearer, more visual way to enter the product, inspect portfolios and understand operations without treating client-side estimates as financial truth. The current Swiss presentation does not meet the requested dark redesign or expose all published analytics.

## What Changes

- Redesign login, wallet and operation surfaces with the confirmed Industrial visual direction and a purpose-built dark theme.
- Add authoritative wallet and operation visualizations: investment versus current value, realized versus unrealized result, monthly cash flow and traded volume, separated by currency.
- Preserve existing access, trading, API and accessibility behavior; do not add total-portfolio evolution without a backend endpoint.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `investment-frontend-experience`: replace the visual language and refine login, wallet and operation surfaces.
- `portfolio-analytics`: expose additional published wallet-summary visualizations by currency.
- `trading-analytics-experience`: expose published operation cash-flow and volume visualizations without fabricating financial results.

## Impact

Changes login, wallet, portfolio-detail and operations/history UI, theme tokens, typed summary fields, existing mappers/services and tests. Reuses published endpoints; no new dependency or backend calculation.
