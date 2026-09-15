## Context

A validação atual consulta a lista pública de corretoras da BrasilAPI, normaliza o CNPJ e aceita o primeiro item com o mesmo CNPJ e `codigo_cvm` preenchido. O contrato de `POST /corretoras` exige que somente corretoras validadas sejam persistidas. Veja a motivação em `proposal.md`.

## Goals / Non-Goals

**Goals:**

- Preservar a regra de somente cadastrar instituições com registro CVM ativo.
- Tornar a interpretação da resposta da fonte resiliente às representações válidas de CNPJ e aos campos que expressam o estado do registro.
- Evitar que uma alteração ou resposta incompleta do provedor apareça ao cliente como rejeição regulatória definitiva.

**Non-Goals:**

- Não criar uma lista manual de corretoras nem liberar cadastro sem validação externa.
- Não alterar o payload, a unicidade por CNPJ ou os endpoints públicos de corretoras.
- Não atualizar retrospectivamente os dados de corretoras já persistidas.

## Decisions

### Adaptar a resposta externa a um modelo de validação explícito

O adaptador da CVM passará a extrair, além do CNPJ e do código CVM, a informação necessária para avaliar se o registro está ativo quando ela estiver presente na resposta. A fachada fará a comparação usando apenas os 14 dígitos do CNPJ e decidirá entre registro ativo, registro inexistente/inativo e resposta inválida.

Isso isola mudanças de nomenclatura e formatação do provedor no DTO/adaptador, mantendo o serviço de corretoras orientado à regra de negócio. A alternativa de testar campos crus dentro do serviço foi descartada por espalhar detalhes da integração.

### Manter erro de negócio somente para resultado regulatório conclusivo

Ausência de item correspondente ou item explicitamente inativo continuará produzindo `422 CORRETORA_NAO_REGISTRADA_CVM`. Indisponibilidade, desserialização ou resposta que não permita identificar o registro produzirá o erro padronizado de integração, pois não demonstram que a corretora seja irregular.

Isso preserva a compatibilidade para rejeições reais e oferece uma resposta recuperável quando a fonte falhar. A alternativa de converter toda falha em 422 foi descartada porque oculta problemas operacionais como regra de negócio.

### Provar o comportamento com dublês da fonte e teste do endpoint

Os testes cobrirão CNPJ formatado, registro ativo, registro inativo/ausente e resposta sem campos mínimos. Um teste de integração do endpoint garantirá que o fluxo devolve o status e o erro esperados sem persistir dados em cenários de rejeição.

## Risks / Trade-offs

- [O provedor pode não expor estado ativo de modo uniforme] → Centralizar a interpretação no adaptador e tratar ausência de dados indispensáveis como falha de integração.
- [A lista completa de corretoras pode ter custo e indisponibilidade] → Manter a única consulta por tentativa de cadastro, os limites de tempo existentes e o tratamento de indisponibilidade.
- [Cadastro de um CNPJ de banco, em vez da pessoa jurídica da corretora do grupo] → Rejeitar somente após a fonte confirmar que não há registro ativo para aquele CNPJ e retornar erro inequívoco.

## Migration Plan

1. Publicar a adaptação e os testes sem mudança de esquema ou migração de dados.
2. Monitorar respostas de integração e cadastros recusados após a publicação.
3. Em caso de regressão, reverter a adaptação; não há alteração de dados persistidos a desfazer.
