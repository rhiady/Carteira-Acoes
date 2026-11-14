## ADDED Requirements

### Requirement: Detalhe da carteira prioriza resumo, ação e posições
O detalhe da carteira SHALL apresentar nome, total investido, valor atual, resultado e ações contextuais em uma hierarquia escaneável, antes de analytics e detalhes secundários. SHALL existir no máximo uma ação visualmente dominante, mantendo compra, venda, atualização e ações administrativas compreensíveis.

#### Scenario: Usuário abre uma carteira com dados
- **WHEN** o detalhe possui resumo e posições carregados
- **THEN** a pessoa identifica a carteira, os principais valores, a ação primária e a seção de posições sem precisar percorrer uma grade indistinta de cards

### Requirement: Detalhe permanece legível em viewport estreito
O resumo, analytics, posições, controles e estados do detalhe SHALL reorganizar-se progressivamente em tablet e mobile, mantendo labels, valores, ações, foco visível e ausência de overflow horizontal acidental.

#### Scenario: Usuário consulta no mobile
- **WHEN** a tela é exibida em largura estreita
- **THEN** os indicadores e ações ficam acessíveis em fluxo vertical, os gráficos não são cortados e os dados financeiros continuam distinguíveis por texto e hierarquia
