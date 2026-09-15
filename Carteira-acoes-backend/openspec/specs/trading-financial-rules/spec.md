# trading-financial-rules Specification

## Purpose

Define regras matemáticas verificáveis para operações, posições e indicadores financeiros, preservando precisão decimal, moeda, atomicidade e consistência histórica.

## Requirements

### Requirement: Precisão e unidades financeiras
Todo preço, cotação, quantidade, custo, câmbio, patrimônio, média e resultado financeiro MUST usar representação decimal exata, com escala, arredondamento, unidade e moeda explicitamente definidos. Valores BRL e USD MUST NOT ser somados sem conversão autoritativa identificável.

#### Scenario: Cálculo decimal e multimoeda
- **WHEN** indicadores são calculados para posições BRL e USD
- **THEN** nenhum cálculo crítico usa `float` ou `double`, cada valor preserva sua moeda e agregações não misturam moedas sem taxa válida

### Requirement: Custo médio cronológico
O custo médio de aquisição MUST ser calculado cronologicamente e de forma ponderada pelas compras abertas. Venda parcial MUST preservar o custo médio das unidades remanescentes; após posição zerada, uma nova compra MUST iniciar o custo médio da nova posição sem apagar o resultado realizado histórico.

#### Scenario: Compras sucessivas
- **WHEN** compras de 10 unidades a 30, 10 a 40 e 5 a 50 são registradas
- **THEN** os custos médios após cada etapa são respectivamente 30, 35 e 38

#### Scenario: Venda parcial seguida de compra
- **WHEN** uma posição de 20 unidades a custo médio 35 vende 5 a 45 e compra 5 a 50
- **THEN** restam 20 unidades com custo médio 38,75 e o resultado realizado anterior permanece acumulado

#### Scenario: Reabertura de posição
- **WHEN** uma posição comprada a 30 é totalmente vendida e depois recebe nova compra a 50
- **THEN** a nova posição aberta possui custo médio 50 e o histórico realizado da posição anterior é preservado

### Requirement: Venda, realizado e preço médio de venda
Venda MUST rejeitar quantidade superior à posição sem persistência parcial. O resultado realizado MUST acumular cada venda usando o valor líquido recebido menos o custo base vigente. `precoMedioVenda` MUST ser média ponderada por quantidade e MUST ser `null` quando nunca houve venda.

#### Scenario: Venda parcial com lucro
- **WHEN** 5 unidades são vendidas a 45 a partir de custo médio 35, sem custos
- **THEN** o resultado realizado aumenta em 50, a quantidade diminui em 5 e o custo médio remanescente continua 35

#### Scenario: Múltiplas vendas
- **WHEN** vendas de 5 unidades a 45 e 5 unidades a 30 partem de custo médio 35
- **THEN** o resultado realizado acumulado é 25 e não apenas o resultado da última venda

#### Scenario: Preço médio de venda não aplicável
- **WHEN** nenhuma venda existe no histórico da posição
- **THEN** `precoMedioVenda` é `null`, semanticamente distinto de zero

#### Scenario: Oversell
- **WHEN** uma posição de 15 unidades recebe tentativa de venda de 16
- **THEN** a operação é rejeitada e posição, quantidade e histórico permanecem inalterados

### Requirement: Valor atual e resultados da posição
Quando houver cotação válida, `valorAtual` MUST ser quantidade aberta vezes cotação, o não realizado MUST refletir cotação menos custo médio sobre a quantidade aberta, e o total MUST ser realizado acumulado mais não realizado. Ausência de cotação MUST NOT ser convertida arbitrariamente em zero.

#### Scenario: Resultado positivo completo
- **WHEN** restam 15 unidades a custo médio 35, cotação 45 e resultado realizado 50
- **THEN** valor atual é 675, não realizado é 150 e resultado total é 200

#### Scenario: Cotação ausente
- **WHEN** uma posição não possui cotação válida
- **THEN** os campos dependentes de cotação seguem nullability explícita e não apresentam valor financeiro fictício

### Requirement: Atomicidade e concorrência de posição
Compra e Venda MUST atualizar operação e posição em uma transação única. Operações concorrentes MUST NOT causar atualização perdida ou quantidade negativa; conflito MUST ser rejeitado ou repetido de forma controlada pela estratégia de concorrência existente.

#### Scenario: Falha durante operação
- **WHEN** qualquer validação, consulta externa ou persistência falha durante Compra ou Venda
- **THEN** nenhuma operação, posição ou quantidade parcial permanece persistida

#### Scenario: Vendas concorrentes incompatíveis
- **WHEN** duas vendas concorrentes de 8 tentam consumir uma posição de 10
- **THEN** o estado final nunca possui quantidade negativa nem duas vendas incompatíveis confirmadas
