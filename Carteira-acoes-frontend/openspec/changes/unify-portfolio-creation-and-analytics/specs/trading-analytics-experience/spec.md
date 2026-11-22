## MODIFIED Requirements

### Requirement: Analytics apresenta contexto financeiro real

Analytics SHALL permitir selecionar a carteira quando houver mais de uma, filtrar por `Todos`, `Brasil` ou `EUA`, exibir valor, resultados total/realizado/não realizado e quantidade quando disponíveis, e oferecer comparação preço médio versus cotação, composição por valor atual e resultado por ativo sem fabricar métricas autoritativas.

#### Scenario: Analytics filtrado por mercado
- **WHEN** a pessoa seleciona um mercado em uma carteira com operações ou posições dos dois mercados
- **THEN** os cards, gráficos, tooltips e resumo textual exibem somente aquele mercado e a moeda retornada correspondente

#### Scenario: Moedas distintas
- **WHEN** a carteira possui BRL e USD
- **THEN** Analytics mantém seções e subtotais separados e não soma valores sem conversão autoritativa

#### Scenario: Carteira com posições
- **WHEN** uma carteira possui posições completas
- **THEN** Analytics apresenta visualizações úteis de composição, resultado e preço médio versus cotação com alternativa textual

### Requirement: Operation cash-flow visualization

The operation experience SHALL visualize complete published operation history as monthly cash flow and traded volume by asset, grouped by currency and selected market and using `valorLiquido`. Purchases SHALL be represented as outflow and sales as inflow; incomplete history SHALL be identified as partial.

#### Scenario: Fluxo filtrado por mercado
- **WHEN** uma carteira possui operações de Brasil e EUA e a pessoa seleciona EUA
- **THEN** fluxo de caixa e volume exibem apenas operações `mercado: EUA`, usando `valorLiquido` em USD e mantendo resumo textual acessível

#### Scenario: Histórico parcial
- **WHEN** a resposta paginada não representa todo o histórico necessário para uma agregação
- **THEN** a interface identifica o resultado como parcial e não o apresenta como total completo

#### Scenario: Operations by currency
- **WHEN** uma wallet has BRL and USD operations
- **THEN** the cash-flow and volume views remain separate and provide accessible textual summaries

### Requirement: Experiência é responsiva e acessível

Compra, Venda e Analytics SHALL funcionar em desktop e mobile, com foco visível, labels, contraste, estados vazios/loading/erro, contexto textual para gráficos, reduced motion e sem comunicação de ganho/perda apenas por cor. Os controles de mercado SHALL permanecer utilizáveis e anunciados por tecnologia assistiva em viewport estreito.

#### Scenario: Tela estreita
- **WHEN** a pessoa usa largura mobile
- **THEN** filtros, resumo, ações, tabelas e gráficos permanecem acessíveis sem overflow horizontal acidental
