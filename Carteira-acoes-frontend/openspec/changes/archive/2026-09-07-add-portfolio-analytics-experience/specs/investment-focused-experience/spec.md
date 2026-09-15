## Purpose

Establish a responsive financial-product experience in which portfolios, positions, quotations and results take precedence over administrative CRUD presentation.

## ADDED Requirements

### Requirement: Hierarquia financeira e espaçamento consistente
The system SHALL use the declared spacing scale from 4px through 48px and a desktop content container no wider than 1440px with 32px padding. Dashboard, portfolio and stock screens SHALL visually prioritize financial values and actions over administrative information.

#### Scenario: Visualização desktop
- **WHEN** a user opens a financial page on a wide viewport
- **THEN** its content is contained within the defined desktop width and primary investment information is more prominent than administrative metadata.

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
