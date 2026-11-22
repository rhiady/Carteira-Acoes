## Context

See [proposal.md](proposal.md). Existing Angular Material screens already consume authoritative wallet, position and operation data; `dados-graficos-financeiros.txt` defines additional usable fields and explicitly excludes total portfolio evolution.

## Goals / Non-Goals

**Goals:** Replace the Swiss visual language with the confirmed Industrial system across login, wallet and operation surfaces; render published analytics per currency with accessible alternatives.

**Non-Goals:** Backend endpoints, client-side financial reconstruction, currency conversion, portfolio-evolution charts, and new chart dependencies.

## Decisions

### Use Industrial tokens with Material controls

Adopt warm-black surfaces, monospaced typography, flat borders and semantic green/red/amber states while preserving familiar Material interactions. A time-rail treatment will connect operation history and cash-flow views without changing route behavior.

### Load complete data only for complete analytics

Reuse complete position/operation pagination helpers and pure mappers. Charts group by currency; nullable fields produce unavailable states. `valorLiquido` is the sole cash-flow input.

## Risks / Trade-offs

- [Large operation histories] → page requests and render only the returned complete dataset needed for charts.
- [Missing market data] → omit affected series and state why.
- [Dark contrast regression] → run AXE, contrast and viewport checks.

## Migration Plan

1. Establish tokens and Material theme overrides.
2. Redesign target surfaces and add typed fields/mappers.
3. Add charts, textual alternatives and regression tests.
4. Validate responsive, dark/light, keyboard and API states; rollback by reverting UI and mapper changes.
