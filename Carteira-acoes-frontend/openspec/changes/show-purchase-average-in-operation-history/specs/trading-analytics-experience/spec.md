## MODIFIED Requirements

### Requirement: Histórico e rankings respeitam suficiência dos dados
Histórico SHALL usar somente valores retornados pelo backend. Para cada registro de compra, SHALL exibir o `precoMedio` da posição correspondente como `Preço médio de compra` quando esse dado estiver disponível; registros de venda MUST NOT exibir esse campo. Quando a posição ou seu `precoMedio` não estiverem disponíveis, o registro de compra SHALL exibir `—`. A interface MUST NOT recalcular a média a partir do histórico paginado nem usar `precoMedioVenda` como média de compra. O histórico de cotação MAY exibir uma linha `Preço médio` quando houver posição correspondente. Rankings, pesos e agregações globais SHALL ser produzidos apenas com conjunto completo de posições; caso contrário, a interface SHALL indicar indisponibilidade.

#### Scenario: Compra com preço médio autoritativo
- **WHEN** uma pessoa consulta uma compra cujo ativo possui `precoMedio` na posição da carteira
- **THEN** o registro apresenta `Preço médio de compra` com o valor e a moeda retornados pela posição

#### Scenario: Compra sem preço médio disponível
- **WHEN** uma pessoa consulta uma compra cujo ativo não possui posição atual ou `precoMedio`
- **THEN** o registro apresenta `Preço médio de compra: —` sem calcular ou inferir outro valor

#### Scenario: Venda não repete a média de compra
- **WHEN** uma pessoa consulta um registro de venda
- **THEN** o registro não apresenta `Preço médio de compra` nem usa `precoMedioVenda` como substituto

#### Scenario: Histórico real com preço médio
- **WHEN** existem pontos históricos e `precoMedio` da posição analisada
- **THEN** o gráfico exibe os pontos reais e uma referência horizontal identificada como `Preço médio`

#### Scenario: Página parcial
- **WHEN** a resposta paginada não representa todas as posições
- **THEN** a interface não apresenta ranking ou composição global como completa
