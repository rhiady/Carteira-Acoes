## MODIFIED Requirements

### Requirement: Tokens adaptam toda a superfície visual

O sistema SHALL aplicar um único conjunto coerente de tokens semânticos por tema para página, surface, surface-raised, surface-hover, dialogs, menus, inputs, tabelas, texto, bordas, foco, ações e estados financeiros. O tema claro SHALL apresentar uma superfície clara e legível; o tema escuro SHALL usar grafite/carvão em camadas sutis, evitar preto absoluto como superfície principal, evitar neon e manter uma única cor de destaque usada com economia. Regras globais ou de componentes SHALL NOT substituir os tokens do tema claro pelo tratamento escuro.

#### Scenario: Tema claro é ativado
- **WHEN** o tema claro está ativo
- **THEN** página, cards, painéis, inputs, navegação e ações usam superfícies claras distinguíveis, texto legível e a mesma hierarquia funcional do tema escuro

#### Scenario: Superfícies escuras confortáveis
- **WHEN** o tema escuro está ativo
- **THEN** página, surfaces, cards, painéis e overlays têm diferenças discretas de luminância, textos secundários permanecem legíveis e nenhum bloco claro desconectado aparece

#### Scenario: Superfícies escuras hierárquicas
- **WHEN** o tema escuro está ativo
- **THEN** background, cards, painéis, dialogs, menus e inputs permanecem distinguíveis por superfície e borda sutil

### Requirement: Angular Material, overlays e gráficos acompanham o tema

Dialogs, menus, tooltips, snackbars, botões, inputs, selects, tabelas e demais overlays SHALL acompanhar o tema ativo, inclusive quando a troca ocorrer com um overlay aberto. Gráficos e legendas existentes SHALL adaptar cores sem criar dados, métricas ou regras financeiras novas. Estados default, hover, active, focus-visible e disabled dos controles SHALL permanecer distinguíveis e legíveis em ambos os temas.

#### Scenario: Overlay durante o tema escuro
- **WHEN** o usuário abre um dialog ou menu em tema escuro
- **THEN** a superfície, texto, controles, backdrop e foco permanecem no tema escuro

#### Scenario: Controle de operação em ambos os temas
- **WHEN** uma pessoa interage com campos, ações secundárias e a ação principal de uma operação nos temas claro e escuro
- **THEN** cada estado do controle permanece legível, o foco é visível e a ação principal é identificável sem depender apenas de cor
