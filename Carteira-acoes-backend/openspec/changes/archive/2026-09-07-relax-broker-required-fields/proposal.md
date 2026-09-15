## Why

O cadastro de corretoras atualmente exige dados de endereço retornados por integrações externas, mesmo quando a instituição já pode ser identificada pelo CNPJ e nome empresarial. Isso impede persistir registros válidos com dados complementares ausentes ou incompletos.

## What Changes

- Tornar opcionais, no banco e no modelo de corretora, todos os atributos além de CNPJ e razão social (nome da instituição).
- Manter CNPJ único e razão social como os únicos dados obrigatórios para persistir uma corretora.
- Permitir que o cadastro persista uma corretora validada cujo provedor não retorne campos complementares, sem alterar a exigência de validação de CNPJ e CVM.
- **BREAKING**: respostas de corretora podem conter `null` nos campos cadastrais e de endereço que antes eram sempre preenchidos.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `broker-validation`: o cadastro de corretora passa a requerer apenas CNPJ e razão social; dados cadastrais e de endereço complementares deixam de ser obrigatórios.

## Impact

- Banco de dados: nova migração remove restrições `NOT NULL` dos campos complementares de `corretora`.
- Domínio e mapeamento: devem aceitar valores ausentes vindos da fonte de CNPJ.
- API: consumidores devem aceitar campos complementares nulos na resposta de corretora.
- Validação externa, unicidade do CNPJ e vínculo com carteira permanecem inalterados.
