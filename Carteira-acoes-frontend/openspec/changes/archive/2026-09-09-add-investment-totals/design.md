## Context

O frontend já possui modelos, formatters monetários, estados assíncronos e telas de carteira/posição. A implementação deve estender esses pontos existentes e seguir a linguagem Swiss/financial editorial, sem redesign geral.

## Goals / Non-Goals

**Goals:**

- Mapear os novos campos exatamente como publicados pelo backend.
- Reutilizar componentes e formatters atuais para exibir indicadores legíveis e responsivos.
- Cobrir null, sinais, moedas, loading/error e não agregação de páginas parciais em testes.

**Non-Goals:**

- Alterar backend, endpoints, deploy ou contratos.
- Criar métricas históricas, projeções ou novo fluxo de negociação.

## Decisions

- Ampliar o DTO/domain de posição com campos nullable conforme o contrato real, preservando compatibilidade com respostas antigas.
- Exibir os totais no detalhe da carteira/posição onde os dados já são consumidos; Dashboard/Analytics só serão ajustados se os componentes já receberem dados autoritativos adequados.
- Usar os formatters centrais de BRL/USD e componentes de estado existentes; nenhum cálculo cliente substituirá campos retornados.
- Validar o contrato real inspecionando services/models e fixtures/testes antes da alteração.

## Risks / Trade-offs

- [Backend ainda não publica algum campo] → manter o campo opcional e exibir estado indisponível.
- [Resposta paginada incompleta] → não renderizar agregado global derivado da página atual.
- [Variação de moeda] → selecionar formatter pela moeda da posição e manter subtotais separados.
