## 1. Design foundation

- [x] 1.1 Audit target pages, shared tokens and chart components; implement the confirmed Industrial token/theme system without adding dependencies.
- [x] 1.2 Redesign login with preserved authentication, validation and accessibility behavior.

## 2. Wallet analytics

- [x] 2.1 Extend typed summary models and pure mappers for published investment/current and realized/unrealized fields.
- [x] 2.2 Redesign wallet list/detail and render per-currency comparisons with loading, empty, error and textual chart states.

## 3. Operation analytics

- [x] 3.1 Add complete paginated operation retrieval and pure per-currency cash-flow/volume mappers using `valorLiquido`.
- [x] 3.2 Redesign operation selection/history with interactive filters, time-rail and accessible chart summaries.

## 4. Verification

- [x] 4.1 Add focused mapper/page tests for all new data states, null values and currency separation.
- [x] 4.2 Run AXE-backed focused page checks, focused tests, build and OpenSpec validation; Impeccable browser detector requires an unavailable CDP session, so source/test validation was used as the fallback.
