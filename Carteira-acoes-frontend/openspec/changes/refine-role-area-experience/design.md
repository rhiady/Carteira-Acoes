## Context

See `proposal.md` and the delta specs for motivation and observable behavior. The repository already has Angular standalone pages, lazy role routes, shared shell components, Angular Material, semantic light/dark tokens and a Swiss/financial editorial direction. The Impeccable critique of `/login` scored the baseline 27/40: low cognitive load and good foundations, with priority gaps around ambiguous labels, error focus/announcement, recovery expectations, silent transitions and excess identity ceremony. The same principles must be carried into the common-user and administrator entry surfaces.

## Goals / Non-Goals

**Goals:**

- Give `ROLE_USER` and `ROLE_ADMIN` distinct, calm entry surfaces that communicate purpose, authority and next action immediately.
- Reuse existing shell, route, service, model and shared-component boundaries before creating anything new.
- Establish one visual direction: Swiss — neutral white/`#F7F7F8` surfaces, Helvetica-style sans, Yves Klein blue `#002FA7` as the deliberate action accent, 1px hairlines, left alignment and tabular financial numerals.
- Use a role-specific entry rail as the differentiator: a compact authority/context marker paired with the active route and one primary action, without decorative dashboard fabrication.
- Treat Impeccable audit output as a release gate for accessibility, performance, theming, responsive behavior and implementation integrity.

**Non-Goals:**

- Do not change authentication, role guards, backend authorization, API endpoints, DTOs, financial calculations or user ownership rules.
- Do not add password recovery until a published backend/support contract exists.
- Do not create duplicate feature pages, a second design system, a new dependency or fabricated admin KPIs/activity feeds.
- Do not replace the established Swiss direction with gradients, warm paper, grain, rounded-card proliferation or decorative filler copy.

## Decisions

### Preserve the incumbent Swiss anchor

Keep the existing Swiss anchor rather than introducing a new visual world because the product is a dense financial tool and the current tokens already establish a coherent identity. Use current semantic tokens, Helvetica-style typography, blue action color, hairline rules and restrained surfaces. Avoid visual divergence between login, common user and admin areas.

### Use a compact role entry rail as the differentiator

The first viewport will use a compact, left-aligned context rail or header that shows the real area label (common investment or administration), authenticated display identity where available, and the active route. The role marker is information, not decoration; it must not become a fake step indicator like the criticized `01 / acesso` treatment.

An expansive split hero was rejected because it spends too much space on generic branding. A duplicated dashboard for each role was rejected because it invites fabricated metrics and duplicates feature behavior.

### Reuse existing route and shell boundaries

Refine existing `AdminPage`, `UserAccessPage`, `InicioPage`, `DashboardPage`, shared page-header/navigation/theme components and current route data. Add a shared primitive only when the existing components cannot express the contract without duplication. Keep lazy routes and role guard metadata unchanged.

### Keep content contract-driven

Common-user surfaces may show only authenticated user context and investment data already returned by existing services. Admin surfaces may show only published management resources and their real loading/empty/error states. When a summary is unavailable, render an explicit unavailable state rather than a placeholder metric, sample record or “recent activity” fiction.

### Make state and focus behavior explicit

Use the existing shared loading/error/empty patterns and preserve valid context during failures. For forms encountered in these flows, associate error text only while it exists, avoid multiple simultaneous live-region announcements for the same failure, and focus the first actionable error or a single summary. Preserve native disabled/loading semantics and safe retry behavior.

### Audit the rendered matrix before implementation is considered complete

The audit plan covers light/dark themes at 375px, 768px and desktop widths, keyboard traversal, focus visibility, accessible names, contrast where computed styles are available, reduced motion, no horizontal overflow and detector findings. The previous critique had no browser automation available; if that remains true, record source/test evidence and the skipped rendered checks explicitly instead of claiming visual verification.

## Risks / Trade-offs

- [The current role pages may already contain overlapping shell or navigation markup] → Read and reuse the incumbent components first; keep any new shared markup limited to the smallest repeated boundary.
- [Admin APIs may not publish summary data] → Make the entry state action/context-led and show unavailable states; never synthesize KPIs.
- [Role identity can become stale after logout or user change] → Continue deriving identity from `AuthService` signals and clear/reload affected context through existing session behavior.
- [Angular Material styles can override local state colors] → Use semantic tokens and scoped component selectors, then test light/dark default, hover, focus, active and disabled states.
- [Mobile navigation can hide context or overflow] → Use progressive stacking, a named menu control, active-route state and viewport checks; do not rely on a wide desktop grid collapsing by accident.
- [Visual audit tooling may be unavailable] → Run source detector, unit/AXE checks and document browser/overlay limitations in validation notes.

## Migration Plan

1. Capture current common/admin entry and shell behavior in focused tests before changing layout.
2. Refine shared shell/context primitives and role-specific page composition without changing route or service contracts.
3. Add state, focus, theme and responsive coverage for common and admin pages; run the Impeccable audit/detector and OpenSpec validation.
4. Verify no fabricated content, backend contract changes or authorization regressions were introduced.
5. Roll back by reverting the affected entry/shell templates, styles and tests; no data migration or backend rollback is needed.
