## Purpose

Provide authoritative stock quotation history and safe quote refresh flows so people can inspect price evolution without frontend-generated market data.

## ADDED Requirements

### Requirement: Histórico de cotação por ação
The system SHALL retrieve quotation history from `GET /acoes/{id}/historico` with default query parameters `page=0`, `size=100` and `sort=dataHora,asc`; it MAY include optional `inicio` and `fim` OffsetDateTime filters. The paginated response content SHALL use `id`, `acaoId`, `cotacao` and `dataHora`. The interface SHALL display a time-series chart whose X axis is date/time and whose Y axis is quotation value, using only returned points.

#### Scenario: Histórico disponível
- **WHEN** the history endpoint returns quotation points for an action
- **THEN** the stock detail displays those points in chronological time context and supports 7D, 30D, 90D and all-history views when the corresponding returned data exists.

#### Scenario: Histórico vazio
- **WHEN** the history endpoint returns no points
- **THEN** the interface explains that there is not enough history and invites the user to refresh the quotation without rendering fictitious points.

### Requirement: Atualização individual de cotação
The system SHALL send `POST /acoes/{id}/cotacao` for an individual refresh, prevent concurrent requests for the same action and, after success, replace the displayed quotation, timestamp and history/chart data with newly retrieved authoritative responses.

#### Scenario: Atualização individual bem-sucedida
- **WHEN** a user refreshes an action quotation successfully
- **THEN** the control indicates completion and the detail reloads the quotation history before presenting the updated chart.

### Requirement: Atualização global de cotações
The system SHALL expose an action to send bodyless `POST /acoes/atualizar-cotacoes` from the dashboard and stocks page. It SHALL present the returned `total`, `atualizadas`, `falhas` and `dataHora` as a refresh outcome, prevent duplicate global refresh requests while one is pending and refresh affected summaries after success.

#### Scenario: Atualização global pendente
- **WHEN** a global quote refresh is in progress
- **THEN** its trigger is disabled, announces progress and cannot create a duplicate request.

#### Scenario: Atualização global parcialmente concluída
- **WHEN** the global refresh response reports one or more failures
- **THEN** the interface reports the updated and failed counts with the returned completion time without presenting the operation as fully successful.

### Requirement: Estados de carregamento do gráfico
The system SHALL replace chart content with a chart-specific loading state while its source data is loading or being refreshed and SHALL provide a retry path for retry-safe failures.

#### Scenario: Histórico falha ao carregar
- **WHEN** quotation history retrieval fails
- **THEN** the chart area presents an accessible error and retry action without retaining obsolete chart points as current data.
