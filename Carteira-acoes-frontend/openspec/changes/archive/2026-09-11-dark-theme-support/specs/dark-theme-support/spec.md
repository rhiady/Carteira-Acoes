## Purpose

Oferecer temas claro e escuro consistentes, acessíveis e persistentes em todas as rotas do frontend, preservando a identidade Swiss/financial editorial e os dados financeiros existentes.

## ADDED Requirements

### Requirement: Temas claro e escuro são controláveis e persistentes

O sistema SHALL oferecer tema claro e escuro, com controle acessível na navegação, preferência inicial baseada em `prefers-color-scheme` quando não houver escolha manual e persistência local da escolha manual entre rotas e recarregamentos.

#### Scenario: Preferência do sistema
- **WHEN** não existe escolha manual e o sistema informa preferência escura
- **THEN** a aplicação inicia em tema escuro

#### Scenario: Escolha manual
- **WHEN** o usuário alterna para tema escuro
- **THEN** a aplicação atualiza o tema, expõe nome acessível para ativar o tema claro e mantém a escolha após refresh

### Requirement: Tokens adaptam toda a superfície visual

O sistema SHALL aplicar tokens semânticos distintos para página, cards, painéis, dialogs, menus, inputs, tabelas, texto, bordas, foco e estados financeiros nos dois temas, sem depender de inversão global ou cores hardcoded relevantes.

#### Scenario: Superfícies escuras hierárquicas
- **WHEN** o tema escuro está ativo
- **THEN** background, cards, painéis, dialogs, menus e inputs permanecem distinguíveis por superfície e borda sutil

### Requirement: Angular Material, overlays e gráficos acompanham o tema

Dialogs, menus, tooltips, snackbars, botões, inputs, selects, tabelas e demais overlays SHALL acompanhar o tema ativo. Gráficos e legendas existentes SHALL adaptar cores sem criar dados, métricas ou regras financeiras novas.

#### Scenario: Overlay durante o tema escuro
- **WHEN** o usuário abre um dialog ou menu em tema escuro
- **THEN** a superfície, texto, controles, backdrop e foco permanecem no tema escuro

### Requirement: Rotas e estados preservam acessibilidade e responsividade

Todas as rotas existentes, incluindo autenticação, dashboard, carteiras, ações, operações, analytics, corretoras, usuários e dialogs destrutivos, SHALL funcionar nos dois temas em desktop e mobile, mantendo layout, foco, contraste, sinais financeiros e estados de loading, vazio, erro, sucesso e disabled.

#### Scenario: Informação financeira negativa
- **WHEN** um valor negativo aparece em qualquer tema
- **THEN** o sinal e o texto explicam o significado e a cor não é o único indicador

#### Scenario: Troca em viewport mobile
- **WHEN** o usuário troca o tema em uma largura estreita
- **THEN** a ordem, dimensões, breakpoints e alcance dos controles permanecem equivalentes, sem overflow horizontal obrigatório

### Requirement: Aplicação evita flash e movimento excessivo

O sistema SHALL aplicar a preferência conhecida o mais cedo possível e SHALL respeitar `prefers-reduced-motion`, usando apenas transição curta e discreta quando apropriado.

#### Scenario: Carregamento com preferência persistida
- **WHEN** a aplicação reabre com tema escuro persistido
- **THEN** não apresenta um flash claro perceptível antes do tema escuro
