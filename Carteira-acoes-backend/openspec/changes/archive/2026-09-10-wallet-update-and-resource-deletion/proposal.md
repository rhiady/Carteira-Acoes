## Why

Carteiras e corretoras hoje podem ser criadas e consultadas, mas não possuem operações de manutenção seguras. A change adiciona atualização restrita do nome da carteira e exclusões condicionadas à ausência de vínculos, evitando perda de histórico financeiro e expondo conflitos compreensíveis.

## What Changes

- Adicionar `PUT /carteiras/{id}` com request DTO contendo somente `nome`.
- Validar e normalizar o nome como na criação, respeitando ownership e retornando 404 para carteira inexistente.
- Adicionar `DELETE /carteiras/{id}` e `DELETE /corretoras/{id}`.
- Adicionar `DELETE /acoes/{id}` para ações sem operações, posições ou histórico de cotações vinculados.
- Bloquear exclusões quando existirem operações, posições ou outros vínculos reais, retornando conflito de domínio 409.
- Documentar os endpoints e cobrir sucesso, validação, inexistência, ownership e vínculos com testes.
- Preservar operações financeiras e não alterar frontend ou deploy.

## Capabilities

### New Capabilities

- `wallet-resource-maintenance`: atualização limitada e exclusão segura de carteiras, corretoras e ações.

### Modified Capabilities

- `portfolio-operations`: a carteira passa a ter manutenção sem modificar as regras de Compra/Venda; exclusão deve preservar operações vinculadas.

## Impact

- Afeta resources, services, DTOs, repositories, exceções e documentação OpenAPI do backend, incluindo o fluxo de ações.
- Exige consultas eficientes de existência para vínculos de `CarteiraAcao`, `Operacao` e carteiras associadas à `Corretora`.
- Requer testes de integração e de contrato de erro; não requer mudanças de schema, frontend, deploy ou integrações externas.
