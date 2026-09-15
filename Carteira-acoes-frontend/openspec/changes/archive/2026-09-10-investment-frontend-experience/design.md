## Context

O projeto é Angular com organização em `core`, `features` e `shared`, Angular Material, Reactive Forms e contratos já publicados. A implementação deve complementar essa arquitetura, sem criar endpoints ou assumir campos além dos responses confirmados. Ver `proposal.md` e a especificação desta change para o comportamento esperado.

## Goals / Non-Goals

**Goals:**

- Centralizar tipos de posição, resumo, moeda e estados de carregamento em models/formatters/services apropriados.
- Preservar campos financeiros do backend como autoritativos; deixar no componente apenas métricas de apresentação seguras.
- Definir um único sistema visual Swiss: superfícies brancas/neutras, regras de 1px, grid assimétrico, sans única, numerais tabulares e um acento controlado.
- Reorganizar dashboard e analytics em contexto, métricas, visualização principal, controles e comparativos.
- Reutilizar componentes compartilhados para métricas, estados, tabelas responsivas, mensagens e dialogs.
- Testar estados incompletos, nulos, múltiplas moedas, acessibilidade e larguras desktop/mobile.

**Non-Goals:**

- Criar ou alterar endpoints, regras contábeis, cálculo de preço médio, câmbio, patrimônio histórico ou projeções.
- Alterar contratos de compra, venda, criação ou edição de usuário.
- Introduzir uma biblioteca de gráficos ou uma nova arquitetura sem evidência de necessidade no projeto.

## Decisions

1. **Modelo financeiro tipado e formatters centralizados.** Expandir os models/DTOs e mappers existentes; formatters serão responsáveis por BRL/USD, sinais, datas e indisponibilidade. Isso evita duplicação e impede que templates reconstruam valores. Alternativa rejeitada: cálculos diretamente nos componentes.

2. **Derivados de apresentação isolados.** Criar funções puras para variação percentual, peso e rankings, com guardas para nulo, zero, moeda incompatível e páginas parciais. Os campos retornados pelo backend nunca serão sobrescritos. Alternativa rejeitada: recompor financeiro via operações.

3. **Dashboard e analytics orientados por evidência.** Consumir somente responses existentes; renderizar gráficos apenas com dados suficientes, com legendas e resumo textual. A composição será por ação e `valorAtual`; resultados usarão `lucroPrejuizo`. Alternativa rejeitada: preencher lacunas com séries sintéticas.

4. **Direção visual Swiss/financial editorial.** Usar branco/neutro, petrol/azul como apoio já compatível com o produto, um acento único para ação primária, hairlines, alinhamento à esquerda, escala tipográfica compacta e `font-variant-numeric: tabular-nums`. Diferenciador: uma faixa de contexto e régua de métricas alinhadas ao grid, que mantém ticker e números como eixo visual. Angular Material permanece para comportamento, com superfícies e estados tematizados.

5. **Estados e acessibilidade como componentes compartilhados.** Padronizar loading, vazio, erro/retry, sucesso e indisponível, incluindo `aria-live`, foco, labels e texto alternativo dos gráficos. Dialogs terão submissão idempotente, cancelamento e preservação do formulário. Alternativa rejeitada: mensagens ad hoc por tela.

6. **Refatoração por fatias de rota.** Primeiro core/contratos, depois dashboard/analytics e, por fim, cada grupo de rotas (carteiras, ações, operações, cadastros) usando os mesmos tokens e componentes. Isso reduz regressões e permite validar visualmente cada superfície.

## Risks / Trade-offs

- [Contrato backend incompleto] → omitir a métrica e registrar a limitação; não fabricar valor.
- [Responses paginadas insuficientes para ranking] → restringir o ranking ao contexto completo carregado ou exibir indisponível.
- [Mudanças visuais amplas] → manter contratos, rotas e Reactive Forms; executar build, testes e inspeção de todas as rotas.
- [Gráficos difíceis em mobile] → trocar para lista/barras compactas quando necessário, preservando os mesmos dados e alternativa textual.
- [Dependência de runtime/backend] → validar com fixtures/responses reais disponíveis e documentar qualquer rota não exercitável.

## Migration Plan

Implementar em commits/fases revisáveis: contrato e formatters; componentes compartilhados; dashboard/analytics; demais rotas; validação visual e acessibilidade. O rollback é a reversão das alterações da change, sem migração de dados ou mudança de API.

