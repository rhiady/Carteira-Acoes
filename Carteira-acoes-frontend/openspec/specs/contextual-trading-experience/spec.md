# contextual-trading-experience Specification

## Purpose

Define clear, contextual and confirmable purchase and sale journeys that preserve the user's wallet, asset and position context while respecting the existing investment API contract.

## Requirements

### Requirement: Contextual purchase
The purchase experience MUST preserve a wallet or asset supplied by the entry route when technically available and MUST use only the permitted asset reference format from the existing contract. An administrator SHALL receive the complete asset-reference flow, including an explicit choice of existing asset ID or ticker with market. A common user SHALL receive a contextual list of positions from the selected own wallet and start a purchase from one listed asset, without manually choosing an asset identifier or market.

#### Scenario: Purchase starts from a wallet and asset
- **WHEN** the user chooses `Comprar ação` from a wallet position
- **THEN** the wallet and asset are preselected, the user can review quantity and price, and the request does not send conflicting asset identifiers

#### Scenario: Administrator starts a purchase
- **WHEN** an administrator opens a purchase for an eligible wallet
- **THEN** the interface allows choosing either an existing asset by ID or a ticker together with its market before entering the order

#### Scenario: Administrator reaches a purchase
- **WHEN** an administrator opens the administrative area
- **THEN** the interface provides a visible route to that person's wallets and, from an eligible wallet, to the complete purchase flow

#### Scenario: Common user starts a purchase
- **WHEN** a common user opens purchase for an own wallet with positions
- **THEN** the interface lists only those positions and lets the person choose one before entering the order

### Requirement: Purchase confirmation
Before submitting a purchase, the interface MUST show a clear summary of wallet, asset, quantity, unit price and estimated operation value when those values are available. The purchase flow SHALL make asset reference, order values, optional costs, estimated total and confirmation legible as one sequential decision path, with a single visually dominant submit action.

#### Scenario: User reviews a purchase
- **WHEN** quantity and price are valid
- **THEN** the interface shows a review state with an explicit `Confirmar compra` action

#### Scenario: User reviews a purchase on a narrow viewport
- **WHEN** a person fills a purchase order on a narrow viewport
- **THEN** asset reference, order fields, optional costs, estimated total and confirmation remain visible in reading order without horizontal overflow

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
