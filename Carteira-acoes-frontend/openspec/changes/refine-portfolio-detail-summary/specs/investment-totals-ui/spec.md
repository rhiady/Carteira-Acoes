## ADDED Requirements

### Requirement: Resumo de carteira apresenta total investido por moeda
O detalhe da carteira SHALL apresentar `Total investido` por moeda quando o conjunto completo de posições disponíveis contiver `valorInvestidoAtual` suficiente para cada posição. O valor SHALL manter a moeda de origem, usar o formatter correspondente e permanecer separado de valor atual e resultado.

#### Scenario: Carteira com posições completas
- **WHEN** todas as posições da carteira foram carregadas e possuem `valorInvestidoAtual` autoritativo
- **THEN** o resumo apresenta o total investido de cada moeda sem combinar BRL e USD

#### Scenario: Dados insuficientes para total investido
- **WHEN** as posições estão paginadas, incompletas ou não fornecem `valorInvestidoAtual` suficiente
- **THEN** o resumo não inventa nem soma um total parcial e apresenta o campo como indisponível ou o omite com explicação acessível

### Requirement: Totais financeiros preservam significado e disponibilidade
O resumo e as posições SHALL distinguir total investido, valor atual, total comprado, total vendido e resultados realizado/não realizado/total. Campos ausentes SHALL permanecer indisponíveis, e resultados SHALL manter sinal e moeda sem depender apenas de cor.

#### Scenario: Carteira multimoeda
- **WHEN** existem posições em BRL e USD
- **THEN** a interface apresenta subtotais independentes por moeda e não exibe patrimônio consolidado
