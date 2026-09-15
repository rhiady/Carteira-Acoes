## Context

`Carteira` referencia obrigatoriamente `Usuario` e `Corretora`, além de possuir posições e operações. `Acao` é referenciada por posições, operações e `HistoricoCotacao`; a migration V11 define cascade do histórico de cotações na exclusão da ação. As migrations definem foreign keys sem cascade destrutivo para carteira/operação; a entidade possui cascade de posições, portanto os caminhos de exclusão precisam rejeitar recursos com vínculos antes de chamar o repositório. `Corretora` não mantém coleção inversa, mas a tabela carteira referencia seu ID.

## Goals / Non-Goals

**Goals:**

- Isolar a atualização da carteira em DTO validado contendo apenas nome.
- Centralizar ownership, existência, vínculo e atomicidade nos services.
- Usar verificações `exists`/`count` para impedir exclusões inválidas e converter conflitos em `ApiException` 409.
- Documentar e testar os contratos HTTP sem modificar o fluxo financeiro.

**Non-Goals:**

- Alterar Compra, Venda, cálculo financeiro, schema, frontend ou deploy.
- Permitir edição de corretora ou remoção seletiva de histórico/posições.

## Decisions

- **DTO dedicado para PUT:** criar request record com `@NotBlank` e limite de tamanho compatível com `CarteiraRequest`; o service fará `trim`, como na criação. Não reutilizar entity ou request de criação, pois ambos permitiriam campos fora do contrato.
- **Service como unidade transacional:** resources apenas delegam. Métodos de atualização/exclusão serão transacionais, validarão a carteira no contexto do usuário e executarão validações antes do delete.
- **Bloqueio explícito de carteira:** verificar existência de `Operacao` e `CarteiraAcao` por carteira. Mesmo que posições tenham cascade JPA, nenhum cascade será usado para apagar dados; qualquer vínculo retorna conflito antes da operação.
- **Bloqueio explícito de corretora:** adicionar consulta de existência de carteira por `corretoraId` no `CarteiraRepository`. Vínculos futuros devem seguir a mesma regra; não assumir relação inversa em memória.
- **Bloqueio explícito de ação:** adicionar consultas de existência em operações, posições e histórico de cotações. Qualquer vínculo bloqueia `DELETE /acoes/{id}`; isso evita que o cascade da V11 apague histórico para viabilizar a exclusão.
- **Contrato de conflito:** usar a fábrica de conflito já existente em `ApiException`, com códigos estáveis e mensagens específicas (operações/posições ou carteiras vinculadas). O handler global permanece responsável pelo formato `ErroResponse`; exceções de integridade devem ter tradução defensiva caso uma corrida ultrapasse a verificação.
- **Concorrência:** manter validação e delete na mesma transação; considerar constraint exception como conflito de domínio, garantindo rollback. Não adicionar migração nem cascade.
- **OpenAPI e testes:** anotar 200 para PUT e 204/400/404/409 conforme endpoint, e adicionar testes de resource/service cobrindo campos protegidos, ownership, preservação e ausência de registros após sucesso.

## Risks / Trade-offs

- [Condição de corrida entre `exists` e `delete`] → transação e tratamento de integridade referencial como 409; rollback impede estado parcial.
- [Cascade de posições existente na entidade] → exclusão é proibida quando qualquer posição existe; não alterar o cascade nesta change para evitar impacto financeiro.
- [Novos vínculos adicionados no futuro] → consultas de bloqueio devem ser mantidas junto às entidades/migrations e cobertas por teste de integração.
- [Cascade existente em histórico de cotação] → manter a ação vinculada enquanto houver histórico; não usar o delete para limpar histórico financeiro.

## Migration Plan

Nenhuma migração de banco é necessária. Implantar o backend com os endpoints e testes; rollback consiste em reverter o artefato da aplicação, sem alteração de dados ou schema.
