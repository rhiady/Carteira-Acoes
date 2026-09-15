# investment-focused-experience Specification

## Purpose
Establish a responsive financial-product experience in which portfolios, positions, quotations and results take precedence over administrative CRUD presentation.
## Requirements
### Requirement: Hierarquia financeira e espaçamento consistente
The system SHALL use the declared spacing scale from 4px through 48px and a desktop content container no wider than 1440px with 32px padding. Dashboard, portfolio and stock screens SHALL visually prioritize financial values and actions over administrative information. The shell, cards, lists, dialogs and operation history SHALL use the same Swiss / financial editorial language with neutral surfaces, fine rules, restrained elevation and compact left-aligned typography.

#### Scenario: Visualização desktop
- **WHEN** a user opens a financial page on a wide viewport
- **THEN** its content is contained within the defined desktop width and primary investment information is more prominent than administrative metadata.

#### Scenario: Navegação entre superfícies
- **WHEN** a user moves between Dashboard, Carteiras, Operações and Ações
- **THEN** headings, financial values, actions and feedback use consistent visual tokens and hierarchy.

### Requirement: Carteiras e posições informativas
The system SHALL present portfolios as informative cards containing broker, value, lucro/prejuízo, asset count and available tickers when authoritative data exists. A portfolio detail SHALL provide contextual buy, sell and quote-refresh actions and present each position as a spacious, readable investment item with current quotation, position value, average sale price and result.

#### Scenario: Carteira com posições
- **WHEN** a user opens a populated portfolio
- **THEN** the interface provides its financial summary, asset charts and contextual actions alongside accessible position details.

### Requirement: Compra e venda contextualizadas
The system SHALL start purchase and sale from a portfolio or position context, identify the selected portfolio and asset, offer optional additional costs, and show an estimate explicitly labeled non-authoritative. A sale MUST identify available quantity and require explicit confirmation before submission.

#### Scenario: Venda iniciada pela posição
- **WHEN** a user chooses to sell a position
- **THEN** the form identifies the ticker and available quantity, rejects quantities above availability and asks for confirmation before sending the request.

### Requirement: Gráficos e controles responsivos
The system SHALL show two analytics charts side by side only when their available width permits it and SHALL stack them on tablet and mobile. Charts and position actions MUST remain operable without page-level horizontal scrolling or keyboard traps.

#### Scenario: Visualização em dispositivo estreito
- **WHEN** a user opens a portfolio or stock page on a narrow viewport
- **THEN** charts stack in one column and all chart labels, position details and actions remain visible and keyboard-accessible.

### Requirement: Linguagem visual de resultados
The system SHALL use discreet positive and negative visual treatments for results, while quotations, current values and average prices remain neutral. Tooltips for lucro/prejuízo and average sale price SHALL explain their calculation or unavailable state.

#### Scenario: Informação financeira assistida
- **WHEN** a user focuses or opens help for a calculated metric
- **THEN** a tooltip explains the metric without relying on color to convey its meaning.

### Requirement: Comparação de carteiras preserva moedas
The system SHALL present portfolio comparison values with explicit currency identity and MUST NOT convert or combine values from different currencies in the frontend.

#### Scenario: Carteiras em moedas diferentes
- **WHEN** compared portfolios contain BRL and USD values
- **THEN** each value remains in its own currency and no consolidated comparison is presented.

### Requirement: Desempenho do ativo respeita disponibilidade
The system SHALL present asset performance using only authoritative quotation, position and result fields available from the API. It MUST render unavailable position values as unavailable and MUST NOT simulate a position for an asset absent from the portfolio.

#### Scenario: Ativo sem posição
- **WHEN** an asset is not part of the selected portfolio
- **THEN** the interface presents the asset information without fabricated quantity, value or result metrics.

### Requirement: Rotas legadas compartilham a experiência visual
The system SHALL audit existing routes for raw field stacking, generic detail actions, technical identifiers as primary content, uncustomized Material surfaces, inconsistent spacing or excessive empty space, and SHALL modernize relevant routes before completion.

#### Scenario: Rota legada identificada
- **WHEN** a route presents one or more legacy visual characteristics
- **THEN** it is redesigned with the same visual system or its simple detail is replaced by a contextual dialog.
