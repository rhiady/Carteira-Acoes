## Context

O frontend Angular já possui services, Reactive Forms, componentes de estado, gráficos ApexCharts e models de posições. A mudança deve complementar essas estruturas e preservar endpoints e payloads existentes.

## Goals / Non-Goals

**Goals:**

- Transformar Compra/Venda em um fluxo visual único e seguro, com contexto específico de cada operação.
- Consolidar Analytics em uma área selecionável por carteira, orientada por moeda e por dados completos.
- Reutilizar formatters, estados, dialogs e componentes de gráfico existentes.
- Manter a direção Swiss: superfícies neutras, hairlines, grid, sans única, numerais tabulares e azul de ação controlado.

**Non-Goals:**

- Criar endpoints, alterar regras contábeis ou recalcular preço médio/resultados autoritativos.
- Fabricar histórico, câmbio, benchmarks ou patrimônio consolidado sem dados suficientes.
- Adicionar nova biblioteca de gráficos sem necessidade comprovada.

## Decisions

1. Reutilizar Reactive Forms e services atuais; separar cálculos estimados em funções puras e manter respostas do backend como fonte autoritativa.
2. Derivar seleção de carteira e carregamento analítico por signals/computed, evitando estado duplicado e nested subscriptions onde a sequência puder ser composta com RxJS.
3. Usar barras/listas analíticas para comparação e resultado, donut somente para composição legível, sempre com resumo textual.
4. Passar contexto de posição explicitamente por rota/query param para Compra/Venda e histórico; sem contexto, mostrar estado indisponível em vez de inferir.
5. Implementar estados de submissão idempotentes e retry apenas em leituras seguras; preservar formulários após falhas.

## Risks / Trade-offs

- [Backend não fornece posição completa] → ocultar métricas dependentes e informar indisponibilidade.
- [Carteira multimoeda] → renderizar grupos independentes; nunca somar moedas.
- [Paginação parcial] → carregar todas as páginas para agregações ou não exibir a agregação.
- [Alta densidade analítica no mobile] → trocar gráficos densos por listas compactas com os mesmos dados.

## Migration Plan

Implementar em etapas: contratos/formatters; Compra/Venda; seleção e resumo de Analytics; visualizações/histórico; estados e acessibilidade; testes e runtime. Sem migração de dados. Rollback pela reversão dos arquivos da change.
