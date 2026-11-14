## Why

O detalhe da carteira já expõe muitos dados autoritativos, mas o resumo inicial ainda prioriza apenas ativos, valor atual e resultado. Isso dificulta responder rapidamente quanto está investido e torna a leitura da tela mais fragmentada, especialmente quando há mais de uma moeda.

## What Changes

- Exibir `Total investido` por moeda no resumo da carteira quando esse valor puder ser obtido a partir do conjunto completo de posições carregadas.
- Reorganizar a hierarquia do detalhe para destacar total investido, valor atual, resultado e ações contextuais antes dos gráficos e da lista de posições.
- Melhorar a leitura responsiva do resumo, analytics e posições em desktop, tablet e mobile, sem criar uma soma consolidada entre BRL e USD.
- Tornar estados indisponíveis, loading, erro, foco e ações da carteira mais explícitos e acessíveis.
- Manter valores financeiros, moedas, operações, cotações, endpoints e regras de cálculo autoritativos inalterados.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `investment-totals-ui`: ampliar o resumo visual para apresentar total investido por moeda apenas com posições completas e preservar indisponibilidade quando os dados não forem suficientes.
- `frontend-usability-and-visual-consistency`: ajustar hierarquia, densidade, responsividade e acessibilidade do detalhe da carteira sem inventar métricas.
- `investment-frontend-experience`: reforçar a leitura integrada de totais, posições, analytics e estados da carteira.

## Impact

- Affected UI: `src/app/features/carteiras/carteira-detail.page.ts` e estilos globais/da feature relacionados ao resumo, analytics e posições.
- Affected verification: `src/app/features/carteiras/carteira-detail.page.spec.ts` e eventuais testes de formatters/mappers se a exibição exigir cobertura adicional.
- Affected data flow: reutilização do `CarteiraService.allPositions` para uma agregação de apresentação somente quando o conjunto completo estiver disponível; nenhuma alteração de endpoint ou contrato backend.
- No new dependency, route, API, persistence, operation behavior, or currency conversion.
