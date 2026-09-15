## Context

Atualmente `POST /acoes` cria ações globais antes de qualquer operação, e `POST /operacoes/compras` exige `acaoId`. A compra já é transacional e busca a posição por carteira e ação. Consulte `proposal.md` e os deltas de especificação para o comportamento desejado.

## Goals / Non-Goals

**Goals:**

- Garantir que uma ação só seja inserida no catálogo em consequência de uma compra vinculada a carteira.
- Manter criação de ação, atualização de posição e persistência da operação atômicas.
- Dar aos clientes uma forma inequívoca de comprar ação existente ou informar novo ticker e mercado.

**Non-Goals:**

- Duplicar uma ação por carteira; o catálogo continuará único por ticker.
- Alterar vendas, histórico, cálculos financeiros ou o contrato das consultas de ações.
- Manter compatibilidade do endpoint de cadastro direto de ação.

## Decisions

### Usar formas mutuamente exclusivas de identificar ação na compra

`CompraRequest` aceitará `acaoId` para ação existente ou `ticker` acompanhado de `mercado` para resolução/criação. A validação de classe garantirá exatamente uma forma: `acaoId` sozinho, ou ticker e mercado juntos. Isso evita decisões implícitas quando cliente enviar campos conflitantes.

Alternativa considerada: sempre receber ticker e mercado. Foi descartada porque tornaria os clientes que já possuem o identificador da ação dependentes de dados redundantes.

### Centralizar a resolução/criação de ação no fluxo transacional de compra

O serviço de operações resolverá a ação antes de criar/atualizar `CarteiraAcao`. Para ticker novo, reutilizará a lógica existente de importação de cotação e empresa, mas persistirá a ação dentro da mesma transação. Qualquer falha de validação, consulta externa ou persistência fará rollback de ação, posição e operação.

Alternativa considerada: criar ação antes de chamar o serviço de compra. Foi descartada porque permite novamente uma ação sem carteira ou uma criação parcialmente concluída.

### Retirar somente a criação do recurso de ações

`POST /acoes` será removido; os endpoints de consulta e atualização de cotação de ações existentes permanecerão. A remoção produzirá resposta de método não permitido para clientes que continuarem usando o endpoint.

## Risks / Trade-offs

- [Clientes existentes usam `POST /acoes`] → documentar a quebra e migrar para compra com ticker/mercado.
- [Duas compras simultâneas tentam criar o mesmo ticker] → preservar unicidade do ticker, tratar o conflito de forma controlada e nunca criar posições inconsistentes.
- [Falha do provedor para ticker novo] → validar antes de alterar posição/operação e preservar rollback total.
- [Cliente escolhe `acaoId` de ação que não pertence à carteira] → permitir a compra, criando posição independente apenas na carteira informada.

## Migration Plan

1. Atualizar clientes para enviar `acaoId` para ações conhecidas ou `ticker` e `mercado` para novas compras.
2. Implantar a mudança removendo o cadastro direto; não há migração de banco de dados.
3. Monitorar respostas de validação e de ticker externo após a migração; a reversão restaura o endpoint de cadastro direto, sem alteração nos dados existentes.
