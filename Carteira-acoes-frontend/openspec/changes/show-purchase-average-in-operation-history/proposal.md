## Why

O histórico de operações informa preço unitário, valores e custos de cada registro, mas não mostra o preço médio de custo/compra autoritativo da posição correspondente. Isso torna a leitura de compras menos contextual e pode levar à confusão com o preço médio de venda.

## What Changes

- Exibir `Preço médio de compra` somente nos registros de compra do histórico de uma carteira.
- Consultar o `precoMedio` publicado nas posições atuais da mesma carteira, mantendo sua moeda.
- Mostrar `—` quando a posição atual ou o campo não estiver disponível, sem recalcular valores no cliente.
- Manter registros de venda sem preço médio de compra e sem usar `precoMedioVenda` como substituto.

## Capabilities

### New Capabilities

Nenhuma.

### Modified Capabilities

- `trading-analytics-experience`: tornar o histórico de operações capaz de contextualizar compras com o preço médio de custo autoritativo, preservando a suficiência e a origem dos dados.

## Impact

- UI: `HistoricoPage` e seus testes.
- Dados: reutiliza somente `GET /carteiras/{id}/acoes` e o campo já publicado `precoMedio`.
- API/backend: nenhum endpoint, payload, cálculo financeiro ou contrato é alterado.
- Dependências: nenhuma; reutiliza Angular, `CarteiraService`, `OperacaoService` e formatadores existentes.
