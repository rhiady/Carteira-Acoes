## Why

O frontend já oferece analytics, operações, carteiras, ações e gráficos autoritativos, mas a experiência ainda se aproxima de um painel administrativo. É necessário tornar a leitura financeira, a navegação e os fluxos cotidianos mais claros e distintos sem mover regras de negócio para o navegador.

## What Changes

- Introduzir uma tela principal `Início` e reorganizar a navegação existente.
- Aplicar a direção visual Swiss / financial editorial ao shell, páginas financeiras e áreas administrativas.
- Reforçar hierarquia de tickers, cotações, valores e resultados com uma paleta neutra e ações azuis.
- Melhorar Dashboard, Carteiras, detalhe da carteira, Operações, Ações, Compra e Venda sem alterar contratos financeiros.
- Transformar Operações em histórico/relatório com filtros, estados e contexto de carteira.
- Usar dialogs para fluxos simples de criação e detalhes contextuais quando isso preservar contexto e acessibilidade.
- Substituir ações genéricas de `Detalhes` por rótulos contextuais.
- Refinar skeletons, empty states, feedbacks, transições e microinterações discretas.
- Validar as telas em runtime com os payloads reais do backend.

## Capabilities

### New Capabilities

- `investment-product-navigation`: navegação principal, tela inicial e contextualização entre áreas de investimento.
- `investment-visual-language`: linguagem visual, hierarquia financeira, superfícies, microinterações e estados de interface.

### Modified Capabilities

- `investment-focused-experience`: alterar a apresentação observável do shell, Dashboard, carteiras, operações, ações e fluxos contextuais sem alterar autoridade financeira.
- `portfolio-analytics`: preservar e apresentar analytics existentes com hierarquia visual e estados mais claros.

## Impact

Afeta rotas e shell Angular, páginas de Dashboard, carteiras, operações e ações, dialogs compartilhados, componentes de estado, estilos globais e testes visuais/acessíveis. Não altera endpoints, DTOs autoritativos, cálculos financeiros, autenticação, câmbio ou a biblioteca de gráficos existente.

## Expanded Product Scope

The application SHALL be completed as an investment-first product experience across the full frontend surface, not only the primary Dashboard and portfolio routes. The expanded scope includes:

- a Swiss / financial editorial design system with calm financial palette, tabular financial typography, consistent spacing, subtle motion and Angular Material behavior preserved underneath the custom visual language;
- distinct Início and Dashboard roles, with Início serving as summary/navigation and Dashboard serving as analytics;
- structured portfolio, position, stock, operation, broker and user experiences with contextual actions and no technical identifiers as primary content;
- responsive and accessible creation dialogs, operation details, loading/empty/error states, reduced motion and keyboard/focus management;
- legacy-route auditing and modernization before archive, including broker and user details;
- runtime validation against the available API and explicit recording of backend incompatibilities.

The frontend SHALL remain subject to backend authority: it SHALL NOT invent endpoints, fields, financial data, historical series, currency conversions or authoritative calculations.
