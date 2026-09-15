# investment-visual-language Specification

## Purpose
Define a coherent Swiss / financial editorial visual language that gives financial information hierarchy without changing authoritative values or backend responsibilities.

## Requirements

### Requirement: Linguagem visual financeira consistente
The system SHALL use neutral light surfaces, fine rules, one primary action accent, tabular numerals and restrained semantic treatments for positive, negative, neutral and unavailable values. Financial values and tickers MUST be visually more prominent than identifiers and administrative metadata.

#### Scenario: Leitura de uma posição
- **WHEN** a populated position is displayed
- **THEN** its ticker, company, quotation and result are visually prioritized while quantity, currency and actions remain clearly associated.

#### Scenario: Resultado neutro ou indisponível
- **WHEN** a result is zero, null or unavailable
- **THEN** the interface uses explicit neutral/unavailable text and does not communicate the state through color alone.

### Requirement: Estados e microinterações preservam contexto
The system SHALL provide visible loading, empty, error and retry states for asynchronous financial surfaces and SHALL use short, reduced-motion-compatible transitions for hover, focus, submission and refresh feedback.

#### Scenario: Atualização em andamento
- **WHEN** a quote or financial resource is being refreshed
- **THEN** the triggering action communicates progress, prevents duplicate activation and preserves the current page context.

#### Scenario: Falha recuperável
- **WHEN** a safe read request fails
- **THEN** the affected surface explains the failure and exposes a retry action without displaying stale data as current.
