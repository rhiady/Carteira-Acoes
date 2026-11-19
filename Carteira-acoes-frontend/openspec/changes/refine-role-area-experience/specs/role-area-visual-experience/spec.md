## Purpose

Define a focused, accessible and role-aware visual experience for authenticated common-user and administrator areas without changing authorization, financial contracts or backend capabilities.

## ADDED Requirements

### Requirement: Role entry surfaces communicate purpose and authority

The authenticated common-user and administrator entry surfaces SHALL identify the current area, the authenticated person's permitted context and the primary next action using real route and identity data. The common-user surface SHALL prioritize the person's own investment flows, while the administrator surface SHALL prioritize published management flows without presenting private investment metrics as administrative facts.

#### Scenario: Common user opens the permitted area
- **WHEN** a person with `ROLE_USER` enters the common area
- **THEN** the page identifies the investment context, shows only permitted navigation, and presents one visually dominant next action based on available real data

#### Scenario: Administrator opens the permitted area
- **WHEN** a person with `ROLE_ADMIN` enters the administrative area
- **THEN** the page identifies the management context, shows only published administrative navigation, and does not fabricate dashboard totals or another person's portfolio data

### Requirement: Role surfaces expose complete and actionable UI states

Both role entry surfaces SHALL provide understandable loading, empty, error, access-denied and success/continuation states where applicable. Each non-success state SHALL preserve valid context, explain the next safe action and remain operable by keyboard and assistive technology.

#### Scenario: Role data is loading or unavailable
- **WHEN** the initial role-specific data is pending or fails
- **THEN** the surface retains its heading and role context, communicates the state in text, prevents duplicate retry requests, and offers retry only when safe

#### Scenario: A permitted collection is empty
- **WHEN** a role-specific collection has no records
- **THEN** the surface explains that no records are available and provides the relevant existing creation or navigation action without fake records

### Requirement: Role-specific navigation remains separated and responsive

The common-user and administrator navigation SHALL preserve their existing route authorization boundaries, show an identifiable active route, expose accessible names for controls and remain usable at desktop, tablet and mobile widths without accidental horizontal overflow.

#### Scenario: Person navigates on mobile
- **WHEN** a person opens either role area at a narrow viewport
- **THEN** navigation can be opened, closed and traversed by keyboard or touch, the active route remains identifiable, and primary actions remain reachable without clipping

#### Scenario: Person tries an unavailable role route
- **WHEN** a person attempts to open a route outside their authority
- **THEN** the existing access-denied behavior remains intact, preserves the session, explains the restriction and offers a permitted destination
