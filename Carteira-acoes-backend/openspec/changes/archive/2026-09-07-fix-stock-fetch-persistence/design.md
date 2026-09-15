## Context

Veja a motivação em `proposal.md`. A compra com ticker delega a importação ao catálogo, que consulta um adaptador de cotação, persiste a ação e registra seu histórico inicial. A atualização de cotações usa uma transação independente, enquanto a importação deve permanecer parte da transação da compra para não sobreviver a uma falha posterior da posição ou operação.

## Goals / Non-Goals

**Goals:**

- Fazer com que a importação por ticker use uma única resposta válida do provedor para preencher e persistir a ação e seu histórico inicial.
- Manter ação, histórico, posição e operação coerentes em caso de sucesso ou erro.
- Provar o comportamento por testes que exercitem o fluxo público de compra e a persistência resultante.

**Non-Goals:**

- Alterar os endpoints, os provedores suportados ou o contrato de compra.
- Adicionar tentativas automáticas, cache ou atualização agendada de cotações.
- Alterar dados de ações já cadastradas.

## Decisions

### Importação permanece na transação da compra

O serviço de catálogo continuará responsável por normalizar ticker, consultar o adaptador e construir a ação. O serviço de operações continuará como fronteira transacional da compra, de modo que exceções de consulta, validação, histórico, posição ou operação revertam todo o fluxo.

Alternativa considerada: executar a importação em transação independente. Foi descartada porque uma ação poderia permanecer cadastrada mesmo quando a compra falhasse.

### Histórico inicial usa a mesma unidade transacional da importação

Após persistir a ação para obter sua identidade, o histórico será salvo na transação já ativa, sem iniciar uma transação independente. A cotação e a data/hora serão reutilizadas da resposta já validada e normalizadas para UTC.

Alternativa considerada: reutilizar o método de atualização em transação nova. Foi descartada porque ele faria nova consulta externa e quebraria a atomicidade ou criaria divergência entre a ação e o histórico inicial.

### Cobertura orientada aos resultados persistidos

Os testes de integração verificarão que uma compra por ticker consulta o provedor e deixa ação, histórico, posição e operação persistidos. Casos de falha verificarão a ausência de registros parciais; testes de serviço cobrirão a validação e a normalização dos dados retornados.

Alternativa considerada: testar apenas interações de mocks. Foi descartada porque não detecta falhas de transação ou mapeamento de persistência.

## Risks / Trade-offs

- [O provedor pode falhar depois de a ação ter sido criada em memória] → validar a resposta antes de salvar e propagar a exceção para o limite transacional da compra.
- [Erro ao salvar o histórico pode deixar uma ação sem histórico] → manter ambos na mesma transação e testar rollback.
- [Uma alteração de propagação transacional pode afetar atualização avulsa] → limitar a mudança ao caminho de importação e preservar o isolamento da atualização explícita.

## Migration Plan

1. Implementar a correção e executar a suíte de testes de serviços e integração.
2. Publicar a aplicação sem migração de banco ou alteração de contrato HTTP.
3. Em caso de regressão, reverter a versão da aplicação; transações completas impedem a necessidade de limpeza de registros parciais novos.
