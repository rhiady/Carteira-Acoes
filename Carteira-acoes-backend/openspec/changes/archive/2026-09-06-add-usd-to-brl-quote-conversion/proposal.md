## Why

As cotações de ações americanas são retornadas somente em USD, dificultando a leitura por usuários que acompanham a carteira em reais. O sistema precisa fornecer o equivalente em BRL sem perder a cotação original nem a moeda do ativo.

## What Changes

- Consultar a taxa USD/BRL de referência pela API pública PTAX do Banco Central do Brasil.
- Expor, para ações do mercado EUA, a cotação original em USD e um novo valor equivalente em BRL, além da taxa e do instante de câmbio usados.
- Incluir o valor atual da posição americana em BRL, mantendo os valores financeiros originais em USD.
- Não converter ações brasileiras nem substituir campos monetários existentes.

## Capabilities

### New Capabilities

- `currency-conversion`: obtenção resiliente da taxa USD/BRL de referência e cálculo decimal de conversão.

### Modified Capabilities

- `market-data`: enriquecer a resposta de cotação de ações americanas com seu equivalente em BRL e metadados da taxa utilizada.
- `portfolio-catalog`: enriquecer posições de ações americanas com a cotação e o valor atual equivalentes em BRL, preservando os valores originais em USD.

## Impact

- Novo cliente HTTP e configurações para a API PTAX do Banco Central do Brasil.
- Contratos de resposta de ações e posições da carteira.
- Serviços, mapeadores, documentação e testes de cotações e catálogo.
