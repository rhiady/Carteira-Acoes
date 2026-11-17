## Purpose

Define a role-aware frontend experience that separates administrative controls from the ordinary investment experience while relying on backend authorization as the final security boundary.

## ADDED Requirements

### Requirement: Administrative and common areas are separated by authority
The system SHALL expose distinct navigation areas for `ROLE_ADMIN` and `ROLE_USER`. Administrative navigation SHALL contain only administrative resources published by the backend, while the common area SHALL contain the authenticated user's investment flows and SHALL not expose administrative commands.

#### Scenario: Administrator enters the application
- **WHEN** an authenticated person has `ROLE_ADMIN`
- **THEN** the application directs the person to the administrative area and presents administrative navigation without exposing another person's private investment data by default

#### Scenario: Common user enters the application
- **WHEN** an authenticated person has `ROLE_USER`
- **THEN** the application directs the person to the common user area and does not render administrative navigation or administrative actions

#### Scenario: User attempts an administrative route
- **WHEN** a person without `ROLE_ADMIN` navigates to an administrative route
- **THEN** the application blocks the route, preserves the session, and presents an accessible access-denied state with a path to the person's permitted area

### Requirement: Role selection is derived from authenticated identity
The system SHALL derive the visible area and route authorization from the authorities returned by the authenticated-session contract. It SHALL not trust a role selected in the URL, local editable data, or a visual-only toggle, and backend authorization SHALL remain authoritative.

#### Scenario: Authority is missing or unsupported
- **WHEN** the authenticated identity has no supported application authority
- **THEN** the application SHALL not enter the administrative area and SHALL present a safe access-denied or session-recovery state without granting broader access

### Requirement: Areas remain accessible and consistent
Both areas SHALL provide a meaningful page title, visible focus, keyboard-reachable navigation, responsive layout, loading/error states, and text that explains access restrictions without relying only on color.

#### Scenario: Person navigates by keyboard on mobile
- **WHEN** a person uses keyboard navigation at a narrow viewport in either area
- **THEN** all available navigation and primary actions remain reachable in logical order without horizontal overflow or hidden focus
