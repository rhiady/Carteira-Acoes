## 1. Ticker entry and existing purchase behavior

- [x] 1.1 Map `CompraPage` callers, current form controls, request construction and tests before editing; preserve identifier exclusivity, wallet ownership, confirmation and existing payload normalization.
- [x] 1.2 Normalize ticker letters to uppercase while the person types, retain submit-time normalization, and add focused coverage for typing, clearing and the existing ID-versus-ticker validation.

## 2. Purchase-form composition

- [x] 2.1 Refactor the purchase template and scoped styles into the designed Swiss sequence: asset reference, order, optional costs, estimated total and a single dominant confirmation action.
- [x] 2.2 Preserve real selected-asset context, loading, validation, error, pending and confirmation states; ensure keyboard focus, labels, live feedback and mobile stacking remain accessible.

## 3. Verification and visual audit

- [x] 3.1 Extend focused purchase-form tests for ticker normalization, identifier choices, estimate/confirmation behavior and responsive-safe rendering states.
- [x] 3.2 Run focused and full tests, production build, AXE checks, Impeccable detector/audit and rendered checks at 375px, tablet and desktop when browser tooling is available; validate the OpenSpec change and confirm no API, dependency or financial-rule change.
