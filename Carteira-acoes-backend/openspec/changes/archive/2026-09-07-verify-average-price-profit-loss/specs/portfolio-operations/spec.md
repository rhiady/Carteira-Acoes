## MODIFIED Requirements

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
