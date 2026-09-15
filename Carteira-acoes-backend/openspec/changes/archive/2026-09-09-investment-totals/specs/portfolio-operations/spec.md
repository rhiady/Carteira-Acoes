## ADDED Requirements

### Requirement: Totais financeiros de posição
A resposta de cada posição SHALL incluir, além dos campos existentes, `valorInvestidoAtual`, `valorTotalComprado` e `valorTotalVendido`, todos como valores decimais na moeda da ação. `valorInvestidoAtual` SHALL representar a quantidade atual multiplicada pelo preço médio de aquisição vigente. `valorTotalComprado` SHALL ser a soma histórica do valor bruto de cada compra e `valorTotalVendido` SHALL ser a soma histórica do valor bruto de cada venda; vendas não podem reduzir o total comprado e valor vendido não pode ser usado como lucro realizado. Na ausência de operações de um tipo, o respectivo total SHALL ser zero.

#### Scenario: Totais após compras e venda parcial
- **WHEN** uma carteira registra compras de 10 ações a 30 e 10 a 40, vende 5 a 45 e a cotação atual é 45
- **THEN** a posição informa quantidade 15, preço médio 35, `valorInvestidoAtual` 525, `valorTotalComprado` 700, `valorTotalVendido` 225, valor atual 675, resultado realizado 50, não realizado 150 e total 200

#### Scenario: Somente compra
- **WHEN** uma posição possui uma compra de 10 ações a 30 e nenhuma venda
- **THEN** `valorInvestidoAtual` e `valorTotalComprado` são 300, `valorTotalVendido` é zero e o contrato vigente de preço médio de venda permanece não aplicável

#### Scenario: Múltiplas compras sem venda
- **WHEN** uma posição possui compras de 10 ações a 30 e 10 a 40
- **THEN** a quantidade é 20, o preço médio é 35, `valorInvestidoAtual` é 700, `valorTotalComprado` é 700 e `valorTotalVendido` é zero

### Requirement: Totais financeiros do resumo de carteira
Quando existir resumo de carteira, o sistema SHALL disponibilizar os totais de posições agregados por moeda: valor investido atual, valor atual, total comprado, total vendido e resultados realizado, não realizado e total. O sistema MUST NOT somar diretamente valores de moedas distintas; conversões SHALL reutilizar exclusivamente a conversão autoritativa já existente.

#### Scenario: Resumo com moedas distintas
- **WHEN** uma carteira possui posições em BRL e USD
- **THEN** o resumo mantém os totais separados por moeda, salvo quando a conversão autoritativa existente for explicitamente aplicável
