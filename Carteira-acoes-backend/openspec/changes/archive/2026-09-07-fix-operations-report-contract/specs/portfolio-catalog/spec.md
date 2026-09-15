## MODIFIED Requirements

### Requirement: Posições ativas da carteira
O sistema SHALL listar as ações de uma carteira apenas quando a quantidade da posição for maior que zero. `GET /carteiras/{id}/acoes` SHALL retornar uma página de posições enriquecidas, com paginação padrão `page=0`, `size=20` e `sort=id,desc`. Cada posição SHALL conter `carteiraId`, `acaoId`, ticker, nome da empresa, mercado, moeda, quantidade, cotação atual, data/hora da cotação, valor atual, preço médio de venda quando aplicável, lucro/prejuízo realizado, não realizado e total. Quando nenhuma venda tiver sido registrada para a posição, `precoMedioVenda` SHALL ser `null`, não zero. Para posições de ações do mercado EUA cuja taxa USD/BRL esteja disponível, a resposta SHALL incluir `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl`, preservando todos os valores financeiros originais em USD. Quantidades e valores monetários MUST ser representados com precisão decimal, sem valores de ponto flutuante; campos monetários originais de cada posição SHALL estar na moeda da própria ação.

#### Scenario: Posição zerada não é exibida
- **WHEN** uma carteira contém uma posição com quantidade igual a zero
- **THEN** a consulta de posições ativas não inclui essa posição

#### Scenario: Posição enriquecida
- **WHEN** um cliente consulta posições ativas de uma carteira
- **THEN** cada item retornado inclui os indicadores financeiros oficiais calculados pelo backend e a moeda da ação

#### Scenario: Preço médio de venda sem venda
- **WHEN** um cliente consulta uma posição ativa que nunca teve venda registrada
- **THEN** a posição retorna `precoMedioVenda` como `null`

#### Scenario: Posição americana com equivalente em BRL
- **WHEN** um cliente consulta uma posição ativa de ação do mercado EUA e a taxa USD/BRL está disponível
- **THEN** a posição retorna a cotação e o valor atual originais em USD e os equivalentes `cotacaoEmBrl` e `valorAtualEmBrl`

### Requirement: Indicadores financeiros oficiais da posição
O sistema SHALL calcular os indicadores de uma posição exclusivamente a partir do histórico cronológico de operações da carteira e ação, sem persistir valores derivados como fonte de verdade. O preço médio de venda SHALL ser a soma de `quantidade × preço unitário` de todas as vendas dividida pela quantidade total vendida, sem incluir custos; quando não houver venda, esse indicador SHALL ser não aplicável. O custo médio de aquisição SHALL usar o custo total das compras e ser atualizado cronologicamente; uma venda reduzirá o custo acumulado pela quantidade vendida multiplicada pelo custo médio vigente. O lucro/prejuízo realizado SHALL somar, para cada venda, o valor líquido recebido menos seu custo base. O não realizado SHALL ser quantidade atual multiplicada pela cotação atual menos o custo restante; o total SHALL ser a soma dos resultados realizado e não realizado.

#### Scenario: Preço médio de venda ponderado
- **WHEN** uma posição possui vendas de 5 unidades a 30 e 10 unidades a 36
- **THEN** o preço médio de venda retornado é 34, calculado sem corretagem, impostos ou custo adicional

#### Scenario: Preço médio de venda não aplicável
- **WHEN** uma posição não possui nenhuma venda no histórico
- **THEN** o preço médio de venda oficial é não aplicável

#### Scenario: Resultado realizado com custo médio de aquisição
- **WHEN** compras de 10 unidades a 30 e 10 unidades a 40 precedem uma venda de 5 unidades a 45 sem custos
- **THEN** o resultado realizado dessa venda é 50, usando custo médio de aquisição de 35 por unidade

#### Scenario: Resultado não realizado e total
- **WHEN** uma posição restante possui custo acumulado de 525 e cotação atual de 45 para 15 unidades, além de resultado realizado de 50
- **THEN** o resultado não realizado é 150 e o resultado total é 200
