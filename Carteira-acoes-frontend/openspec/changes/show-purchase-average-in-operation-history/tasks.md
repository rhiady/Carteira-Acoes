## 1. Contextual purchase-average presentation

- [x] 1.1 Load the current portfolio positions through the existing service when loading operation history, preserving history data if the position lookup is unavailable.
- [x] 1.2 Associate each operation with its current position by asset identifier and render `Preço médio de compra` only for purchase entries, using the position currency and `—` for an unavailable value.

## 2. Verification

- [x] 2.1 Add focused history-page coverage for an authoritative purchase average, unavailable purchase average, and the absence of the field on sale entries.
- [x] 2.2 Run focused tests, TypeScript/build checks, OpenSpec validation and confirm that no API contract or client-side financial calculation changed.
