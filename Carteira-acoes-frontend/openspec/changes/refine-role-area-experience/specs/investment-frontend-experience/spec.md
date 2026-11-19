## MODIFIED Requirements

### Requirement: Todas as telas compartilham estados e linguagem visual

O sistema SHALL revisar e apresentar Home, Dashboard, carteiras, posições, ações, histórico, operações, corretoras, usuários, formulários e dialogs com a mesma escala Swiss/financial editorial, largura útil adequada ao conteúdo, loading, vazio, erro, sucesso quando aplicável, retry seguro, foco visível e mensagens de erro priorizando `message`. A revisão SHALL incluir todas as rotas existentes, sem considerar a mudança concluída enquanto uma tela permanecer com layout legado ou comprimido. As entradas autenticadas comum e administrativa SHALL compartilhar tokens, foco, densidade e linguagem de estados, mas manter seus conteúdos e navegações separados por autoridade.

#### Scenario: Coleção vazia
- **WHEN** uma tela recebe uma coleção vazia
- **THEN** apresenta uma explicação acessível, dimensões proporcionais e a próxima ação relevante, sem parecer quebrada

#### Scenario: Falha de operação
- **WHEN** uma solicitação falha sem mensagem utilizável
- **THEN** mostra `Não foi possível concluir a operação.`, preserva contexto e valores de formulário, e evita submissão duplicada

#### Scenario: Entrada de papel é carregada
- **WHEN** a pessoa abre a área comum ou administrativa
- **THEN** a superfície usa a linguagem visual compartilhada, mostra o contexto atual, apresenta estados reais e não mistura navegação ou dados da outra autoridade

### Requirement: Experiência permanece acessível e responsiva

O sistema SHALL manter todas as informações e ações operáveis por teclado em desktop grande, notebook, tablet e mobile, sem overflow acidental, gráficos cortados, dialogs maiores que a viewport ou dependência exclusiva de cor. SHALL respeitar preferências de redução de movimento. As entradas comum e administrativa SHALL manter foco visível, ordem lógica, alvos confortáveis e leitura adequada nos temas claro e escuro.

#### Scenario: Viewport estreito
- **WHEN** qualquer tela é visualizada em largura mobile
- **THEN** campos, dados, ações, tabelas e gráficos permanecem alcançáveis e legíveis sem rolagem horizontal obrigatória

#### Scenario: Navegação por teclado
- **WHEN** a pessoa percorre uma tela ou dialog com teclado
- **THEN** a ordem é lógica, os rótulos são acessíveis, o foco é visível e a ação principal pode ser acionada sem armadilha de foco

#### Scenario: Tema é alternado em uma área autenticada
- **WHEN** a pessoa muda entre tema claro e escuro na área comum ou administrativa
- **THEN** superfícies, textos, controles, estados e foco continuam distinguíveis e legíveis sem perda do contexto ou dos valores carregados

### Requirement: Financial surfaces visibly use authenticated context

The shell SHALL identify the authenticated person discreetly and Dashboard, wallets, operations, purchase, sale and analytics SHALL derive context from the current authenticated user. The visual language SHALL remain Swiss / financial editorial, calm, professional and consistent with the existing login. The common-user entry SHALL expose the person's investment context, while the administrator entry SHALL expose management context without silently loading another person's private wallet context.

#### Scenario: Authenticated shell
- **WHEN** a person is authenticated
- **THEN** the shell identifies that person without a complex profile menu and keeps `Sair` available

#### Scenario: User changes
- **WHEN** the authenticated identity changes
- **THEN** affected financial surfaces reload or clear their data and do not display stale values from the previous identity

#### Scenario: Administrator opens management context
- **WHEN** an administrator enters the administrative area
- **THEN** the shell identifies the management context, keeps `Sair` available and does not present another person's private financial data as the default content
