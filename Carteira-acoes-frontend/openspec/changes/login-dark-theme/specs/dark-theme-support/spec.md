## ADDED Requirements

### Requirement: Login controls remain legible through theme changes

The system SHALL render `/login` with readable labels, inputs, validation feedback, password visibility control, secondary account-creation action, and primary `Entrar` action in both light and dark themes. Theme switching while the login route is open SHALL preserve the form's layout, values, enabled state, and interaction behavior.

#### Scenario: Person opens the login route in dark theme
- **WHEN** `/login` renders with the dark theme active
- **THEN** the form surface, labels, inputs, validation feedback, password visibility control, secondary action, and `Entrar` action remain visually distinguishable and readable

#### Scenario: Person switches theme during login
- **WHEN** a person changes between light and dark themes while `/login` is open
- **THEN** the login form remains visible and operable without turning the primary action transparent, invisible, or low contrast

### Requirement: Login primary action exposes distinguishable visual states

The `Entrar` action SHALL retain sufficient text and control contrast against its background in light and dark themes. Its default, hover, keyboard-focus, active, and disabled states SHALL be visually distinguishable, and disabled state SHALL remain readable while preventing duplicate submission. Angular Material theme styles SHALL NOT override this required login-specific appearance.

#### Scenario: Person interacts with the login primary action in dark theme
- **WHEN** the person hovers, focuses with a keyboard, or activates the enabled `Entrar` action in dark theme
- **THEN** each interaction state is visible, the action text remains readable, and keyboard focus is clearly indicated

#### Scenario: Login request is pending in either theme
- **WHEN** a login request is in progress in light or dark theme
- **THEN** the action displays its existing processing label, remains visibly disabled with readable text, and cannot initiate a duplicate request
