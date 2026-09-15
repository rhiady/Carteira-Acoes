## MODIFIED Requirements

### Requirement: Posições ativas da carteira
O sistema SHALL listar as ações de uma carteira apenas quando a quantidade da posição for maior que zero. `GET /carteiras/{id}/acoes` SHALL retornar uma página de posições enriquecidas, com paginação padrão `page=0`, `size=20` e `sort=id,desc`. O endpoint SHALL aceitar ordenação pública somente por `ticker`, `quantidade` e `id`, em direção ascendente ou descendente, sem exigir que clientes conheçam nomes internos de persistência. Campos de ordenação não permitidos MUST ser rejeitados com erro 400 padronizado. Cada posição SHALL conter `carteiraId`, `acaoId`, ticker, nome da empresa, mercado, moeda, quantidade, cotação atual, data/hora da cotação, valor atual, preço médio de venda quando aplicável, lucro/prejuízo realizado, não realizado e total, todos calculados pelas operações históricas com precisão decimal. Quando nenhuma venda tiver sido registrada para a posição, `precoMedioVenda` SHALL ser `null`, não zero. Para posições de ações do mercado EUA cuja taxa USD/BRL esteja disponível, a resposta SHALL incluir `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl`, preservando todos os valores financeiros originais em USD. Quantidades e valores monetários MUST ser representados com precisão decimal; campos monetários originais de cada posição SHALL estar na moeda da própria ação.

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

#### Scenario: Ordenação por ticker ascendente
- **WHEN** um cliente consulta `GET /carteiras/{id}/acoes?sort=ticker,asc`
- **THEN** o sistema retorna HTTP 200 com as posições ordenadas por ticker em ordem ascendente

#### Scenario: Ordenação por ticker descendente
- **WHEN** um cliente consulta `GET /carteiras/{id}/acoes?sort=ticker,desc`
- **THEN** o sistema retorna HTTP 200 com as posições ordenadas por ticker em ordem descendente

#### Scenario: Paginação com ordenação por ticker
- **WHEN** um cliente consulta posições ativas com `page`, `size` e `sort=ticker,asc`
- **THEN** o sistema aplica a ordenação por ticker antes de retornar a página solicitada

#### Scenario: Ordenação inválida
- **WHEN** um cliente consulta posições ativas com campo de ordenação diferente de `ticker`, `quantidade` ou `id`
- **THEN** o sistema responde HTTP 400 com erro padronizado e não retorna erro 500

#### Scenario: Consulta sem ordenação explícita
- **WHEN** um cliente consulta `GET /carteiras/{id}/acoes` sem parâmetro `sort`
- **THEN** o sistema retorna HTTP 200 usando a ordenação padrão do endpoint

#### Scenario: Indicadores coerentes com o histórico
- **WHEN** uma posição possui compras e vendas parciais
- **THEN** os campos de preço médio e lucro/prejuízo da resposta correspondem ao processamento cronológico de todas as operações

#### Scenario: DTO contém preço médio de aquisição
- **WHEN** um cliente consulta uma posição com compras registradas
- **THEN** cada item retorna `precoMedio` com o custo médio de aquisição calculado pelas operações

### Requirement: Resumo financeiro segmentado da carteira
O sistema SHALL expor `GET /carteiras/{id}/resumo` com o identificador e nome da carteira, a quantidade de ativos com posição positiva e uma coleção de subtotais por moeda. Cada subtotal SHALL informar a moeda, `valorAtual` e `lucroPrejuizo`; o sistema MUST NOT somar valores em BRL e USD nem retornar um total financeiro consolidado sem conversão cambial. O `lucroPrejuizo` de cada subtotal SHALL ser a soma dos resultados totais das posições daquela moeda, com precisão decimal.

#### Scenario: Carteira com uma moeda
- **WHEN** uma carteira possui posições ativas somente em BRL
- **THEN** o resumo retorna um único subtotal BRL com o valor atual e o lucro/prejuízo calculados

#### Scenario: Carteira multimoeda
- **WHEN** uma carteira possui posições ativas em BRL e USD
- **THEN** o resumo retorna subtotais distintos para BRL e USD sem somar seus valores

#### Scenario: Carteira inexistente
- **WHEN** um cliente consulta o resumo de uma carteira inexistente
- **THEN** o sistema retorna erro padronizado de carteira não encontrada

#### Scenario: Resumo consistente com posições
- **WHEN** uma carteira possui posições com lucro/prejuízo realizado e não realizado
- **THEN** o subtotal de cada moeda soma o lucro/prejuízo total das posições correspondentes sem conversão implícita
