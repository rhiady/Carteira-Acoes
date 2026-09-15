## Why

Os indicadores financeiros de preço médio e lucro/prejuízo são centrais para a carteira, mas precisam de uma verificação explícita para garantir que o cálculo permaneça correto diante de múltiplas compras, vendas parciais, custos e posições abertas. A mudança consolida os casos de aceite e identifica a cobertura necessária na API e nos testes.

## What Changes

- Verificar o cálculo cronológico do custo médio de aquisição e do custo restante da posição.
- Expor o preço médio de aquisição no DTO de resposta como `precoMedio`.
- Verificar o preço médio de venda ponderado, sem incorporar custos de operação.
- Verificar lucro/prejuízo realizado, não realizado e total para compras, vendas parciais e posições restantes.
- Garantir precisão decimal e valores nulos quando o indicador de venda não for aplicável.
- Validar que os indicadores exibidos nas posições e no resumo da carteira usam a mesma regra oficial, respeitando a moeda do ativo.

## Capabilities

### New Capabilities

<!-- Nenhuma capacidade nova; a mudança reforça requisitos existentes. -->

### Modified Capabilities

- `portfolio-operations`: tornar verificável o cálculo oficial dos indicadores financeiros a partir do histórico cronológico de operações.
- `portfolio-catalog`: garantir que posições e resumo exponham os indicadores calculados com precisão e segmentados por moeda.

## Impact

- `PosicaoFinanceiraCalculator` e serviços que montam posições e resumo da carteira.
- Testes unitários e de integração dos endpoints de posições, operações e resumo.
- Contratos de resposta contendo `precoMedio`, `precoMedioVenda`, `lucroPrejuizoRealizado`, `lucroPrejuizoNaoRealizado` e `lucroPrejuizo`.
- Nenhuma nova dependência externa ou alteração incompatível prevista.
