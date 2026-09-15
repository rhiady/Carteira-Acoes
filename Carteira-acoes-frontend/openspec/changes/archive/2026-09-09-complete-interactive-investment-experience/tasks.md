## 1. Discovery and baseline

- [x] 1.1 Re-read and apply the `frontend-design` skill, recording the Swiss anchor, palette, typography, grid rules and differentiator in the implementation notes.
- [x] 1.2 Inventory every route from `app.routes.ts` and map route, component, services, models, dialogs, loading, empty, error and primary action.
- [x] 1.3 Audit existing models, services, mappers, formatters, shared components and API contracts; document supported financial fields and unsupported metrics.
- [x] 1.4 Run baseline tests and production build; record existing warnings without modifying backend or deploy.

## 2. Shared visual system and shell

- [x] 2.1 Consolidate global tokens for container widths, spacing, typography, borders, surfaces, radii, controls, semantic colors and breakpoints.
- [x] 2.2 Refine shared page headers, metric displays, UI states, tables, action groups and dialogs to use the tokens and accessible focus states.
- [x] 2.3 Refine shell navigation so current location, Dashboard, Carteiras, Ações, Operações, Analytics, user context and logout are understandable without competing with content.
- [x] 2.4 Add route-level runtime checklist covering every real route, including wildcard and not-found/error states.

## 3. Authentication and navigation

- [x] 3.1 Refine Login hierarchy, validation, password visibility, loading, invalid credentials, keyboard submission and registration link.
- [x] 3.2 Refine Cadastro hierarchy, validation, duplicate email, success/failure feedback and return to Login.
- [x] 3.3 Validate authentication pages at notebook, tablet and mobile widths with no overflow and accessible touch targets.
- [x] 3.4 Add or update tests for auth loading, errors, accessible controls, navigation and non-persistence of secrets.

## 4. Overview and wallets

- [x] 4.1 Refactor Início and Dashboard around real available metrics, authenticated context, clear hierarchy and explicit quick actions.
- [x] 4.2 Distinguish total, realized and unrealized results wherever the backend provides those fields; omit unsupported metrics.
- [x] 4.3 Refactor Carteiras list/cards with useful values when available, open/create actions, user-scoped empty state and responsive behavior.
- [x] 4.4 Refine wallet creation with contextual ownership, broker selection, validation, feedback, loading and success navigation.
- [x] 4.5 Refactor wallet detail summary, actions, positions, composition and ranking only when complete supported data exists.
- [x] 4.6 Refine position cards/rows with authoritative values, currency, null sale-average handling, variation and realized/unrealized explanations.
- [x] 4.7 Add contextual empty states, including `Criar primeira carteira` and `Comprar primeira ação` where applicable.
- [x] 4.8 Add/update overview, wallet and position tests for context, states, derived metrics and partial-data safety.

## 5. Assets and history

- [x] 5.1 Refactor Ações list with discoverable ticker/company/market/currency information and functional search/filter only where supported.
- [x] 5.2 Refine asset detail with real quote information, context-aware position data and explicit `Comprar {ticker}` action.
- [x] 5.3 Refine quote history presentation using only backend history, including loading, empty, error and optional average-price reference when valid.
- [x] 5.4 Refine Corretoras list, search, detail and creation routes to match the shared system and preserve feedback states.
- [x] 5.5 Review Usuários routes for legitimate administrative behavior, remove only redundant personal-flow selectors and preserve existing contracts.
- [x] 5.6 Add/update asset, history, broker and user route tests without fabricated data.

## 6. Purchase and sale

- [x] 6.1 Improve purchase entry points from Dashboard, wallet, position, asset detail and empty state without indiscriminate CTA duplication.
- [x] 6.2 Refine purchase form validation, wallet/asset context, allowed asset identifier modes and responsive layout.
- [x] 6.3 Add purchase review summary with wallet, asset, quantity, unit price and estimated total using safe presentation math.
- [x] 6.4 Implement explicit purchase confirmation, processing state, duplicate-submission prevention, success feedback and friendly retryable errors.
- [x] 6.5 Improve sale entry points from positions and wallet detail, preserving wallet and asset context.
- [x] 6.6 Refine sale form with available quantity, partial/total sale validation, review summary and responsive layout.
- [x] 6.7 Implement explicit sale confirmation, processing state, duplicate-submission prevention, success feedback and friendly retryable errors.
- [x] 6.8 Add/update purchase and sale tests for context, payload contract, summaries, validation, loading, errors and partial/total quantities.

## 7. Operations and analytics

- [x] 7.1 Refactor Operações and wallet history to differentiate purchase/sale, preserve pagination and present supported filters without page-scope mislabeling.
- [x] 7.2 Refactor Analytics using real wallet/position data, currency separation, safe rankings, comparisons and clear empty/loading/error states.
- [x] 7.3 Improve charts and textual summaries for keyboard access, tooltip clarity, reduced motion and responsive layout.
- [x] 7.4 Add/update operations and analytics tests for pagination, ownership, currency separation and unsupported-metric omission.

## 8. Accessibility and responsive validation

- [x] 8.1 Validate semantic labels, focus order, keyboard submit, touch targets, contrast, dialog behavior and status/error announcements across route groups.
- [x] 8.2 Validate all relevant routes at desktop large, notebook, tablet and mobile widths, including Compra and Venda end to end.
- [x] 8.3 Fix page-level horizontal overflow, table/dialog overflow and mobile action reachability found during validation.
- [x] 8.4 Run AXE or equivalent rendered accessibility audit and fix actionable findings.

## 9. Runtime flows and final verification

- [x] 9.1 Run the new-user flow: Login, Cadastro, return to Login, authenticated empty state, first wallet and first purchase entry.
- [x] 9.2 Run the authenticated flow: Dashboard, wallet, purchase, position, asset detail, history, operations, partial sale and updated result.
- [x] 9.3 Run logout and user-switch validation to ensure no financial state from the previous user remains.
- [x] 9.4 Complete the route checklist and record any backend/environment blockers separately from frontend results.
- [x] 9.5 Run the complete test suite and production build; confirm backend and deploy files are unchanged.
