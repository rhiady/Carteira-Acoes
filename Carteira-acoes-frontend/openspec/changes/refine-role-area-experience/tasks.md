## 1. Baseline and shared entry structure

- [x] 1.1 Read and map the current `AdminPage`, `UserAccessPage`, `InicioPage`, `DashboardPage`, shared page-header/navigation/theme components and their callers before editing; record the smallest reusable boundary.
- [x] 1.2 Add or update focused page tests for the current common-user and administrator entry states, including authenticated identity, role-specific navigation and no fabricated summary data.
- [x] 1.3 Define the shared role-entry visual contract using existing Swiss tokens, Angular Material controls, semantic states and the compact role/context rail described in `design.md`.

## 2. Common-user experience

- [x] 2.1 Refine the common-user entry composition to prioritize the authenticated investment context, available real portfolios/actions and one dominant primary action.
- [x] 2.2 Remove or compress generic branding/index content that does not help the user's current decision while preserving product identity and existing route behavior.
- [x] 2.3 Implement loading, empty, error, retry and continuation states that retain valid user context, use backend messages when available and never invent portfolio metrics.
- [x] 2.4 Ensure common-user navigation exposes only permitted routes, identifies the active route and remains keyboard/touch usable on mobile.

## 3. Administrator experience

- [x] 3.1 Refine the administrator entry composition to prioritize published management flows, authenticated admin context and one dominant primary action.
- [x] 3.2 Keep administrative content distinct from private investment data and render explicit unavailable states wherever no summary contract exists.
- [x] 3.3 Implement loading, empty, error, access-denied and retry states with safe continuation and no fabricated KPIs, activity feeds or records.
- [x] 3.4 Ensure administrator navigation exposes only published administrative routes, identifies the active route and remains keyboard/touch usable on mobile.

## 4. Accessibility, themes and responsive behavior

- [x] 4.1 Correct form and state feedback relationships in affected surfaces: conditional `aria-describedby`, one clear live announcement per failure, visible focus and focus placement on the first actionable error.
- [x] 4.2 Validate light and dark themes for text, controls, disabled/loading states, active navigation and focus using semantic tokens and no accidental Material cascade regressions.
- [x] 4.3 Validate 375px, 768px and desktop layouts for clipping, horizontal overflow, touch target size, logical tab order and menu open/close behavior.
- [x] 4.4 Confirm reduced-motion behavior preserves state communication and does not introduce blocking animation or focus disruption.

## 5. Verification and audit

- [x] 5.1 Extend component/route tests for both role areas, including identity changes, logout/context clearing, role boundaries, loading/empty/error/access-denied states and continuation actions.
- [x] 5.2 Run AXE or the project's equivalent accessibility checks with contrast evaluation enabled where supported, and fix WCAG AA violations found in the affected surfaces.
- [x] 5.3 Run Impeccable detector/audit for the affected source and record accessibility, performance, theming, responsive and implementation-integrity results; if browser tooling is unavailable, document the source-only fallback.
- [x] 5.4 Run focused tests, TypeScript/build checks and `openspec validate --type change refine-role-area-experience`; confirm no API, dependency, financial-rule or authorization changes were introduced.
