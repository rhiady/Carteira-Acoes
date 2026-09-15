## 1. Login visual treatment

- [x] 1.1 Inspect the final Angular Material MDC button styles and add scoped `.login-submit` state tokens/selectors for default, hover, focus-visible, active, and disabled states in light and dark themes.
- [x] 1.2 Align login labels, inputs, password visibility control, validation feedback, and account-creation action with the existing semantic theme tokens, preserving the current layout and responsive breakpoints.
- [x] 1.3 Confirm the scoped login styles override conflicting Material dark-theme rules without modifying login controls, form submission, routing, or `/auth/login` integration.

## 2. Regression coverage

- [x] 2.1 Extend login component tests for light and dark rendering, theme switching with retained form state, and the readable disabled/loading `Entrando…` CTA behavior.
- [x] 2.2 Add deterministic assertions or browser-level checks for the CTA's default, hover, focus, active, and disabled visual-state contract, including its Material MDC foreground/background tokens.
- [x] 2.3 Run the relevant Angular/Vitest tests and AXE checks, and manually verify `/login` in both themes, including keyboard focus and theme switching while the page is open.
