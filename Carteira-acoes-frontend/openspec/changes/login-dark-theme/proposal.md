## Why

The `/login` primary action can inherit Angular Material dark-theme styles that make its foreground or state treatment insufficiently visible. The sign-in screen is the entry point to the application, so its controls must remain reliably legible and operable while a person changes themes.

## What Changes

- Define explicit light- and dark-theme requirements for the `/login` form, including the `Entrar` button's default, hover, focus, active, and disabled states.
- Require login inputs, labels, validation feedback, password visibility control, and account-creation action to retain readable contrast in dark mode.
- Require the login-specific visual treatment to take precedence over conflicting Angular Material button tokens without changing login submission behavior or API integration.
- Add visual and automated coverage for rendering and theme switching on `/login`.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `dark-theme-support`: Strengthen the dark-theme requirements for the login route and its interactive authentication controls.

## Impact

- Affected UI: `src/app/features/login/login.page.ts`, global theme tokens and Angular Material style overrides in `src/styles.css`.
- Affected verification: login page and theme service tests, plus browser/a11y visual-state checks where supported.
- No backend endpoints, request payloads, routing destinations, authentication storage, or `/auth/login` integration change.
