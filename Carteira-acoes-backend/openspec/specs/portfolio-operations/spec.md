# portfolio-operations Specification

## Purpose

Registrar compras e vendas como histórico imutável e manter a posição corrente de cada ação em uma carteira sempre consistente com esse histórico.
## Requirements
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

### Requirement: Cálculos e valores de operação
O sistema SHALL calcular valor bruto como quantidade vezes preço unitário. Corretagem, impostos e valor adicional ausentes MUST assumir zero e custos não podem ser negativos. Para compra, o custo total da operação SHALL ser o valor bruto mais todos os custos; para venda, o valor líquido recebido SHALL ser o valor bruto menos todos os custos. Esses valores SHALL ser usados nos cálculos financeiros da posição.

#### Scenario: Cálculo com custos opcionais ausentes
- **WHEN** uma compra ou venda é registrada sem corretagem, impostos e valor adicional
- **THEN** o sistema registra esses campos como zero e o valor líquido é igual ao valor bruto

#### Scenario: Custos reduzem resultado de venda
- **WHEN** uma venda possui corretagem, impostos ou valor adicional
- **THEN** o valor líquido recebido é menor que o valor bruto pela soma desses custos

### Requirement: Atomicidade e histórico imutável
O sistema SHALL aplicar cada compra ou venda atomicamente: operação e posição MUST ser persistidas juntas, ou nenhuma mudança poderá persistir. Operações registradas MUST NOT possuir endpoint de edição genérica; o histórico de uma carteira SHALL ser retornado paginado em ordem decrescente de data/hora. Cada item do histórico SHALL conter dados suficientes para exibição de relatório, incluindo identificador da ação, ticker, nome da empresa, mercado, moeda, tipo de operação, quantidade, preço unitário, valores bruto e líquido, custos e data/hora. Uma carteira com operações ou posições vinculadas MUST NOT ser excluída, e uma tentativa de exclusão MUST preservar integralmente esse histórico e essas posições.

#### Scenario: Falha durante venda
- **WHEN** uma venda falha depois de iniciar o processamento, inclusive por falha de cotação
- **THEN** a transação é revertida e a posição e o histórico permanecem inalterados

#### Scenario: Histórico ordenado
- **WHEN** um cliente consulta as operações de uma carteira
- **THEN** o sistema retorna resultados paginados da operação mais recente para a mais antiga

#### Scenario: Histórico paginado consumível pelo frontend
- **WHEN** um cliente consulta `GET /operacoes/carteiras/{carteiraId}`
- **THEN** a API retorna uma página cujo array de operações está em `content`

#### Scenario: Operação enriquecida para relatório
- **WHEN** um cliente consulta o histórico de operações de uma carteira
- **THEN** cada operação retornada inclui ticker, nome da empresa, mercado e moeda da ação sem exigir consulta adicional por `acaoId`

#### Scenario: Exclusão bloqueada preserva histórico
- **WHEN** um cliente tenta excluir carteira que possui operação ou posição
- **THEN** a API responde 409 e nenhum registro financeiro ou posição é removido

#### Scenario: Exclusão bloqueada preserva vínculos da ação
- **WHEN** um cliente tenta excluir ação que possui operação ou posição vinculada
- **THEN** a API responde 409 e nenhuma operação, posição ou histórico financeiro é removido

### Requirement: Concorrência de posição
O sistema SHALL impedir atualização perdida quando operações concorrentes alterarem a mesma posição e MUST preservar a regra de que a quantidade jamais seja negativa.

#### Scenario: Operações concorrentes incompatíveis
- **WHEN** duas operações concorrentes tentam gravar estados conflitantes para a mesma posição
- **THEN** o sistema rejeita ou repete de forma controlada a atualização conflitante sem persistir quantidade incorreta

### Requirement: Indicadores financeiros oficiais da posição
O sistema SHALL calcular os indicadores de uma posição exclusivamente a partir do histórico cronológico de operações da carteira e ação, sem persistir valores derivados como fonte de verdade. O preço médio de venda SHALL ser a soma de `quantidade × preço unitário` de todas as vendas dividida pela quantidade total vendida, sem incluir custos; quando não houver venda, esse indicador SHALL ser não aplicável. O custo médio de aquisição SHALL usar o custo total das compras e ser atualizado cronologicamente; uma venda reduzirá o custo acumulado pela quantidade vendida multiplicada pelo custo médio vigente. O lucro/prejuízo realizado SHALL somar, para cada venda, o valor líquido recebido menos seu custo base. O não realizado SHALL ser quantidade atual multiplicada pela cotação atual menos o custo restante; o total SHALL ser a soma dos resultados realizado e não realizado. Todos os resultados MUST preservar precisão decimal e a moeda da ação.

