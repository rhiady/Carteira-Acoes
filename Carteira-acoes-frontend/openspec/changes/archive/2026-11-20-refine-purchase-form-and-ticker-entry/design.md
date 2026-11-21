## Context

See `proposal.md` and the delta specs. `CompraPage` already owns the existing published buy request, reference validation, optional costs, estimated total and confirmation. The visual problem is sequencing: the asset choice, order and confirmation currently share a dense, largely uniform form surface.

Impeccable mode: **Operate**. A purchase is a focused financial task; truthful context, error prevention and scanability outweigh decoration.

## Goals / Non-Goals

**Goals:**

- Keep the existing Swiss / financial editorial system: neutral surfaces, Helvetica-style sans, Yves Klein blue action accent, hairline rules and tabular financial values.
- Make the buying decision read in a stable order: identify asset, define order, optionally add costs, review total, confirm.
- Make ticker entry feel like a market identifier by displaying typed letters in uppercase immediately.

**Non-Goals:**

- No change to the buy endpoint, payload, identifier exclusivity, confirmation dialog, calculation, asset lookup or wallet authorization.
- No new dashboard, price data, market search, autocomplete, animation sequence, visual dependency or alternate visual world.

## Decisions

### Retain the Swiss financial visual authority

The form remains in the established Swiss anchor rather than introducing a new aesthetic: white and neutral surfaces, one deliberate blue action color, left-aligned hierarchy and hairline structure. The differentiator is a ledger-like order review that gives the estimated total a stable terminal position before confirmation, not a decorative hero or card grid.

### Normalize ticker at the input boundary

Ticker text will be converted to uppercase as the person enters it and also remain normalized at submit time. CSS-only visual transformation was rejected because it can leave the form value different from what the person sees; normalization at the form control keeps visual and submitted values aligned.

### Make optional costs subordinate but discoverable

Costs remain collapsed by default and expand in place after the core order. This preserves the existing behavior while reducing first-pass cognitive load; hiding them in a dialog was rejected because it disconnects their effect from the visible total.

## Risks / Trade-offs

- [Uppercasing can surprise people entering non-ticker text] → limit it to the ticker field and keep all characters other than case intact.
- [A larger review region can make the form feel long on mobile] → use one vertical sequence and keep the primary action after the estimate.
- [A visual refactor can disturb validation or confirmation] → preserve existing controls, form state and request construction, with focused behavior tests and AXE checks.

## Migration Plan

1. Add focused tests for live ticker normalization, identifier exclusivity and purchase states.
2. Refactor the purchase template and scoped styles using existing tokens and Material controls.
3. Verify desktop and narrow layouts, themes, keyboard focus, error states and the existing confirmation flow.
4. Roll back by restoring the previous purchase template and styles; no API or data rollback is necessary.
