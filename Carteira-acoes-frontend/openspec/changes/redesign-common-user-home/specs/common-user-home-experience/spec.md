## Purpose

Provide a canonical, visual and data-rich home for authenticated common users that exposes their real portfolio context, actions and analytics without intermediary navigation screens.

## ADDED Requirements

### Requirement: Common user enters the financial home directly

The system SHALL use `/inicio` as the canonical post-login destination for `ROLE_USER`. The legacy `/usuario` route SHALL redirect to `/minha-conta`, and `/dashboard` SHALL redirect to or be consolidated into `/inicio` so the common-user experience does not split equivalent summary and analytics content across competing destinations.

#### Scenario: Common user completes login
- **WHEN** authentication resolves a person with `ROLE_USER`
- **THEN** the application opens `/inicio` without first showing an intermediary route-selection page

#### Scenario: Legacy personal route is opened
- **WHEN** an authenticated common user opens `/usuario`
- **THEN** the application redirects to `/minha-conta` and preserves the authenticated session

#### Scenario: Legacy dashboard route is opened
- **WHEN** an authenticated common user opens `/dashboard`
- **THEN** the application reaches the canonical home analytics experience without presenting duplicate navigation or conflicting summaries

### Requirement: Home exposes real portfolio context in the first reading

The common-user home SHALL identify the authenticated account, provide a portfolio selector when more than one portfolio exists, display authoritative asset count, current value and result separated by currency, and expose one visually dominant contextual action. It MUST NOT hide the primary summary behind an additional route or fabricate consolidated values.

#### Scenario: User has portfolios with summaries
- **WHEN** the common-user home loads one or more portfolios with authoritative summaries
- **THEN** the selected portfolio, currency-separated metrics, primary action and analytics are visible in the main reading order

#### Scenario: User has multiple portfolios
- **WHEN** more than one portfolio is available
- **THEN** the person can change the portfolio in focus and the visible summary and charts update to that portfolio without losing the page context

#### Scenario: User has no portfolio
- **WHEN** the authenticated person has no portfolio
- **THEN** the home explains the empty state and presents `Criar carteira` as the primary action without displaying fake values or charts

### Requirement: Home charts are prominent, interpretable and accessible

The common-user home SHALL present portfolio composition and result charts as primary financial content when authoritative position data exists. Charts SHALL use explicit currency context, readable labels, visual differentiation beyond color, accessible textual alternatives and useful loading, empty and error states.

#### Scenario: Analytics are available
- **WHEN** the selected portfolio has sufficient authoritative position data
- **THEN** composition and result charts appear in the main home flow with titles, legends or labels and equivalent textual values

#### Scenario: Analytics are unavailable
- **WHEN** position data is incomplete, unavailable or empty
- **THEN** each chart region explains its state and keeps the portfolio summary and available actions usable

### Requirement: Common-user utilities remain continuously reachable

The common-user shell SHALL keep `Minha conta`, the authenticated identity and `Sair` grouped as personal utilities distinct from investment destinations. Logout SHALL remain keyboard reachable and SHALL clear the complete session before redirecting to `/login`.

#### Scenario: User navigates investment routes
- **WHEN** a common user moves between home, portfolios, operations and stocks
- **THEN** account identity, `Minha conta` and `Sair` remain discoverable without competing with the current page's primary action

