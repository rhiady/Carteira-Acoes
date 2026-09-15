## MODIFIED Requirements

### Requirement: Todas as telas compartilham estados e linguagem visual
O sistema SHALL revisar e apresentar Home, Dashboard, carteiras, posições, ações, histórico, operações, corretoras, usuários, formulários e dialogs com a mesma escala Swiss/financial editorial, largura útil adequada ao conteúdo, loading, vazio, erro, sucesso quando aplicável, retry seguro, foco visível e mensagens de erro priorizando `message`. A revisão SHALL incluir todas as rotas existentes, sem considerar a mudança concluída enquanto uma tela permanecer com layout legado ou comprimido.

#### Scenario: Coleção vazia
- **WHEN** uma tela recebe uma coleção vazia
- **THEN** apresenta uma explicação acessível, dimensões proporcionais e a próxima ação relevante, sem parecer quebrada

#### Scenario: Falha de operação
- **WHEN** uma solicitação falha sem mensagem utilizável
- **THEN** mostra `Não foi possível concluir a operação.`, preserva contexto e valores de formulário, e evita submissão duplicada

### Requirement: Experiência permanece acessível e responsiva
O sistema SHALL manter todas as informações e ações operáveis por teclado em desktop grande, notebook, tablet e mobile, sem overflow acidental, gráficos cortados, dialogs maiores que a viewport ou dependência exclusiva de cor. SHALL respeitar preferências de redução de movimento.

#### Scenario: Viewport estreito
- **WHEN** qualquer tela é visualizada em largura mobile
- **THEN** campos, dados, ações, tabelas e gráficos permanecem alcançáveis e legíveis sem rolagem horizontal obrigatória

#### Scenario: Navegação por teclado
- **WHEN** a pessoa percorre uma tela ou dialog com teclado
- **THEN** a ordem é lógica, os rótulos são acessíveis, o foco é visível e a ação principal pode ser acionada sem armadilha de foco
