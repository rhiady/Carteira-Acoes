## MODIFIED Requirements

### Requirement: Compra cria ou reutiliza o ativo conforme o contrato
The application SHALL submit purchases to `/operacoes/compras` with `carteiraId`, `acaoId` and `quantidade` of at least `0.0001`. It MUST obtain `acaoId` from a registered action returned by the published action listing and MUST NOT send `ticker` or `mercado` in a purchase request. It SHALL include `precoUnitario` only when it is at least `0.0001` and charges only when each supplied charge is non-negative. It MUST prevent submission without a carteira or registered action selection and MUST treat the returned `OperacaoResponse` amounts as official rather than any client estimate.

#### Scenario: Compra de ação cadastrada
- **WHEN** the user selects a registered action, a carteira and a valid quantidade
- **THEN** the application submits the selected action's `acaoId` to the purchase endpoint and confirms the returned operation

#### Scenario: Compra de ativo ainda inexistente
- **WHEN** the desired asset is not present in the published action listing
- **THEN** the application does not offer a ticker/market fallback and prevents a purchase until the asset is registered

#### Scenario: Compra sem ação selecionada
- **WHEN** the user has not selected a registered action
- **THEN** the application prevents submission and identifies that an action selection is required

#### Scenario: Compra não envia referência legada
- **WHEN** the application submits a purchase
- **THEN** the request body contains `acaoId` and does not contain `ticker` or `mercado`

#### Scenario: Compra possui referências de ativo conflitantes
- **WHEN** a purchase form is displayed
- **THEN** it exposes one registered-action selection only, so the person cannot provide `acaoId` together with ticker or mercado

#### Scenario: Compra concluída atualiza a carteira
- **WHEN** a purchase returns `201 Created`
- **THEN** the application closes the operation form, confirms success, and refreshes that carteira's positions and operation history
