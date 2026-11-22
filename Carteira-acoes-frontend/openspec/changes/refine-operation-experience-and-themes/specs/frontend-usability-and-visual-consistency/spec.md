## MODIFIED Requirements

### Requirement: Layout usa espaço e densidade de forma consistente

O sistema SHALL usar uma escala coerente de espaçamento, containers adequados ao tipo de tela, tipografia hierárquica e agrupamentos que reduzam cards, bordas e caixas redundantes sem comprimir conteúdo financeiro. Compra e Venda SHALL organizar referência do ativo, dados da ordem, custos opcionais, estimativa e confirmação como sequência financeira escaneável, com uma única ação primária visualmente dominante e sem superfícies decorativas redundantes. Histórico e detalhe de operação SHALL priorizar identificação, valores e próxima ação antes de metadados secundários. A listagem de carteiras SHALL privilegiar leitura financeira e continuidade da tarefa, exibindo dados reais por moeda em regiões proporcionais, sem repetir identificadores técnicos como elemento dominante.

#### Scenario: Dashboard em desktop
- **WHEN** o dashboard é exibido em viewport amplo
- **THEN** patrimônio, resultado, capital investido, posições principais e ações rápidas ocupam a hierarquia visual antes de métricas secundárias

#### Scenario: Compra em desktop
- **WHEN** uma pessoa abre compra em viewport amplo
- **THEN** identifica a referência do ativo, os dados da ordem, a estimativa e a confirmação em regiões proporcionais e legíveis

#### Scenario: Compra em mobile
- **WHEN** uma pessoa abre compra em viewport estreito
- **THEN** as mesmas regiões se organizam em uma única sequência vertical sem sobreposição, corte ou rolagem horizontal

#### Scenario: Carteiras em viewport estreito
- **WHEN** uma pessoa consulta suas carteiras em viewport estreito
- **THEN** nome, valores por moeda, resultados, ativos e ação para abrir a carteira permanecem em ordem de leitura e sem rolagem horizontal

### Requirement: Estados e dialogs são consistentes e orientam continuidade

Loading, empty, error, success, disabled, forms e dialogs SHALL preservar layout, explicar o próximo passo quando aplicável, manter foco e não parecerem superfícies desconectadas do tema ativo. A troca entre tema claro e escuro SHALL preservar conteúdo, valores preenchidos, estado habilitado e contexto da rota aberta.

#### Scenario: Coleção vazia
- **WHEN** não há carteiras, posições ou resultados
- **THEN** a interface explica a situação e apresenta a próxima ação relevante quando ela existir

#### Scenario: Troca de tema durante uma operação
- **WHEN** uma pessoa alterna o tema com uma compra, venda ou detalhe de operação aberto
- **THEN** dados apresentados, valores já preenchidos, foco funcional e ordem do layout são preservados
