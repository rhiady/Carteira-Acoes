## MODIFIED Requirements

### Requirement: Carteiras e posições informativas
The system SHALL present portfolios as informative cards containing broker, value, lucro/prejuízo, asset count and available tickers when authoritative data exists. A portfolio detail SHALL provide contextual buy, sell and quote-refresh actions and present each position as a spacious, readable investment item with current quotation, position value, average sale price and result. The portfolio list SHALL prioritize portfolio name, currency-separated authoritative values, result, asset count, ticker preview and a clear continuation action; it MUST avoid making technical identifiers the primary reading path or repeating decorative surfaces.

#### Scenario: Carteira com posições
- **WHEN** a user opens a populated portfolio
- **THEN** the interface provides its financial summary, asset charts and contextual actions alongside accessible position details.

#### Scenario: Lista de carteiras com valores por moeda
- **WHEN** a person has more than one wallet or a wallet with multiple currencies
- **THEN** each wallet row or card keeps BRL and USD values separate, emphasizes its financial context and offers an identifiable action to open that wallet
