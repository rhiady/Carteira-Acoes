## MODIFIED Requirements

### Requirement: Registro de compra
O sistema SHALL registrar uma compra somente para carteira existente quando a quantidade for no mínimo `0.0001`. A compra SHALL identificar uma ação existente por `acaoId` OU informar conjuntamente `ticker` e `mercado`; MUST rejeitar identificação ausente, combinação parcial de ticker/mercado ou as duas formas de identificação na mesma requisição. Quando `acaoId` for informado, a ação deverá existir. Quando ticker e mercado forem informados, o sistema SHALL reutilizar a ação correspondente se ela existir ou criá-la a partir do provedor de mercado, dentro da mesma transação da compra. Se o preço unitário for informado, SHALL usá-lo; se estiver ausente, SHALL obter cotação válida externamente. Corretagem, impostos e valor adicional serão custos opcionais. Uma compra bem-sucedida SHALL persistir a operação e criar ou aumentar somente a posição da ação na carteira informada, registrar os valores bruto e líquido calculados e responder HTTP 201 com o identificador da operação. Quando não puder ser persistida, a API SHALL retornar um erro padronizado que identifique a categoria da falha e MUST NOT retornar sucesso.

#### Scenario: Compra de ação existente por identificador
- **WHEN** um cliente compra quantidade positiva para uma carteira existente informando `acaoId` válido e preço unitário válido
- **THEN** o sistema registra a compra e aumenta somente a posição daquela carteira sem consultar cotação externa

#### Scenario: Compra com preço informado
- **WHEN** um cliente compra quantidade positiva de uma ação existente com preço unitário válido
- **THEN** o sistema registra a compra e aumenta a posição sem consultar cotação externa

#### Scenario: Compra cria ação e posição
- **WHEN** um cliente compra quantidade positiva para uma carteira existente informando ticker e mercado ainda não cadastrados
- **THEN** o sistema importa a ação, cria sua posição naquela carteira e registra a compra na mesma transação

#### Scenario: Compra reutiliza ação por ticker
- **WHEN** um cliente compra informando ticker e mercado de uma ação já cadastrada
- **THEN** o sistema reutiliza a ação existente e altera somente a posição da carteira informada

#### Scenario: Compra persistida pela API
- **WHEN** um cliente envia uma compra válida para `POST /operacoes/compras`
- **THEN** a API responde HTTP 201 com o identificador da operação, e a operação e a posição correspondente podem ser consultadas após a resposta

#### Scenario: Identificação de ação inválida
- **WHEN** uma compra informa `acaoId` junto com ticker/mercado, omite todos esses campos ou informa apenas um entre ticker e mercado
- **THEN** o sistema responde com erro de validação e não persiste ação, posição ou operação

#### Scenario: Compra sem preço e cotação indisponível
- **WHEN** uma compra ocorre sem preço e a cotação não pode ser obtida
- **THEN** a API retorna erro padronizado de indisponibilidade e não registra operação nem altera a posição

### Requirement: Registro de venda
O sistema SHALL registrar uma venda somente para posição existente cuja quantidade seja suficiente e quando a quantidade for no mínimo `0.0001`. A venda SHALL requerer `carteiraId`, `acaoId` e quantidade; `precoUnitario`, corretagem, impostos e valor adicional serão campos opcionais. Quando `precoUnitario` for informado, ele MUST ser no mínimo `0.0001` e o sistema SHALL usá-lo para registrar a venda sem consultar cotação externa. Quando estiver ausente, o sistema SHALL obter uma cotação externa válida. Uma venda bem-sucedida SHALL persistir a operação, reduzir a posição sem removê-la — inclusive quando resultar em quantidade zero — e responder HTTP 201 com o identificador da operação. Quando não puder ser persistida, a API SHALL retornar um erro padronizado que identifique a categoria da falha e MUST NOT retornar sucesso.

#### Scenario: Venda com preço informado
- **WHEN** um cliente vende uma quantidade disponível e informa `precoUnitario` válido
- **THEN** o sistema registra a venda com o preço informado sem consultar cotação externa

#### Scenario: Venda sem preço informado
- **WHEN** um cliente vende uma quantidade disponível sem informar `precoUnitario`
- **THEN** o sistema obtém cotação externa válida e a usa para registrar a venda

#### Scenario: Venda persistida pela API
- **WHEN** um cliente envia uma venda válida para `POST /operacoes/vendas`
- **THEN** a API responde HTTP 201 com o identificador da operação, e a operação e a posição atualizada podem ser consultadas após a resposta

#### Scenario: Venda total
- **WHEN** um cliente vende exatamente toda a quantidade de uma posição
- **THEN** o sistema registra a venda e mantém a posição persistida com quantidade zero

#### Scenario: Venda acima da posição
- **WHEN** um cliente solicita venda com quantidade maior que a posição atual
- **THEN** o sistema responde 422 e não cria operação nem altera a posição

#### Scenario: Preço manual de venda inválido
- **WHEN** um cliente informa `precoUnitario` menor que `0.0001` em uma venda
- **THEN** o sistema responde com erro de validação e não cria operação nem altera a posição
