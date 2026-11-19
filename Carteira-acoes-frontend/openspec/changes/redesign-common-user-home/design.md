## Context

See `proposal.md` and the delta specs for motivation and observable behavior. The current common-user flow has four overlapping layers: `/usuario` as a link gateway, `/inicio` as portfolio cards, `/dashboard` as analytics, and `/minha-conta` as a separate account form. The shell also places account identity and logout beside page controls in the top toolbar. Existing services already expose the authenticated user, user-scoped portfolios, portfolio summaries and complete positions needed by the current charts; no new data contract is necessary.

Impeccable mode: **Operate**. The surface is used repeatedly to inspect financial state and choose the next task, so scanability, truthful state, predictable navigation and dense-but-readable data outrank marketing expression.

## Goals / Non-Goals

**Goals:**

- Replace the common-user route gateway with one canonical financial cockpit at `/inicio`.
- Make portfolio identity, currency-separated values, result, primary action and analytics visible without another navigation step.
- Give investment destinations and personal utilities separate, stable positions in the shell.
- Make `Minha conta` feel like the personal control area for identity, security and session/logout.
- Reuse current Angular Material, ApexCharts, formatters, mappers, services and semantic tokens.

**Non-Goals:**

- No new endpoint, DTO, financial calculation, exchange-rate conversion, activity feed or synthetic KPI.
- No administrator redesign, authorization change or duplicated role shell.
- No new chart library, state library, design system or generic dashboard-builder abstraction.
- No decorative hero, section numbering, eyebrow labels, equal-weight card grid, animation sequence or full-page color treatment.

## Decisions

### Use one canonical common-user route

After `ROLE_USER` login, navigate directly to `/inicio`. Configure `/dashboard` as a compatibility redirect to `/inicio` and `/usuario` as a compatibility redirect to `/minha-conta`. Update the product brand, access-denied recovery and account back-links to point to canonical destinations.

Keeping three separately authored entry pages was rejected because it creates duplicate concepts and hides the actual financial content behind route selection. Removing compatibility paths entirely was rejected because bookmarks and existing links may still use them.

### Consolidate the existing home and dashboard implementation

Move the useful dashboard loading and analytics behavior into the canonical home and remove the orphaned role-entry/dashboard presentation once callers and tests are updated. Reuse the existing portfolio summary endpoint, complete-position loading, analytics mappers, formatters and `ChartPanelComponent`. Keep portfolio selection synchronized across summary, charts and contextual links.

Creating another coordinator service was rejected because the state is local to one route and existing services already own API access.

### Commit to a financial cockpit composition

The established visual authority remains Swiss/financial editorial: neutral page and surface tokens, Helvetica-style sans, Yves Klein blue for current selection and primary action, hairline rules, left alignment and tabular numerals. “More visual” comes from data scale and chart composition, not decoration.

The first desktop viewport is structured as:

1. A compact heading row with `Início`, authenticated identity and portfolio selector.
2. A full-width financial ledger strip: assets, current value and result grouped by currency, with one `Comprar ação` primary action.
3. A dominant analytics stage where composition receives the larger region and result comparison the adjacent region.
4. Secondary portfolio navigation and unavailable explanations below the analytical content.

On mobile the same reading order stacks into selector, action, metrics and charts. Headings use a fixed product UI scale rather than fluid display typography. No data is hidden behind accordions or tabs in the initial implementation.

### Make charts carry interpretation, not decoration

Keep composition by stock and result by stock, but increase their usable visual area and keep the selected currency explicit in titles and surrounding labels. Use blue/neutral differentiation for composition and semantic positive/negative colors plus signed labels for results. Tooltips, legends and textual summaries must communicate the same values; empty/error states occupy the chart region without collapsing the layout.

Adding new chart types, animation-heavy transitions or consolidated multi-currency views was rejected because current contracts do not support a more authoritative story.

### Separate investment navigation from personal utilities

Primary side navigation contains `Início`, `Carteiras`, `Operações` and `Ações`. A pinned utility region at the end contains authenticated login, `Minha conta` and `Sair`. Remove `Dashboard` and the role-entry destination from common-user navigation. The top toolbar retains menu, product brand, route title and theme control; logout moves out of the row of page controls.

On compact viewports, the utility region remains within the same accessible overlay and closes under the existing compact-navigation behavior.

### Redesign Minha conta around three real responsibilities

Use one page heading followed by identity details, password security and session control. Remove `01 / 02` labels and the `/usuario` back-link. Keep the password contract and validation unchanged, improve field/error relationships where needed, and make `Sair` a clearly labeled secondary/destructive session action without using color alone.

### Preserve state topology while increasing visual clarity

Use the shared loading, empty and error components for page-level states. Summary and chart failures remain region-specific so valid portfolio context is not replaced by a full-page error. Portfolio changes clear stale analytics before loading the next selection. Theme and responsive behavior must be verified at 375px, 768px and desktop widths.

## Risks / Trade-offs

- [Consolidating routes can leave stale internal links] → Search all route callers and tests, preserve redirects and verify brand/back/access-denied destinations.
- [Loading complete positions can delay charts] → Render portfolio context and summary independently, keep the chart stage stable and expose chart-specific loading/error states.
- [A visually larger dashboard can overload mobile] → Preserve one reading sequence, stack regions, keep actions near the selected portfolio and avoid hidden tab panels.
- [Charts can imply consolidated currency values] → Group every metric and chart by explicit currency and never combine BRL/USD.
- [Moving logout can make it harder to find] → Keep it persistently visible in the personal utility region and repeat it in the session section of `Minha conta`.
- [Existing global chart/card styles may fight the new hierarchy] → Reuse semantic tokens but scope the cockpit layout to the canonical home; do not broadly restyle unrelated feature pages.

## Migration Plan

1. Update route and login/navigation tests for canonical `/inicio`, `/dashboard` compatibility and `/usuario` → `/minha-conta`.
2. Consolidate home/dashboard state and template using current services, mappers and shared chart/state components.
3. Restructure the common shell navigation and personal utility region, then update `Minha conta` and logout coverage.
4. Validate real, empty, loading, partial-error and multimoeda states in light/dark themes and target viewports.
5. Run focused/full tests, build, AXE, Impeccable detector and strict OpenSpec validation.
6. Roll back by restoring the former route components and shell links; no data migration or backend rollback is required.
