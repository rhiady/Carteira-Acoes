## MODIFIED Requirements

### Requirement: Contextual purchase
The purchase experience MUST preserve a wallet or asset supplied by the entry route when technically available and MUST use the `acaoId` of a registered action as its only asset reference. When no contextual asset is supplied, a person SHALL choose one registered action from the published action listing before entering the order. A common user SHALL remain within the selected own-wallet context and an administrator SHALL use the same registered-action selection without manually entering an identifier, ticker or market.

#### Scenario: Purchase starts from a wallet and asset
- **WHEN** the user chooses `Comprar ação` from a wallet position
- **THEN** the wallet and registered asset are preselected, the user can review quantity and price, and the request uses that asset's `acaoId`

#### Scenario: Purchase starts from an empty wallet
- **WHEN** a person opens purchase for an eligible wallet without a contextual asset
- **THEN** the interface lists registered actions and requires one selection before the order can be submitted

#### Scenario: Administrator starts a purchase
- **WHEN** an administrator opens a purchase for an eligible wallet
- **THEN** the interface allows choosing one registered action and does not expose manual ID, ticker or market inputs

#### Scenario: Administrator reaches a purchase
- **WHEN** an administrator opens the administrative area
- **THEN** the interface provides a visible route to that person's wallets and, from an eligible wallet, to the complete purchase flow

#### Scenario: Common user starts a purchase
- **WHEN** a common user opens purchase for an own wallet
- **THEN** the interface preserves that wallet context and permits selecting a registered action without exposing another user's wallet context
