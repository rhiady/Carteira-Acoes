## MODIFIED Requirements

### Requirement: Posições ativas da carteira
O sistema SHALL listar as ações de uma carteira apenas quando a quantidade da posição for maior que zero. `GET /carteiras/{id}/acoes` SHALL retornar uma página de posições enriquecidas, com paginação padrão `page=0`, `size=20` e `sort=id,desc`. O endpoint SHALL aceitar ordenação pública somente por `ticker`, `quantidade` e `id`, em direção ascendente ou descendente, sem exigir que clientes conheçam nomes internos de persistência. Campos de ordenação não permitidos MUST ser rejeitados com erro 400 padronizado. Cada posição SHALL conter `carteiraId`, `acaoId`, ticker, nome da empresa, mercado, moeda, quantidade, cotação atual, data/hora da cotação, valor atual, preço médio de venda, lucro/prejuízo realizado, não realizado e total. Para posições de ações do mercado EUA cuja taxa USD/BRL esteja disponível, a resposta SHALL incluir `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl`, preservando todos os valores financeiros originais em USD. Quantidades e valores monetários MUST ser representados com precisão decimal, sem valores de ponto flutuante; campos monetários originais de cada posição SHALL estar na moeda da própria ação.

#### Scenario: Posição zerada não é exibida
- **WHEN** uma carteira contém uma posição com quantidade igual a zero
- **THEN** a consulta de posições ativas não inclui essa posição

#### Scenario: Posição enriquecida
- **WHEN** um cliente consulta posições ativas de uma carteira
- **THEN** cada item retornado inclui os indicadores financeiros oficiais calculados pelo backend e a moeda da ação

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

#### Scenario: Posição americana com equivalente em BRL
- **WHEN** um cliente consulta uma posição ativa de ação do mercado EUA e a taxa USD/BRL está disponível
- **THEN** a posição retorna a cotação e o valor atual originais em USD e os equivalentes `cotacaoEmBrl` e `valorAtualEmBrl`
