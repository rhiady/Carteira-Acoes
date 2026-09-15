## Purpose

Define clear, contextual and confirmable purchase and sale journeys that preserve the user's wallet, asset and position context while respecting the existing investment API contract.

## ADDED Requirements

### Requirement: Contextual purchase
The purchase experience MUST preserve a wallet or asset supplied by the entry route when technically available and MUST use only the permitted asset reference format from the existing contract.

#### Scenario: Purchase starts from a wallet and asset
- **WHEN** the user chooses `Comprar ação` from a wallet position
- **THEN** the wallet and asset are preselected, the user can review quantity and price, and the request does not send conflicting asset identifiers

### Requirement: Purchase confirmation
Before submitting a purchase, the interface MUST show a clear summary of wallet, asset, quantity, unit price and estimated operation value when those values are available.

#### Scenario: User reviews a purchase
- **WHEN** quantity and price are valid
- **THEN** the interface shows a review state with an explicit `Confirmar compra` action

### Requirement: Contextual sale
The sale experience MUST preserve the wallet and position supplied by the entry route when available, show known quantity available and prevent an obviously excessive quantity before submission.

#### Scenario: User sells part of a position
- **WHEN** a position has 15 available shares and the user enters 5
- **THEN** the interface shows the available amount, accepts the partial sale and presents an explicit confirmation action

### Requirement: Trading processing and errors
Purchase and sale submissions MUST indicate processing, block duplicate requests, preserve useful form context on failure and show a backend message when appropriate or a friendly fallback otherwise.

#### Scenario: Trading request fails
- **WHEN** the backend rejects a purchase or sale
- **THEN** the interface keeps the user on the flow, presents the error without technical payload details and allows retry
