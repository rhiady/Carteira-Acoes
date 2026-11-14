## Context

See `proposal.md` for motivation and the delta specs for the observable contract. The current detail route already calls `CarteiraService.summary`, loads all positions for analytics, renders authoritative per-position totals, and uses the application's Swiss / financial editorial tokens. The main gap is the first-read summary: it has current value and result by currency but no visible invested-total hierarchy.

Frontend-design direction: retain the existing **Swiss** anchor because this is a dense financial surface and the product already uses neutral surfaces, Helvetica-style sans typography, fine rules and a restrained blue accent. The differentiator is a compact “capital → current value → result” reading rail per currency, with the invested figure set as the first financial number and hairline separators making currency boundaries explicit.

## Goals / Non-Goals

**Goals:**

- Reuse the existing `CarteiraDetailPage`, `CarteiraService.allPositions`, formatters, chart panels and UI-state components.
- Make total invested visible per currency only when the complete position set supports it.
- Improve hierarchy, responsive stacking, focus treatment and accessible textual states on the existing route.
- Add focused component coverage for complete, incomplete, mult-currency and empty states.

**Non-Goals:**

- No backend endpoint, DTO, database, route, operation flow or quote-refresh contract changes.
- No currency conversion, client-side reconstruction of authoritative financial fields, portfolio-wide sum from a paginated page, or new analytics metric.
- No new design system, dependency, generic summary component or broad route redesign.

## Decisions

### Derive the invested summary only from the complete analytics set

Use the existing full-position loading path as the source for a presentation-only total grouped by `moeda`, summing only authoritative `valorInvestidoAtual` values when the collection is complete and every required value is present. Keep the field unavailable when the collection is partial or has insufficient values.

Using `positions()` was rejected because it is page-sized and would violate the existing rule against global totals from partial data. Adding a backend summary field was rejected because the current request is a frontend improvement and the endpoint contract does not publish it.

### Keep summary sections currency-separated

Render one summary rail for each currency returned by the portfolio summary or complete positions. Do not create a consolidated total, BRL conversion, exchange-rate estimate or cross-currency ranking.

### Preserve the current Swiss visual system

Use neutral light/dark semantic surfaces already defined in global styles, one blue primary action, 1px rules, left-aligned typography and tabular financial numerals. Use existing positive/negative/unavailable classes and textual descriptions; do not add texture, gradients, rounded-card proliferation or fabricated labels.

### Validate behavior at the existing component boundary

Extend `carteira-detail.page.spec.ts` for total-invested rendering, currency separation, unavailable data, empty position state and mobile-safe structure where the current test environment permits. Keep tests at the page boundary instead of introducing a new abstraction solely for the summary.

## Risks / Trade-offs

- [The all-positions request is slower on large portfolios] → Reuse the existing analytics request and show the summary as unavailable/loading until the complete set arrives; do not add another request.
- [A backend response omits one invested value] → Treat the currency subtotal as unavailable rather than mixing complete and incomplete values.
- [Visual changes affect existing chart/position density] → Keep existing components and verify desktop, 768px tablet and 375px mobile layouts plus keyboard focus.
- [Material or theme styles reduce contrast] → Reuse current semantic tokens and retain explicit text labels for result direction and unavailable values.

## Migration Plan

1. Implement the summary and layout changes in the existing detail route and extend its tests.
2. Validate light/dark themes, BRL/USD separation, loading/error/empty states, responsive widths and AXE checks.
3. Roll back by reverting the route template/style/test changes; no data or backend migration is required.
