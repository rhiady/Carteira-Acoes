## Context

A carteira contém nome e usuário, e o sistema mantém um catálogo de corretoras previamente validadas. Conforme a proposta e a especificação delta, a carteira passará a referenciar esse catálogo como dado obrigatório.

## Goals / Non-Goals

**Goals:**

- Tornar uma corretora validada um vínculo persistido e obrigatório para novos cadastros de carteira.
- Manter o contrato de leitura de carteira consistente com o cadastro.
- Reutilizar o cadastro e a validação existentes de `Corretora` como origem confiável do vínculo.

**Non-Goals:**

- Alterar os critérios de validação e cadastro de `Corretora`.
- Aceitar nome livre, CNPJ ou outra identificação textual de corretora no cadastro da carteira.
- Consolidar carteiras por corretora ou adicionar operações específicas de corretora.

## Decisions

### Chave estrangeira obrigatória para corretora

Adicionar `corretora_id` não nulo à tabela de carteiras, com chave estrangeira para `corretora`, e mapear uma associação obrigatória de carteira para corretora. Essa decisão garante que o vínculo só seja criado para uma instituição já validada. O inverso não será exclusivo: uma corretora poderá estar vinculada a várias carteiras.

### Validação por identificador no contrato de criação

O request de carteira exigirá `corretoraId`, e a resposta também o incluirá. O serviço consultará a corretora pelo identificador antes de persistir a carteira; uma referência inexistente será rejeitada. A alternativa de aceitar texto livre foi descartada porque não evidencia que a corretora passou pelas validações do sistema.

### Migração com restrição de nulidade

A migração Flyway criará a coluna e a chave estrangeira, preservando a obrigatoriedade também no armazenamento. Em ambientes que já possuam carteiras, os registros precisam receber o identificador da corretora validada correta antes da aplicação da restrição; não será introduzido valor fictício para mascarar essa informação.

## Risks / Trade-offs

- [Bases já populadas não possuem corretora] → Associar cada carteira a uma corretora já validada antes de ativar a restrição ou executar uma migração operacional de backfill aprovada.
- [Uma corretora não cadastrada ainda é necessária] → Cadastrá-la pelo fluxo de validação antes de criar a carteira.
