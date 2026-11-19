## 1. Route and navigation consolidation

- [x] 1.1 Inventory every caller/test of `/usuario`, `/inicio`, `/dashboard`, `/minha-conta` and common-user navigation before changing route behavior.
- [x] 1.2 Redirect successful `ROLE_USER` login and the common-user brand destination to `/inicio`; keep `ROLE_ADMIN` behavior unchanged.
- [x] 1.3 Configure `/usuario` to redirect to `/minha-conta` and `/dashboard` to redirect to `/inicio`, preserving guards and compatibility.
- [x] 1.4 Update route tests for canonical and compatibility paths, anonymous access, role boundaries and access-denied recovery.

## 2. Common-user financial cockpit

- [x] 2.1 Consolidate the useful `InicioPage` and `DashboardPage` state/template behavior into the canonical `/inicio` page, reusing current services, formatters, analytics mappers and shared state/chart components.
- [x] 2.2 Present authenticated identity, portfolio selector, currency-separated asset/value/result summary and one contextual primary action in the first reading order.
- [x] 2.3 Synchronize portfolio selection across summary metrics, complete-position analytics, chart labels and contextual portfolio/purchase links; clear stale data while a new portfolio loads.
- [x] 2.4 Implement truthful no-session, no-portfolio, summary-unavailable, analytics-loading, analytics-empty and analytics-error states without replacing valid surrounding content.
- [x] 2.5 Remove the orphaned common-user role-entry/dashboard presentation and update imports/tests after confirming no remaining callers.

## 3. Visual hierarchy and charts

- [x] 3.1 Implement the Swiss financial cockpit layout from `design.md`: compact heading/selector, full-width metric ledger and dominant analytics stage without eyebrow labels, decorative numbering or repeated equal-weight cards.
- [x] 3.2 Refine composition and result charts using existing chart types and real values, with explicit currency context, larger usable regions, readable labels/tooltips and accessible textual alternatives.
- [x] 3.3 Preserve positive/negative meaning through signed text and semantic treatment without relying only on color; preserve unavailable values as `—` or explanatory states.
- [x] 3.4 Add responsive cockpit styles for desktop, 768px and 375px layouts with stable chart regions, usable selectors/actions and no page-level horizontal overflow.
- [x] 3.5 Verify theme tokens, focus, loading/disabled states and reduced-motion behavior in light and dark themes without broad unrelated style changes.

## 4. Personal utilities and Minha conta

- [x] 4.1 Simplify common-user primary navigation to `Início`, `Carteiras`, `Operações` and `Ações`; remove duplicate `Dashboard` and role-entry links.
- [x] 4.2 Add a distinct personal utility region to the existing shell containing authenticated login, `Minha conta` and keyboard-reachable `Sair`, including compact-overlay behavior.
- [x] 4.3 Redesign `Minha conta` into identity, security and session sections; remove decorative numbering and obsolete `/usuario` back navigation.
- [x] 4.4 Keep password-change behavior intact while improving focus, field-error relationships, pending/success/error feedback and mobile stacking.
- [x] 4.5 Add logout coverage from the shell and account session area, confirming complete session/financial-context cleanup and redirect to `/login`.

## 5. Verification and finish

- [x] 5.1 Extend home tests for one/multiple/no portfolios, BRL/USD separation, portfolio switching, partial summary failure and chart loading/empty/error states.
- [x] 5.2 Extend shell/account tests for navigation grouping, active route, legacy redirects, identity visibility, password states, logout and absence of administrator controls.
- [x] 5.3 Run AXE checks, keyboard-order checks and responsive rendered inspection at 375px, 768px and desktop where browser tooling is available; document any source-only fallback.
- [x] 5.4 Run the Impeccable detector on changed UI files and complete a finish review against the financial cockpit direction; resolve mechanical findings before completion.
- [x] 5.5 Run focused and full tests, TypeScript/build checks, `git diff --check` and strict OpenSpec validation; confirm no API, dependency, authorization or financial-calculation change was introduced.
