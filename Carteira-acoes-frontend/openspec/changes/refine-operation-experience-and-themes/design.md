## Context

See [proposal.md](proposal.md) and the delta specs for the observable contract. `ThemeService` already persists an explicit `light` or `dark` preference, applies it to the document and CDK overlay container, and exposes it through signals. The current global stylesheet contains successive token blocks: a later default block forces dark/Industrial tokens into `:root`, so the light preference does not provide a coherent light surface. It also has broad Angular Material overrides that can compete with feature-level styles.

The current operation pages already consume typed `Operacao`, `PosicaoAcao`, `CompraRequest` and `VendaRequest` through `OperacaoService`; the published contract is sufficient. Local estimates exist only to support order review and must remain explicitly non-authoritative.

Impeccable assessment: this is an **Operate** surface. The principal issue is not missing information but weak hierarchy: context, input, estimate, and confirmation compete in similarly treated regions. The redesign must improve scanability without changing financial semantics.

Frontend-design direction: retain the project's **Industrial** anchor because it is already the incumbent financial system: layered near-black surfaces in dark mode, monospaced tabular financial values, one acid-lime action signal, flat one-pixel rules, and square controls. Its counterpart is a deliberate light Industrial mode—near-white neutral surfaces, charcoal text, the same grid/rule structure and signal color—rather than an unrelated visual language. The visible differentiator is a persistent order-review rail on wide screens that collapses into the final sequential review section on narrow screens; it keeps the exact decision total adjacent to its context.

## Goals / Non-Goals

**Goals:**

- Establish one semantic token layer whose values vary only by the active theme and feed application, Material, and overlay surfaces.
- Preserve the existing signal-based theme service and operation data/service boundaries.
- Make the buy, sell, history, detail, loading, error, and confirmation states easily scannable across desktop and mobile.
- Keep the UI truthful: separate currencies, backend-returned values, and locally estimated review values remain visibly distinct.

**Non-Goals:**

- Add endpoints, fields, calculations, currency conversion, financial rules, or an alternative backend contract.
- Replace Angular Material, introduce another component library, or reorganize feature folders.
- Redesign unrelated investment, administrative, authentication, or analytics routes beyond the shared semantic tokens they consume.

## Decisions

### Normalize the existing theme system around semantic tokens

Define light-mode semantic values in `:root` and dark-mode overrides under `[data-theme='dark']`; remove conflicting late declarations and route all existing aliases through those tokens. Keep `ThemeService` and its `localStorage` preference behavior intact, including updating the overlay container and `color-scheme`.

This is safer than changing each feature to branch on theme because it preserves the current single theme boundary, avoids duplicated view logic, and keeps Material overlays synchronized. A second theme service or component-scoped hard-coded palettes was rejected because both would create divergent theme state.

### Scope Material styling to semantic component states

Map MDC custom properties and fallback selectors for buttons, fields, dialogs, menus, selects, snackbars, tables, and focus states to the token layer. Use narrowly scoped selectors for exceptional feature controls only after the global semantic values are correct.

This replaces broad `!important`-led overrides with predictable token ownership. Replacing Angular Material controls with native controls was rejected because it would regress established accessibility and consistency.

### Recompose operations from existing page regions and shared primitives

Retain current routes, reactive forms, models, service calls and shared display components. Reorder/style their existing regions into: asset/wallet context; order fields; optional costs; review; primary confirmation. On wide screens, make the review column sticky only while enough viewport width exists; on narrow screens, present the same semantic DOM order vertically. Reuse the existing operation chip and detail dialog rather than creating a parallel operation component hierarchy.

This focuses the correction on the user's task while preserving working validation and request behavior. A new wizard route or a client-side operation view model was rejected because it would add state and scope without a missing contract.

### Distinguish estimates from confirmed financial data with labels and hierarchy

Continue calculating an order estimate only from currently entered valid form values. Mark it as estimated and explain that the API confirms operation values. Render historical/detail values from `Operacao` as returned, use `—` when optional authoritative fields are unavailable, and preserve currency separation.

This conforms to the API-source-of-truth constraint. Adding projected result, conversion, or synthetic defaults was rejected because those values are not supplied by the backend.

### Verify the two-theme interaction matrix

Add focused unit/component tests for preference persistence, overlay theme propagation, forms retaining state on a theme toggle, and rendered operation hierarchy. Run AXE checks for purchase, sale, history/detail and relevant overlays in both themes, then inspect desktop and mobile layouts plus default, hover, focus-visible, active, disabled, pending, error and unavailable states.

Computed visual checks complement unit tests; neither alone detects both token cascade errors and interaction regressions.

## Risks / Trade-offs

- [A legacy selector relies on a removed alias or hard-coded color] → Search styles before consolidation, retain aliases temporarily where needed, and validate affected routes in both themes.
- [Material MDC specificity changes across upgrades] → Use documented custom properties first, keep exception selectors scoped, and cover the operation controls and overlays with tests.
- [Sticky review crowds small notebooks] → Enable it only at a measured wide breakpoint and use normal sequential flow below it.
- [An estimate is mistaken for a settled result] → Use an explicit estimate label and API-confirmation copy; never reuse that treatment for API-returned history data.
- [Theme changes obscure a focus target] → Use tokenized focus rings with AA-verified contrast and keep native focus/navigation behavior.

## Migration Plan

1. Inventory existing token consumers and replace conflicting global token blocks with the consolidated light baseline and dark overrides.
2. Map Material/overlay styles to the semantic values, retaining the existing `ThemeService` and toggle component.
3. Refine existing operation page layouts and shared display styles without changing services, models, routes or request payloads.
4. Run focused tests, build/type checks, AXE checks and visual inspection in both themes at desktop and mobile widths.
5. Roll back by reverting the token/style and operation-layout commits; no persisted data or backend migration is involved.
