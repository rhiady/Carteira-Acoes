## MODIFIED Requirements

### Requirement: Posições ativas da carteira
O sistema SHALL listar as ações de uma carteira apenas quando a quantidade da posição for maior que zero. `GET /carteiras/{id}/acoes` SHALL retornar uma página de posições enriquecidas, com paginação padrão `page=0`, `size=20` e `sort=id,desc`. Cada posição SHALL conter `carteiraId`, `acaoId`, ticker, nome da empresa, mercado, moeda, quantidade, cotação atual, data/hora da cotação, valor atual, preço médio de venda, lucro/prejuízo realizado, não realizado e total. Para posições de ações do mercado EUA cuja taxa USD/BRL esteja disponível, a resposta SHALL incluir `cotacaoEmBrl`, `valorAtualEmBrl`, `taxaUsdBrl` e `dataHoraTaxaUsdBrl`, preservando todos os valores financeiros originais em USD. Quantidades e valores monetários MUST ser representados com precisão decimal, sem valores de ponto flutuante; campos monetários originais de cada posição SHALL estar na moeda da própria ação.

#### Scenario: Posição zerada não é exibida
- **WHEN** uma carteira contém uma posição com quantidade igual a zero
- **THEN** a consulta de posições ativas não inclui essa posição

#### Scenario: Posição enriquecida
- **WHEN** um cliente consulta posições ativas de uma carteira
- **THEN** cada item retornado inclui os indicadores financeiros oficiais calculados pelo backend e a moeda da ação

#### Scenario: Posição americana com equivalente em BRL
- **WHEN** um cliente consulta uma posição ativa de ação do mercado EUA e a taxa USD/BRL está disponível
- **THEN** a posição retorna a cotação e o valor atual originais em USD e os equivalentes `cotacaoEmBrl` e `valorAtualEmBrl`
