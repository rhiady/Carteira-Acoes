## Context

See [proposal.md](proposal.md) and the delta specs. The project already has Angular standalone lazy routes, Angular Material, signal-based page state, typed domain models, `CarteiraService`/`OperacaoService`, analytics mappers and a shared `ChartPanelComponent`. The API is the financial source of truth: wallet summary, positions and operations expose market and currency fields, but the browser must not convert currencies or fabricate an evolution endpoint.

The reference images show a shared Swiss/financial editorial shell with a desktop-biased wallet form, an administrative entry that lacks investment shortcuts, and charts that need clearer scale, labels and interaction. `PRODUCT.md` confirms this is an Operate surface: investors need to complete financial tasks and inspect real data quickly; administrators need safe navigation to published resources.

## Goals / Non-Goals

**Goals:**

- Make wallet creation one visibly redesigned, responsive and accessible flow across the page and the administrative dialog, while keeping ownership and authorization rules at the existing service boundary.
- Make administrative navigation expose the already-published actions, wallets and operations routes without leaking administrative links to common users.
- Add a single market lens (`Todos`, `Brasil`, `EUA`) shared by wallet and operation analytics, preserving BRL/USD separation.
- Improve chart readability and interaction through accurate series, localized formatting, keyboard/hover feedback, textual alternatives and explicit loading/empty/error states.
- Preserve the existing Swiss visual world: white/neutral surfaces, Yves Klein blue, hairline rules, left alignment, Helvetica Neue/system sans and tabular numerals. The differentiator is a persistent market lens in each analytics header that carries the selected market and currency into every metric and chart.
- Use existing Angular Material controls and chart dependency; keep changes local to current feature/shared component boundaries and verify the rendered result at desktop and mobile sizes.

**Non-Goals:**

- No backend endpoint, payload, role, permission or financial calculation is invented.
- No browser-side BRL/USD conversion, consolidated multi-currency total or historical portfolio-evolution chart is added.
- No new feature architecture, state library, chart library or generic abstraction layer is introduced.
- No redesign of unrelated authentication, registration, broker, stock-maintenance or purchase/sale business rules.

## Decisions

### Reuse the existing wallet form and authenticated context; remove ownership selection

Keep `/carteiras/nova` as the common route and repair its role-sensitive context at the existing `AuthService`/`CarteiraService` boundary. The page form and `CarteiraDialogComponent` both contain only wallet name and required broker selection; neither flow contains a user selector or loads the user directory. Before calling `POST /api/carteiras`, the frontend adds the required `usuarioId` from `AuthService.currentUser().id` for both `ROLE_USER` and `ROLE_ADMIN`. Administrative permission changes where the flow is entered, not who owns the new wallet; the backend remains authoritative and resolves the authenticated user from the JWT.

This is preferable to duplicating admin and user forms, because duplication is the visible source of drift in the reference images and would make responsive/accessibility fixes diverge again. A role-specific wrapper is unnecessary unless the existing route guard or backend contract proves that the entry context cannot be preserved. The shared visual treatment must be applied to both surfaces: compact editorial header, clearly grouped fields, explicit helper/error text, stable action row and a one-column mobile layout. The UI must never expose or allow editing the hidden ownership field.

### Keep market filtering pure and local to authoritative records

Represent the selected market as a small signal state and derive filtered positions/operations and chart inputs from it with pure mappers. `Todos` means records from both markets, not a currency conversion. `BRASIL` remains BRL and `EUA` remains USD for the current published model; if a future response violates that correspondence, the UI shows the returned currency label rather than guessing.

The filter belongs above the analytics content and is shared by all charts on the page. It must reset or remain stable only according to the existing route context; no global store is needed. Existing `compositionItems`, result/comparison mappers and operation analytics mappers remain the calculation boundary, with tests covering empty, null, negative and partial-page cases.

### Make ChartPanel the single presentation boundary

Extend the existing shared chart panel configuration rather than creating page-specific chart shells. Each chart receives a title, unit/currency context, accessible textual summary, series and an explicit empty/error/loading state. ApexCharts tooltips, data labels and responsive options are configured from the same formatted values used in the textual summary, avoiding divergent numbers.

Charts remain standard web chart components inside the existing Angular Material layout. On wide screens compatible charts may share a row; below the established content threshold they stack, and long ticker labels wrap or move into tooltips/legends without page-level horizontal scrolling. Reduced-motion settings disable nonessential animations.

### Administrative navigation stays route-oriented

Add `Ações`, `Carteiras` and `Operações` to the existing administrative link collection and/or shared shell using their published paths. Keep active-route semantics and existing role guards. Do not add a new admin dashboard or duplicate collection data; the administrative entry is navigation, while the destination pages remain the source of their own content.

### Visual direction: Swiss financial editorial, Operate mode

The visual direction follows the established Swiss anchor rather than introducing a new world: neutral white/near-white surfaces, one deliberate Yves Klein blue action/selection color, Helvetica Neue/system sans, tabular numerals and 1px rules. The market lens is the signature interaction: a compact, keyboard-operable segmented control with an adjacent currency context, repeated consistently in the analytics header and reflected in chart titles. It adds product-specific clarity without turning the dashboard into a themed instrument panel.

Before implementation, the four screenshots in `Melhorar/` remain the visual baseline: use their Swiss editorial hierarchy, left rail, blue action color, hairline separators, strong page title and market/currency labeling, while correcting the visible form width, empty spacing, inconsistent admin entry and chart scale issues. The implementation must retain Angular Material affordances, visible focus, theme tokens and the current dark-theme semantic adaptation; it must not introduce rounded decorative cards, fabricated labels or visual ornament that competes with financial data.

## Risks / Trade-offs

- [The common-user creation error is caused by a backend contract/authorization mismatch rather than the page] → verify the exact request and authenticated identity with service/route tests; keep the dependency explicit and show the backend message without weakening guards.
- [An admin shortcut points to a route whose data is not authorized for admins] → use only routes already present and protected, test the navigation with an admin session, and preserve the backend as final authority.
- [Filtering a paginated response could misrepresent a global chart] → load the existing complete-position/complete-history path where already available; mark partial datasets and suppress global rankings/aggregations when completeness is not established.
- [Chart tooltip values diverge from text or formatter output] → derive both from the same mapper/formatter and add assertion-based mapper/component tests for sign, null and currency cases.
- [A denser responsive chart layout remains unusable at narrow widths] → validate at 375px, tablet and desktop with keyboard and AXE checks; stack before labels or controls overflow.
- [The existing mixed light/dark token layers regress after styling changes] → reuse semantic tokens, test both themes, and treat contrast/focus failures as release blockers.

## Migration Plan

1. Add/update unit, route and component tests for ownership, admin links, market filtering, numeric formatting, partial data and chart states.
2. Refactor the existing wallet creation context and responsive composition without changing the published endpoint or request shape.
3. Add the administrative links and shared market-lens control, then wire filtered mappers into wallet and operation analytics.
4. Update shared chart configuration/presentation and verify light/dark theme, keyboard focus, reduced motion, 375px/mobile, tablet and desktop renders.
5. Run build, relevant tests, AXE and the OpenSpec validation suite. If a backend dependency is missing, ship no fallback metric and leave the documented unavailable state.
6. Roll back by reverting the page, mapper, navigation and chart-panel changes; no persisted-data or backend migration is required.
