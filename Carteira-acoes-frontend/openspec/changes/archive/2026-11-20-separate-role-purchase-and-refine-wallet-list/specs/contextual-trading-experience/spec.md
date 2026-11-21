## MODIFIED Requirements

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
