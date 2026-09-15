## MODIFIED Requirements

### Requirement: Registro de venda
O sistema SHALL registrar uma venda somente para posição existente cuja quantidade seja suficiente e quando a quantidade for no mínimo `0.0001`. A venda SHALL requerer `carteiraId`, `acaoId` e quantidade; `precoUnitario`, corretagem, impostos e valor adicional serão campos opcionais. Quando `precoUnitario` for informado, ele MUST ser no mínimo `0.0001` e o sistema SHALL usá-lo para registrar a venda sem consultar cotação externa. Quando estiver ausente, o sistema SHALL obter uma cotação externa válida. A venda SHALL reduzir a posição sem removê-la, inclusive quando resultar em quantidade zero.

#### Scenario: Venda com preço informado
- **WHEN** um cliente vende uma quantidade disponível e informa `precoUnitario` válido
- **THEN** o sistema registra a venda com o preço informado sem consultar cotação externa

#### Scenario: Venda sem preço informado
- **WHEN** um cliente vende uma quantidade disponível sem informar `precoUnitario`
- **THEN** o sistema obtém cotação externa válida e a usa para registrar a venda

#### Scenario: Venda total
- **WHEN** um cliente vende exatamente toda a quantidade de uma posição
- **THEN** o sistema registra a venda e mantém a posição persistida com quantidade zero

#### Scenario: Venda acima da posição
- **WHEN** um cliente solicita venda com quantidade maior que a posição atual
- **THEN** o sistema responde 422 e não cria operação nem altera a posição

#### Scenario: Preço manual de venda inválido
- **WHEN** um cliente informa `precoUnitario` menor que `0.0001` em uma venda
- **THEN** o sistema responde com erro de validação e não cria operação nem altera a posição
