## ADDED Requirements

### Requirement: Financial surfaces visibly use authenticated context
The shell SHALL identify the authenticated person discreetly and Dashboard, wallets, operations, purchase, sale and analytics SHALL derive context from the current authenticated user. The visual language SHALL remain Swiss / financial editorial, calm, professional and consistent with the existing login.

#### Scenario: Authenticated shell
- **WHEN** a person is authenticated
- **THEN** the shell identifies that person without a complex profile menu and keeps `Sair` available

#### Scenario: User changes
- **WHEN** the authenticated identity changes
- **THEN** affected financial surfaces reload or clear their data and do not display stale values from the previous identity

### Requirement: Access and registration remain responsive and accessible
Login and registration SHALL remain operable by keyboard and assistive technology, preserve visible focus and associated validation messages, and remain usable at desktop, notebook, tablet and mobile widths without horizontal overflow.

#### Scenario: Registration on mobile
- **WHEN** the registration form is displayed in a narrow viewport
- **THEN** all four fields, password controls, feedback and the primary action remain reachable, legible and comfortable to tap
