## MODIFIED Requirements

### Requirement: Posições ativas da carteira
O sistema SHALL listar as ações de uma carteira apenas quando a quantidade da posição for maior que zero. `GET /carteiras/{id}/acoes` SHALL retornar uma página de posições enriquecidas, com paginação padrão `page=0`, `size=20` e `sort=id,desc`. Cada posição SHALL conter `carteiraId`, `acaoId`, ticker, nome da empresa, mercado, moeda, quantidade, cotação atual, data/hora da cotação, valor atual, preço médio de venda, lucro/prejuízo realizado, não realizado e total. Quantidades e valores monetários MUST ser representados com precisão decimal, sem valores de ponto flutuante; campos monetários de cada posição SHALL estar na moeda da própria ação.

#### Scenario: Posição zerada não é exibida
- **WHEN** uma carteira contém uma posição com quantidade igual a zero
- **THEN** a consulta de posições ativas não inclui essa posição

#### Scenario: Posição enriquecida
- **WHEN** um cliente consulta posições ativas de uma carteira
- **THEN** cada item retornado inclui os indicadores financeiros oficiais calculados pelo backend e a moeda da ação

## ADDED Requirements

### Requirement: Resumo financeiro segmentado da carteira
O sistema SHALL expor `GET /carteiras/{id}/resumo` com o identificador e nome da carteira, a quantidade de ativos com posição positiva e uma coleção de subtotais por moeda. Cada subtotal SHALL informar a moeda, `valorAtual` e `lucroPrejuizo`; o sistema MUST NOT somar valores em BRL e USD nem retornar um total financeiro consolidado sem conversão cambial.

#### Scenario: Carteira com uma moeda
- **WHEN** uma carteira possui posições ativas somente em BRL
- **THEN** o resumo retorna um único subtotal BRL com o valor atual e o lucro/prejuízo calculados

#### Scenario: Carteira multimoeda
- **WHEN** uma carteira possui posições ativas em BRL e USD
- **THEN** o resumo retorna subtotais distintos para BRL e USD sem somar seus valores

#### Scenario: Carteira inexistente
- **WHEN** um cliente consulta o resumo de uma carteira inexistente
- **THEN** o sistema retorna erro padronizado de carteira não encontrada
