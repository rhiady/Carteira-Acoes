## ADDED Requirements

### Requirement: Login belongs to the existing financial visual language
The login route SHALL use the application's Swiss / financial editorial visual language, with a calm professional composition, consistent typography, spacing, focus treatment and Angular Material controls. It SHALL remain readable and operable at desktop, notebook, tablet and mobile widths without horizontal overflow.

#### Scenario: Login is viewed on mobile
- **WHEN** the login route is displayed in a narrow viewport
- **THEN** the form uses nearly the available width, fields and button retain comfortable touch targets, and no content is clipped or horizontally scrolled

#### Scenario: Login is used with assistive technology
- **WHEN** the person navigates the form by keyboard or assistive technology
- **THEN** labels, focus, validation feedback, password visibility control and loading/error states have understandable accessible names and relationships
