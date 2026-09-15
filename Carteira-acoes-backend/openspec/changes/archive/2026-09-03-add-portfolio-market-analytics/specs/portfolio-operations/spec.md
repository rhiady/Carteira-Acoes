## MODIFIED Requirements

### Requirement: Registro de compra
O sistema SHALL registrar uma compra somente para carteira existente quando a quantidade for no mínimo `0.0001`. A compra SHALL identificar uma ação existente por `acaoId` OU informar conjuntamente `ticker` e `mercado`; MUST rejeitar identificação ausente, combinação parcial de ticker/mercado ou as duas formas de identificação na mesma requisição. Quando `acaoId` for informado, a ação deverá existir. Quando ticker e mercado forem informados, o sistema SHALL reutilizar a ação correspondente se ela existir ou criá-la a partir do provedor de mercado, dentro da mesma transação da compra. Se o preço unitário for informado, SHALL usá-lo; se estiver ausente, SHALL obter cotação válida externamente. Corretagem, impostos e valor adicional serão custos opcionais. A compra MUST criar ou aumentar somente a posição da ação na carteira informada e registrar os valores bruto e líquido calculados.

#### Scenario: Compra de ação existente por identificador
- **WHEN** um cliente compra quantidade válida para uma carteira existente informando `acaoId` válido e preço unitário válido
- **THEN** o sistema registra a compra e aumenta somente a posição daquela carteira sem consultar cotação externa

#### Scenario: Compra com preço informado
- **WHEN** um cliente compra quantidade válida de uma ação existente com preço unitário válido
- **THEN** o sistema registra a compra e aumenta a posição sem consultar cotação externa

#### Scenario: Compra cria ação e posição
- **WHEN** um cliente compra quantidade válida para uma carteira existente informando ticker e mercado ainda não cadastrados
- **THEN** o sistema importa a ação, cria sua posição naquela carteira e registra a compra na mesma transação

#### Scenario: Compra reutiliza ação por ticker
- **WHEN** um cliente compra informando ticker e mercado de uma ação já cadastrada
- **THEN** o sistema reutiliza a ação existente e altera somente a posição da carteira informada

#### Scenario: Identificação de ação inválida
- **WHEN** uma compra informa `acaoId` junto com ticker/mercado, omite todos esses campos ou informa apenas um entre ticker e mercado
- **THEN** o sistema responde com erro de validação e não persiste ação, posição ou operação

#### Scenario: Compra sem preço e cotação indisponível
- **WHEN** um cliente compra sem informar preço e a cotação não pode ser obtida
- **THEN** o sistema não registra operação nem altera a posição

### Requirement: Registro de venda
O sistema SHALL registrar uma venda somente para posição existente cuja quantidade seja suficiente e quando a quantidade for no mínimo `0.0001`. A venda SHALL requerer `carteiraId`, `acaoId` e quantidade; corretagem, impostos e valor adicional serão custos opcionais. O preço unitário da venda MUST ser obtido por cotação externa; o cliente MUST NOT defini-lo. A venda SHALL reduzir a posição sem removê-la, inclusive quando resultar em quantidade zero.

#### Scenario: Venda total
- **WHEN** um cliente vende exatamente toda a quantidade de uma posição
- **THEN** o sistema registra a venda e mantém a posição persistida com quantidade zero

#### Scenario: Venda acima da posição
- **WHEN** um cliente solicita venda com quantidade maior que a posição atual
- **THEN** o sistema responde 422 e não cria operação nem altera a posição

### Requirement: Cálculos e valores de operação
O sistema SHALL calcular valor bruto como quantidade vezes preço unitário. Corretagem, impostos e valor adicional ausentes MUST assumir zero e custos não podem ser negativos. Para compra, o custo total da operação SHALL ser o valor bruto mais todos os custos; para venda, o valor líquido recebido SHALL ser o valor bruto menos todos os custos. Esses valores SHALL ser usados nos cálculos financeiros da posição.

#### Scenario: Cálculo com custos opcionais ausentes
- **WHEN** uma compra ou venda é registrada sem corretagem, impostos e valor adicional
- **THEN** o sistema registra esses campos como zero e o valor líquido é igual ao valor bruto

#### Scenario: Custos reduzem resultado de venda
- **WHEN** uma venda possui corretagem, impostos ou valor adicional
- **THEN** o valor líquido recebido é menor que o valor bruto pela soma desses custos

## ADDED Requirements

### Requirement: Indicadores financeiros oficiais da posição
O sistema SHALL calcular os indicadores de uma posição exclusivamente a partir do histórico cronológico de operações da carteira e ação, sem persistir valores derivados como fonte de verdade. O preço médio de venda SHALL ser a soma de `quantidade × preço unitário` de todas as vendas dividida pela quantidade total vendida, sem incluir custos. O custo médio de aquisição SHALL usar o custo total das compras e ser atualizado cronologicamente; uma venda reduzirá o custo acumulado pela quantidade vendida multiplicada pelo custo médio vigente. O lucro/prejuízo realizado SHALL somar, para cada venda, o valor líquido recebido menos seu custo base. O não realizado SHALL ser quantidade atual multiplicada pela cotação atual menos o custo restante; o total SHALL ser a soma dos resultados realizado e não realizado.

#### Scenario: Preço médio de venda ponderado
- **WHEN** uma posição possui vendas de 5 unidades a 30 e 10 unidades a 36
- **THEN** o preço médio de venda retornado é 34, calculado sem corretagem, impostos ou custo adicional

#### Scenario: Resultado realizado com custo médio de aquisição
- **WHEN** compras de 10 unidades a 30 e 10 unidades a 40 precedem uma venda de 5 unidades a 45 sem custos
- **THEN** o resultado realizado dessa venda é 50, usando custo médio de aquisição de 35 por unidade

#### Scenario: Resultado não realizado e total
- **WHEN** uma posição restante possui custo acumulado de 525 e cotação atual de 45 para 15 unidades, além de resultado realizado de 50
- **THEN** o resultado não realizado é 150 e o resultado total é 200
