## MODIFIED Requirements

### Requirement: Purchase confirmation
Before submitting a purchase, the interface MUST show a clear summary of wallet, asset, quantity, unit price and estimated operation value when those values are available. The purchase flow SHALL make asset reference, order values, optional costs, estimated total and confirmation legible as one sequential decision path, with a single visually dominant submit action.

#### Scenario: User reviews a purchase
- **WHEN** quantity and price are valid
- **THEN** the interface shows a review state with an explicit `Confirmar compra` action

#### Scenario: User reviews a purchase on a narrow viewport
- **WHEN** a person fills a purchase order on a narrow viewport
- **THEN** asset reference, order fields, optional costs, estimated total and confirmation remain visible in reading order without horizontal overflow

