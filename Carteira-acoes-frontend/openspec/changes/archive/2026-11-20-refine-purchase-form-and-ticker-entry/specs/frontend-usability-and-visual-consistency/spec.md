## MODIFIED Requirements

### Requirement: Layout usa espaço e densidade de forma consistente
O sistema SHALL usar uma escala coerente de espaçamento, containers adequados ao tipo de tela, tipografia hierárquica e agrupamentos que reduzam cards, bordas e caixas redundantes sem comprimir conteúdo financeiro. A compra SHALL organizar referência do ativo, ordem, custos opcionais, total estimado e confirmação como uma sequência financeira escaneável, com uma única ação primária visualmente dominante e sem superfícies decorativas redundantes.

#### Scenario: Dashboard em desktop
- **WHEN** o dashboard é exibido em viewport amplo
- **THEN** patrimônio, resultado, capital investido, posições principais e ações rápidas ocupam a hierarquia visual antes de métricas secundárias

#### Scenario: Compra em desktop
- **WHEN** uma pessoa abre a compra em viewport amplo
- **THEN** identifica a referência do ativo, os dados da ordem, o total estimado e a confirmação em regiões proporcionais e legíveis

#### Scenario: Compra em mobile
- **WHEN** uma pessoa abre a compra em viewport estreito
- **THEN** as mesmas regiões se organizam em uma única sequência vertical sem sobreposição, corte ou rolagem horizontal
