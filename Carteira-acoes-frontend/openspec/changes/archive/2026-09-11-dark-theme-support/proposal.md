## Why

O frontend possui uma linguagem Swiss/financial editorial clara, mas não oferece uma alternativa escura consistente para uso noturno ou preferência do sistema. A mudança centraliza a aparência em tokens, preserva o tema claro como referência e adapta todas as superfícies, overlays e gráficos sem alterar dados, contratos ou layout.

## What Changes

- Adicionar temas claro e escuro com alternância acessível no shell da aplicação.
- Respeitar `prefers-color-scheme` quando não houver escolha manual e persistir a escolha localmente.
- Centralizar tokens semânticos de superfície, texto, borda, foco e estados financeiros.
- Integrar Angular Material, overlays, dialogs, menus, snackbars e gráficos ao tema ativo.
- Auditar rotas, forms, tabelas, estados, autenticação, analytics e dialogs nos dois temas.
- Evitar flash perceptível do tema incorreto e respeitar `prefers-reduced-motion`.

## Capabilities

### New Capabilities

- `dark-theme-support`: gerenciamento, tokens, alternância, persistência e cobertura visual dos temas claro/escuro.

### Modified Capabilities

Nenhuma. A mudança adiciona uma capacidade transversal sem alterar contratos ou regras financeiras existentes.

## Impact

Código Angular em `src/app` e estilos globais/design tokens; componentes Material, overlays, charts e testes de acessibilidade. Não altera backend, APIs, banco, autenticação, deploy ou regras financeiras.
