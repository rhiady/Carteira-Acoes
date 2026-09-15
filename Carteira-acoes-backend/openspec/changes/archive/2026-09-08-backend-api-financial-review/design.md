## Context

O backend já possui contratos e regras distribuídos entre controllers, DTOs, services, repositories, entidades, migrations e documentação OpenAPI. A implementação deve ser auditada contra as deltas desta mudança, preservando compatibilidade e limitando alterações ao backend.

## Goals / Non-Goals

**Goals:**

- Construir uma matriz endpoint → request/response/status/erro/OpenAPI.
- Validar cálculos financeiros a partir do histórico, precisão decimal e consistência transacional.
- Corrigir falhas confirmadas com testes de regressão.
- Verificar schema, integrações, timestamps, configuração e inicialização.

**Non-Goals:**

- Criar endpoints, campos ou regras não presentes no contrato atual.
- Alterar frontend, deploy, Nginx, Compose ou infraestrutura compartilhada.
- Implementar autenticação/JWT.

## Decisions

- Usar o código e as migrations como fonte de verdade inicial; tratar divergências entre documentação e comportamento como achados, decidindo correções pela compatibilidade do contrato existente.
- Manter regras de domínio em services e validações estruturais em DTOs/Bean Validation; controllers permanecem adaptadores HTTP.
- Centralizar o mapeamento de exceções no handler existente, preservando o corpo com timestamp, status, código, mensagem e path e evitando conversão de erros conhecidos para 500.
- Manter `BigDecimal` em toda aritmética monetária e explicitar escala/rounding apenas onde o domínio exigir; não substituir null financeiro por zero sem requisito.
- Garantir atomicidade com o boundary transacional já usado pelo fluxo de operação e avaliar locking/controle de versão somente se a auditoria demonstrar risco concorrente real.
- Validar ordenação por campos públicos e paginação no backend, rejeitando propriedades desconhecidas.
- Atualizar OpenAPI junto com qualquer mudança observável, incluindo schemas de posições e erros.

## Risks / Trade-offs

- [Contrato legado divergente] → preservar nomes/status existentes quando não houver evidência de quebra e cobrir o comportamento com testes.
- [Diferenças entre H2 e PostgreSQL] → executar testes e, quando possível, validação de schema/queries no ambiente PostgreSQL configurado.
- [Provedor externo instável] → usar dublês nos testes e confirmar que falhas não substituem dados válidos.
- [Concorrência de posições] → reproduzir cenários concorrentes antes de introduzir locking ou mudanças de schema.

## Migration Plan

Não há migração planejada por padrão. Se a auditoria confirmar constraint ou coluna necessária, criar migration incremental, validar aplicação em banco limpo e existente e manter rollback por versão anterior sem reescrever migrations aplicadas.
