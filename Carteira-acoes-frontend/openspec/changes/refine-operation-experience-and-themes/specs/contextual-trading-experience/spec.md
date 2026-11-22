## MODIFIED Requirements

### Requirement: Purchase confirmation

Before submitting a purchase, the interface MUST show a clear summary of wallet, asset, quantity, unit price and estimated operation value when those values are available. The purchase flow SHALL organize asset reference, order values, optional costs, estimated total and confirmation as a progressive, scannable decision path, with one visually dominant submit action. The estimate MUST be identified as non-authoritative and SHALL NOT replace the values confirmed by the API.

#### Scenario: User reviews a purchase
- **WHEN** quantity and price are valid
- **THEN** the interface shows a review state with asset, order and estimated total context plus an explicit `Confirmar compra` action

#### Scenario: User reviews a purchase on a narrow viewport
- **WHEN** a person fills a purchase order on a narrow viewport
- **THEN** asset reference, order fields, optional costs, estimated total and confirmation remain visible in reading order without horizontal overflow

### Requirement: Contextual sale

The sale experience MUST preserve the wallet and position supplied by the entry route when available, show known quantity available and prevent an obviously excessive quantity before submission. It SHALL present the selected asset and available authoritative position data before the quantity, optional costs, estimated net amount and explicit confirmation, without implying that a local estimate is authoritative.

#### Scenario: User sells part of a position
- **WHEN** a position has 15 available shares and the user enters 5
- **THEN** the interface shows the available amount, accepts the partial sale and presents an explicit confirmation action

#### Scenario: Sale context is incomplete
- **WHEN** an optional authoritative position value is unavailable
- **THEN** the interface shows it as unavailable while preserving the available quantity, editable order and confirmation path
