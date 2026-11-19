## MODIFIED Requirements

### Requirement: Início orienta a leitura da carteira
The system SHALL provide `/inicio` as the canonical common-user entry point and SHALL place the selected portfolio summary, primary action and available analytics in its main reading order. It SHALL expose direct navigation to Carteiras, Operações and Ações, and SHALL keep `Minha conta` as a personal utility rather than an investment destination. The entry point MUST use only data already available from published resources and MUST show an explicit empty or unavailable state when financial data is absent.

#### Scenario: Pessoa abre o produto
- **WHEN** a common user completes login or follows the product brand link
- **THEN** the system opens `/inicio` and presents the selected portfolio context before secondary navigation

#### Scenario: Dados financeiros indisponíveis
- **WHEN** the entry point cannot obtain an authoritative portfolio value, result or analytics set
- **THEN** it preserves every available context, presents an unavailable or empty state for the affected region and does not fabricate financial metrics

#### Scenario: Pessoa acessa dashboard legado
- **WHEN** the person opens `/dashboard`
- **THEN** the application redirects to or renders the canonical `/inicio` experience without maintaining a second competing summary page

### Requirement: Navegação distingue investimento e gestão
The system SHALL visually and semantically separate primary investment destinations from personal utilities, preserve keyboard access and active-route indication, and use responsive overlay behavior. For `ROLE_USER`, navigation SHALL contain one destination for the financial home and MUST NOT duplicate home, dashboard or role-entry links that lead to equivalent content.

#### Scenario: Rota financeira ativa
- **WHEN** the user navigates to home, a portfolio, operation or stock
- **THEN** the corresponding investment destination is visibly active and the current content remains reachable by keyboard

#### Scenario: Viewport estreito
- **WHEN** the viewport is narrow
- **THEN** the navigation can be opened and closed as an accessible overlay, account/logout utilities remain reachable, and closing the overlay restores unobscured access to main content

