## Context

See `proposal.md` for motivation and `specs/dark-theme-support/spec.md` for behavior. The application already uses the Swiss/financial editorial direction: Helvetica-style sans typography, restrained blue primary action, and hairline rules. Login is a standalone feature page that uses Angular Material's filled-button directive plus a local `.login-submit` class. Global dark-theme selectors also target Material filled buttons, creating a competing cascade that can override the intended foreground color.

Frontend-design direction: retain the established **Swiss** anchor to preserve the product's identity rather than introduce a new visual language for authentication. The signature move is a single solid blue CTA with explicit state progression against layered graphite surfaces: darkening for hover/active, a high-contrast outline for focus, and a muted but readable disabled treatment. Palette remains semantic tokens (`--color-blue`, `--color-blue-hover`, `--focus-ring`, layered surface tokens); typography remains the existing Helvetica stack; no added texture or invented content.

## Goals / Non-Goals

**Goals:**

- Give login-local styles enough specificity and Material MDC custom-property coverage to make the CTA deterministic in both themes.
- Keep every login control readable against the existing semantic surface and text tokens.
- Validate the state matrix without changing form controls, request lifecycle, navigation, or authentication data handling.

**Non-Goals:**

- Re-theme other routes, replace the global Material theme, or redesign the login layout.
- Change `/auth/login`, its payload, response handling, route destination, local storage behavior, or validation messages.
- Add a new component, API endpoint, dependency, or client-side financial logic.

## Decisions

### Scope visual treatment to the login CTA

Define login-specific semantic state values on `.login-submit` and set the Material filled-button container and label custom properties alongside the native background, border, and text properties. Include selectors for hover, active, focus-visible, and disabled states, with a component-local disabled treatment that retains readable label contrast.

This isolates the fix to the affected route and lets the existing global Material rules continue to serve other pages. Replacing or removing global dark-theme Material rules was considered, but rejected because it could change unrelated buttons and overlays.

### Use existing semantic tokens in both color modes

Continue using the project's light and graphite dark surface/text tokens for login inputs, labels, help/error text, and secondary navigation. Where an error color must remain legible on a dark surface, use the existing dark semantic danger token rather than the fixed light-theme error red.

Hard-coded per-theme colors were considered for every control, but rejected because they would duplicate the theme system and make future palette changes fragile.

### Preserve accessible interaction and form behavior

Keep the native form, reactive controls, loading signal, password visibility action, router link, and Material button directive intact. Apply a visible keyboard focus ring through existing focus tokens and keep disabled semantics native through the button's `disabled` binding.

Replacing the Material button with a plain button was considered, but rejected because it is unnecessary and risks changing established interaction behavior.

### Verify state and theme-switch regressions

Extend unit/component tests to cover both root theme datasets and the disabled/loading CTA. Add browser-level or style-computed verification where the test setup supports it for default, hover, focus, active, and disabled distinguishability, and retain AXE coverage with contrast evaluation enabled when CSS-computed colors are available.

Only snapshot testing was considered, but rejected because it would not reliably detect semantic state or accessible-name regressions.

## Risks / Trade-offs

- [Material MDC styles change after a dependency update] → Use documented MDC custom properties plus narrowly scoped fallback properties; test the computed login CTA states.
- [A disabled control becomes too faint because of inherited opacity] → Set the login-disabled surface and label intentionally, then verify contrast and disabled semantics together.
- [Global selectors regain precedence] → Keep login selectors local and test theme changes on the mounted login page.
- [Visual checks are environment-dependent] → Preserve deterministic unit assertions for classes/tokens and document any browser-only contrast check in the validation task.

## Migration Plan

1. Ship the scoped CSS and test changes together; no data migration or backend coordination is required.
2. Verify `/login` in light and dark themes, including switching theme in place and a pending login state.
3. Roll back by reverting the scoped login styles and associated tests; authentication behavior and persisted theme selection remain unaffected.
