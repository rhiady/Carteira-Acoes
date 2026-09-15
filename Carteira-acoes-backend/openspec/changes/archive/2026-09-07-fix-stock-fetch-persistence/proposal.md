## Why

O fluxo de compra que identifica uma ação por ticker não está entregando de forma confiável o comportamento esperado de consultar o provedor de mercado e deixar a ação importada persistida. Isso impede que a carteira registre compras de ativos ainda ausentes do catálogo e torna os dados de cotação inconsistentes.

## What Changes

- Corrigir o fluxo de importação de ação iniciado por uma compra com ticker e mercado, garantindo a consulta ao provedor compatível antes da persistência.
- Garantir a persistência atômica da ação importada, seus dados de mercado e a cotação/histórico inicial quando a resposta externa for válida.
- Preservar a integridade do fluxo de compra: uma falha de consulta ou persistência não pode criar ação, posição ou operação parcial.
- Cobrir o comportamento com testes de serviço e integração para importação bem-sucedida e falhas do provedor.

## Capabilities

### New Capabilities

_Nenhuma._

### Modified Capabilities

- `portfolio-catalog`: torna verificável a criação e persistência de uma ação importada por ticker durante uma compra.
- `market-data`: torna verificável a persistência da cotação inicial e de seu histórico após consulta válida do provedor.

## Impact

Afeta o fluxo de compra e importação em `OperacaoService` e `AcaoService`, os adaptadores de cotação, os repositórios de ações e histórico e seus testes. Não altera os endpoints públicos nem introduz dependências novas.