#### Scenario: Preço médio de venda ponderado
- **WHEN** uma posição possui vendas de 5 unidades a 30 e 10 unidades a 36
- **THEN** o preço médio de venda retornado é 34, calculado sem corretagem, impostos ou custo adicional

#### Scenario: Preço médio de venda não aplicável
- **WHEN** uma posição não possui nenhuma venda no histórico
- **THEN** o preço médio de venda oficial é não aplicável e não é convertido para zero

#### Scenario: Resultado realizado com custo médio de aquisição
- **WHEN** compras de 10 unidades a 30 e 10 unidades a 40 precedem uma venda de 5 unidades a 45 sem custos
- **THEN** o resultado realizado dessa venda é 50, usando custo médio de aquisição de 35 por unidade

#### Scenario: Preço médio de aquisição no DTO
- **WHEN** compras de 10 unidades a 30 e 10 unidades a 40 precedem uma venda parcial
- **THEN** o indicador `precoMedio` retornado é 35

#### Scenario: Venda com custos
- **WHEN** uma venda possui corretagem, impostos ou outros custos registrados
- **THEN** o resultado realizado usa o valor líquido recebido e o preço médio de venda permanece calculado sem esses custos

#### Scenario: Resultado não realizado e total
- **WHEN** uma posição restante possui custo acumulado de 525 e cotação atual de 45 para 15 unidades, além de resultado realizado de 50
- **THEN** o resultado não realizado é 150 e o resultado total é 200

#### Scenario: Histórico cronológico reproduzível
- **WHEN** as mesmas operações são consultadas em ordem diferente da data de execução
- **THEN** os indicadores retornados são idênticos aos obtidos processando o histórico em ordem cronológica

### Requirement: Operações financeiras atômicas e determinísticas
Compra e Venda SHALL validar carteira, ação, saldo, quantidade e preço dentro de uma transação; falha em qualquer etapa SHALL reverter operação e posição. Cálculos SHALL usar precisão decimal adequada e respeitar os indicadores financeiros oficiais existentes.

#### Scenario: Venda acima do saldo
- **WHEN** uma venda excede a quantidade disponível
- **THEN** a API rejeita a operação e a quantidade, posição e histórico permanecem inalterados

#### Scenario: Compras sucessivas
- **WHEN** são registradas compras de 10 unidades a 30 e 10 a 40
- **THEN** a posição resulta em 20 unidades e preço médio 35 quando a regra vigente for média ponderada

### Requirement: Totais financeiros de posição
A resposta de cada posição SHALL incluir `valorInvestidoAtual`, `valorTotalComprado` e `valorTotalVendido`, derivados do histórico e expressos na moeda da ação. O valor investido atual é a quantidade atual multiplicada pelo preço médio vigente; os totais comprado e vendido são somas históricas dos valores brutos de compras e vendas, respectivamente. Na ausência de operações de um tipo, o total correspondente SHALL ser zero, sem confundir valor vendido com lucro realizado.

#### Scenario: Totais após compras e venda parcial
- **WHEN** uma carteira registra compras de 10 ações a 30 e 10 a 40, vende 5 a 45 e a cotação atual é 45
- **THEN** a posição informa quantidade 15, preço médio 35, valor investido 525, total comprado 700, total vendido 225, valor atual 675 e resultados realizado 50, não realizado 150 e total 200

#### Scenario: Somente compra
- **WHEN** uma posição possui uma compra de 10 ações a 30 e nenhuma venda
- **THEN** o valor investido e total comprado são 300, total vendido é zero e preço médio de venda permanece não aplicável

### Requirement: Totais financeiros do resumo de carteira
Quando houver resumo de carteira, os totais de posições SHALL ser agregados por moeda, incluindo valor investido atual, valor atual, comprado, vendido e resultados realizado, não realizado e total. Valores de moedas distintas MUST NOT ser somados diretamente; conversões devem usar mecanismo autoritativo existente.

#### Scenario: Resumo com moedas distintas
- **WHEN** uma carteira possui posições em BRL e USD
- **THEN** o resumo mantém os totais separados por moeda, salvo conversão autoritativa aplicável
