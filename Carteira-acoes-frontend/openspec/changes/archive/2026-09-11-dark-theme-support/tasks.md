## Discovery and design

- [x] Auditar tokens, estilos globais, cores hardcoded, Material theme, OverlayContainer e gráficos existentes.
- [x] Definir matriz de tokens light/dark e critérios de contraste/foco.

## Theme foundation

- [x] Implementar ThemeService com signals, preferência do sistema, persistência local e aplicação no document root.
- [x] Adicionar bootstrap anti-flash compatível com a arquitetura Angular.
- [x] Integrar tokens globais e estados financeiros nos temas claro/escuro.
- [x] Adicionar controle acessível no header/sidebar para alternar temas.
- [x] Integrar Angular Material e overlays ao tema ativo.

## Surfaces and data visualization

- [x] Adaptar gráficos existentes, legendas e tooltips sem alterar dados ou métricas.
- [x] Revisar shell, navegação, dialogs, menus, forms, tabelas e estados loading/empty/error/success/disabled.
- [x] Revisar Login, Cadastro, Dashboard, Carteiras, Ações, Operações, Analytics, Corretoras e Usuários.
- [x] Validar dialogs de manutenção, Compra e Venda sem alterar seus contratos ou regras.

## Quality

- [x] Adicionar testes de serviço, alternância, persistência, prefers-color-scheme e controle acessível.
- [x] Executar testes, checagens de acessibilidade/contraste e `npm run build`.
- [x] Validar visualmente claro/escuro em desktop, notebook, tablet e mobile.
- [x] Confirmar ausência de alterações em backend, deploy, dados financeiros e layout responsivo.
