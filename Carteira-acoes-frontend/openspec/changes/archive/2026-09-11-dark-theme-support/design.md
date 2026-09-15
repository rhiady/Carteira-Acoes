## Direction

Anchor Swiss/financial editorial. O claro permanece branco/neutro com hairlines e acento controlado; o escuro usa azul-grafite e superfícies em camadas, nunca preto absoluto, neon ou glow. O diferenciador visual é a régua de elevação por tokens: page → card → panel → overlay, com o mesmo grid, sans e numerais tabulares.

## Decisions

1. Centralizar estado e aplicação do tema em um `ThemeService` baseado em signals, com `data-theme` no elemento raiz.
2. Usar `localStorage` para escolha manual e `matchMedia('(prefers-color-scheme: dark)')` apenas como fallback inicial.
3. Definir tokens semânticos globais e sobrescrevê-los em `[data-theme='dark']`; componentes devem consumir tokens, não duplicar estilos por tema.
4. Configurar Angular Material e `OverlayContainer` com a mesma classe/atributo do tema ativo.
5. Adaptar cores de gráficos por valores semânticos computados, preservando séries e dados autoritativos.
6. Incluir controle acessível no shell e uma estratégia inicial simples no documento raiz para reduzir flash antes do bootstrap Angular.

## Validation

Auditar cores hardcoded, testar persistência e preferência do sistema, executar testes/build e revisar as rotas relevantes em desktop/mobile, com foco em contraste, overlays, foco e reduced motion.
