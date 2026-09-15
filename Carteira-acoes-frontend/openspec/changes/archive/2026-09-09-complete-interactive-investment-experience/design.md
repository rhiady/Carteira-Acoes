## Context

The current Angular application uses standalone lazy routes, Angular Material primitives, centralized financial formatters/mappers, signals for local state, and a Swiss-inspired global stylesheet. The route inventory is: `/login`, `/cadastro`, `/inicio`, `/dashboard`, `/acoes`, `/acoes/:id`, `/usuarios`, `/usuarios/novo`, `/usuarios/:id`, `/corretoras`, `/corretoras/nova`, `/corretoras/buscar`, `/corretoras/:id`, `/carteiras`, `/carteiras/nova`, `/carteiras/:id`, `/carteiras/:id/comprar`, `/carteiras/:id/vender`, `/carteiras/:id/operacoes`, `/operacoes` and `/operacoes/:id`, plus the wildcard redirect. Existing shared components cover charts, financial display, page headers, confirmation, registration dialogs, user edit and UI states.

The frontend must consult existing models and services before changing presentation, preserve API payloads and pagination, and avoid changing backend or deploy files. The visual direction selected from `frontend-design` is the Swiss anchor: neutral white/near-neutral surfaces, one deliberate blue or red accent, sans typography, visible hairline rules, left-aligned editorial hierarchy and numerals treated as composition. The differentiator is a consistent “decision rail” on financial pages: a compact page context and explicit primary action remain discoverable while the content hierarchy expands or collapses responsively.

## Goals / Non-Goals

**Goals:**

- Establish shared visual tokens and page composition patterns without replacing the existing Angular architecture.
- Audit and refine every real route, shared state, dialog, form, table and financial empty/loading/error state.
- Make wallet, asset, position, purchase and sale context persist visibly through each flow.
- Present backend-authoritative values and only mathematically valid presentation metrics.
- Improve desktop, notebook, tablet and mobile behavior, keyboard access, focus, contrast and touch targets.
- Add tests around behavior, context, loading, errors, derived display metrics and route-level regressions.

**Non-Goals:**

- No backend, database, migration, Docker, Nginx or deploy changes.
- No new financial endpoint, unsupported metric, benchmark, projection, exchange-rate source or fabricated demo data.
- No global state library or broad architectural rewrite.
- No removal of legitimate Users or Brokers functionality without a separate requirement.

## Decisions

### 1. Audit first, then refactor by route groups

Create a route checklist from `app.routes.ts` and map each route to its component, service calls, models, shared components, loading/error/empty states and primary action. Refactor in groups: shell/authentication, overview/wallets, assets/history, trading/operations, analytics and supporting administration. This makes the “no legacy route left behind” criterion observable.

Alternative considered: redesigning only Dashboard and Wallet detail. Rejected because the requested experience is end-to-end and inconsistent supporting routes would undermine it.

### 2. Use shared presentation primitives and tokens

Extend the existing global CSS tokens and shared components for page headers, metric groups, state panels, action rails, responsive grids, tables and dialogs. Keep page-specific styles limited to genuine composition differences. Use Angular Material for accessible overlays, buttons, icons and tooltips while styling it into the Swiss system.

Alternative considered: introducing a new component library or stateful design-system package. Rejected because it expands dependencies and conflicts with the current architecture.

### 3. Keep data transformations at the boundary

Use existing services, mappers and formatters as the boundary for API data. Add pure presentation helpers only for defined calculations such as variation against average price and complete-set position weight. Backend fields remain authoritative; partial paginated data cannot be labelled as portfolio totals.

Alternative considered: calculating all dashboard and detail metrics in templates. Rejected due to duplicated logic, accidental recomputation and risk of replacing authoritative values.

### 4. Preserve context through route inputs and authenticated identity

Purchase and sale pages continue to consume route/query context and authenticated user identity. Wallet and asset context will be displayed in the page heading/summary and remain selected when provided. Ownership errors remain backend-authoritative and map to friendly UI states. No duplicate user selector or alternate authentication state is introduced.

Alternative considered: adding a new global trading store. Rejected because the current signals/services are sufficient and the change explicitly avoids unnecessary state infrastructure.

### 5. Make states explicit and testable

Every backend-dependent route will expose loading, success/data, empty and error states appropriate to the data. Important mutations will expose idle, processing, success and failure behavior, disable duplicate submission and preserve safe form context on failure. Tests will focus on service requests, context, state transitions and pure financial presentation helpers rather than brittle CSS snapshots.

### 6. Responsive composition over desktop shrinking

Use a bounded content container appropriate to each page family, CSS grid/flex reflow, scroll-contained tables and single-column form/operation layouts at mobile widths. Primary actions remain explicit and reachable. The mobile representation may change from table to cards where information hierarchy requires it, but must preserve all essential data and actions.

## Risks / Trade-offs

- [Risk] The route count and existing page diversity make a complete visual pass large. → Mitigation: maintain a route checklist and mark each route only after reviewing title, hierarchy, CTA, states, responsiveness and accessibility.
- [Risk] Some desired analytics may lack complete backend operands. → Mitigation: omit unsupported metrics and document data limitations rather than fabricate values.
- [Risk] Existing Material and custom CSS may conflict during token consolidation. → Mitigation: change shared tokens/primitives first, then migrate pages incrementally and run the full test/build suite after each route group.
- [Risk] Paginated data may be mistaken for global data. → Mitigation: label page-scoped values explicitly and only compute composition/rankings from a complete supported set.
- [Risk] Runtime validation may depend on backend data and environment availability. → Mitigation: separate automated frontend tests from runtime smoke evidence and record blocked external checks without marking them successful.

## Migration Plan

1. Capture the route/component/service inventory and baseline tests/build.
2. Consolidate tokens and shared primitives without changing API contracts.
3. Refine route groups in the order defined above, adding focused tests with each group.
4. Run unit tests, production build, accessibility checks and runtime route/flow validation.
5. If rollback is needed, revert the change's frontend commits; no backend or data migration is involved.

## Open Questions

- Which currently running backend dataset is stable enough for the final end-to-end visual walkthrough? This affects validation evidence only, not the frontend contract or design.
