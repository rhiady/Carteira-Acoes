# investment-product-navigation Specification

## Purpose
Define a clear entry point and route-oriented navigation for an investment product whose primary tasks are reading portfolios, positions, quotations and operations.

## Requirements

### Requirement: Início orienta a leitura da carteira
The system SHALL provide an `Início` route as the primary entry point and SHALL expose direct navigation to Dashboard, Carteiras, Ações and Operações. The entry point MUST use only data already available from published resources and MUST show an explicit empty or unavailable state when financial data is absent.

#### Scenario: Pessoa abre o produto
- **WHEN** the user opens the root route
- **THEN** the system opens `Início` and provides contextual links to the main investment areas.

#### Scenario: Dados financeiros indisponíveis
- **WHEN** the entry point cannot obtain an authoritative portfolio value or result
- **THEN** it presents an unavailable or empty state and does not fabricate financial metrics.

### Requirement: Navegação distingue investimento e gestão
The system SHALL visually and semantically separate investment destinations from secondary management destinations while preserving keyboard access, active-route indication and responsive overlay behavior.

#### Scenario: Rota financeira ativa
- **WHEN** the user navigates to a portfolio or operation
- **THEN** the corresponding investment destination is visibly active and the current content remains reachable by keyboard.

#### Scenario: Viewport estreito
- **WHEN** the viewport is narrow
- **THEN** the navigation can be opened and closed as an accessible overlay without obscuring the main content after closing.
