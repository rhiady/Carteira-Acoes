## Why

O backend passou a fornecer totais financeiros autoritativos, mas a interface ainda não os apresenta com significado suficiente para separar capital, fluxos de compra/venda e resultados. Esta change torna esses dados visíveis nas superfícies financeiras já existentes, sem inventar métricas nem alterar contratos.

## What Changes

- Alinhar o modelo e o mapper de posição aos campos de totais financeiros publicados pelo backend.
- Exibir investido atualmente, totais comprado/vendido e resultados realizado, não realizado e total no detalhe de posição/carteira quando disponíveis.
- Preservar moeda, sinais, null, loading, erro e paginação sem derivar totais globais de páginas incompletas.
- Adicionar testes de contrato, mapeamento e apresentação; validar build e testes Angular.

## Capabilities

### New Capabilities
- `investment-totals-ui`: Apresentação clara e autoritativa dos totais financeiros de posições e carteiras.

### Modified Capabilities
- `portfolio-analytics`: Alterar os requisitos de métricas exibidas para incluir os novos totais sem recálculo ou agregação indevida.

## Impact

Somente frontend Angular: models/interfaces, mappers, services consumidores, componentes de detalhe/dashboard/analytics pertinentes e testes. Nenhuma alteração em backend, API ou deploy.
