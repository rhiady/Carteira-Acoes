## Context

O backend já possui contratos e specs por domínio, além de autenticação e ownership recentemente consolidados. Esta change é transversal e deve provar cobertura integral sem sobrescrever mudanças existentes no worktree. O código é a autoridade para o inventário; as specs existentes são a autoridade para comportamento intencional.

## Goals / Non-Goals

**Goals:**

- Produzir inventários mecânicos completos de classes, endpoints e persistência antes de editar código.
- Rastrear endpoint → camadas → erros → testes → OpenAPI e fórmula → operandos → moeda/escala → testes.
- Corrigir somente defeitos demonstrados, preservando compatibilidade e separação de responsabilidades.
- Validar em H2 onde apropriado e obrigatoriamente no PostgreSQL real para schema e runtime.

**Non-Goals:**

- Redesenhar a arquitetura por preferência, criar APIs paralelas ou simplificar contratos para o frontend.
- Alterar Angular, Docker, Nginx ou deploy.
- Introduzir OAuth, MFA, roles ou infraestrutura de segurança desproporcional.

## Decisions

- A auditoria será executada por trilhas com inventários versionados no relatório: HTTP, domínio financeiro, persistência/integrações e qualidade operacional. Isso reduz omissões em uma mudança ampla; uma revisão informal por arquivos não fornece rastreabilidade suficiente.
- Cada endpoint terá uma linha em uma matriz com controller, request, validações, service, repositories, response, status, erros, autenticação/ownership, OpenAPI e testes. A matriz será derivada dos mappings reais, não somente da lista inicial.
- Cálculos financeiros serão verificados com tabelas de casos numéricos conhecidos e invariantes, incluindo posição zerada/reaberta, múltiplas vendas, custos, câmbio e concorrência. O histórico cronológico permanece fonte dos indicadores derivados salvo evidência contrária na spec vigente.
- O schema será comparado em três direções: migrations → PostgreSQL, metadata das entities → schema e repositories → consultas executadas. Divergências serão corrigidas apenas com migrations novas.
- Mudanças serão feitas em lotes pequenos por achado, cada um acompanhado de teste de regressão. Refactors sem mudança observável exigirão evidência de redução de duplicação/risco e não poderão ampliar o escopo.
- A validação final usará testes, package, startup e smoke tests. Bloqueios ambientais serão documentados com comando, erro e evidência alternativa, mas não serão confundidos com sucesso.

## Risks / Trade-offs

- [Escopo transversal pode ocultar omissões] → gerar listas com `rg --files`, mappings e tipos, reconciliando contagens no relatório.
- [Specs existentes podem sobrepor as novas] → usar as novas capabilities como critérios de auditoria e preservar requisitos de domínio mais específicos existentes.
- [Worktree contém mudanças de outras iniciativas] → registrar estado inicial e editar somente arquivos atribuíveis a achados desta change, sem staging amplo.
- [Smoke tests podem alterar dados] → usar dataset isolado e operações reversíveis; nunca limpar volumes ou dados reais sem autorização explícita.
- [Correção de contrato pode quebrar consumidores] → preservar comportamento válido e registrar qualquer mudança observável com justificativa, OpenAPI e teste.
- [Testes H2 podem mascarar PostgreSQL] → executar Flyway/startup e consultas críticas contra PostgreSQL real antes de concluir.

## Migration Plan

1. Capturar inventários e baseline de testes/build/runtime.
2. Auditar e corrigir por trilha, executando testes focados após cada lote.
3. Aplicar somente migrations novas quando houver divergência persistente.
4. Executar suíte completa, package, startup PostgreSQL e smoke matrix.
5. Revisar diff final e entregar relatório classificado.
6. Em rollback, reverter apenas os arquivos desta change; migrations aplicadas exigem rollback operacional compatível com os dados e não serão apagadas do histórico.
