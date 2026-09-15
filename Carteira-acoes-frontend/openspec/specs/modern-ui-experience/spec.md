# modern-ui-experience Specification

## Purpose
Define the responsive, accessible and route-oriented interface conventions for the investment application, including its visual system, dashboard constraints and contextual feedback.

## Requirements

### Requirement: Tema visual financeiro consistente
The application SHALL present a calm financial-product visual language across all routes, using the provided light surface palette, petrol-blue primary and sidebar colors, restrained semantic positive/negative colors, consistent typography, spacing, borders, elevation, and interaction states. The theme MUST meet WCAG AA contrast requirements for text and essential controls and MUST NOT rely on color alone to convey operation type or state.

#### Scenario: Usuário navega entre funcionalidades
- **WHEN** the user moves between dashboard, listings, details, forms and operation pages
- **THEN** headings, primary actions, fields, surfaces and feedback states use the same visual language

#### Scenario: Controle recebe foco pelo teclado
- **WHEN** a keyboard user tabs to an interactive control
- **THEN** the control displays a visible focus indicator with sufficient contrast

### Requirement: Navegação adaptável e orientada à rota
The application SHALL provide a primary navigation shell with Dashboard, Carteiras, Ações, Operações, Corretoras, and Usuários; it MUST identify the active route. On desktop the sidebar SHALL remain fixed; on tablet and mobile it SHALL be available as an overlay that can be opened and closed by mouse and keyboard without obscuring access to page content. The toolbar SHALL show the current page title and only contextual controls.

#### Scenario: Rota ativa em tela ampla
- **WHEN** the user opens an application route on a desktop viewport
- **THEN** the primary navigation identifies the corresponding active destination

#### Scenario: Navegação em tela estreita
- **WHEN** the viewport is narrow
- **THEN** the user can open, use and close the primary navigation using mouse or keyboard while retaining access to the page content

### Requirement: Padrões claros para conteúdo e feedback
The application SHALL render data pages, forms and operational actions with consistent localized loading, empty, error and success feedback. Primary actions and irreversible actions MUST be visually distinguishable and understandable without relying only on color. For investment API interactions, it MUST retain already displayed content and valid unsent form values after a failed request, present the API message in context, and offer retry for safe repeated requests.

#### Scenario: Lista sem resultados
- **WHEN** a data collection has no records to display
- **THEN** the application presents an accessible empty state that explains the condition and, when applicable, offers the next relevant action

#### Scenario: Ação que falha
- **WHEN** a user action cannot be completed
- **THEN** the application presents an accessible error message that describes the outcome and allows the user to continue or retry where applicable

#### Scenario: Falha ao trocar uma página
- **WHEN** a paginated investment API request fails after content was shown
- **THEN** the previously visible content and page context remain available and the user can explicitly retry the request

### Requirement: Informação financeira legível em qualquer viewport
The application SHALL organize portfolio summaries, positions, tables and details so labels, monetary values, ticker symbols, and available actions remain readable and operable at desktop and mobile widths. It MUST format BRL and USD values according to their currencies and render UTC dates in the browser's local timezone. Tabular content that cannot fit horizontally MUST preserve access to all columns through an accessible responsive presentation.

#### Scenario: Visualização de tabela em dispositivo móvel
- **WHEN** the user views a data table on a narrow viewport
- **THEN** all information and row actions remain accessible without clipped content or a keyboard trap

#### Scenario: Visualização de métricas da carteira
- **WHEN** the dashboard or a portfolio detail is rendered
- **THEN** monetary values, their labels and their hierarchy can be distinguished at a glance and read by assistive technologies

### Requirement: Dashboard oferece resumo baseado em dados disponíveis
The application SHALL provide `/dashboard` as the initial destination and build its summary only from published resource endpoints. It MAY show counts and portfolio previews that can be derived from returned data, but MUST NOT show consolidated patrimônio or any financial value unless it can be calculated safely from available authoritative data. When no carteira is available in the selected temporary user context, it MUST explain that no carteira exists and offer the relevant creation action.

#### Scenario: Usuário sem carteira abre o dashboard
- **WHEN** the dashboard has no carteira to present
- **THEN** it displays an empty state inviting the user to create a first carteira

#### Scenario: Dados insuficientes para patrimônio
- **WHEN** the available responses cannot safely produce a consolidated patrimônio
- **THEN** the dashboard omits the patrimônio value instead of inventing a number

### Requirement: Ações financeiras aparecem no contexto
The application SHALL expose purchase and sale actions near the carteira, relevant position, or stock. A purchase form SHALL offer optional costs in a progressively disclosed area and an estimate clearly identified as non-official; a sale form SHALL identify the selected asset and available quantity. The interface MUST ask for confirmation before submitting a sale.

#### Scenario: Usuário inicia compra a partir da carteira
- **WHEN** the user views a carteira with an available purchase action
- **THEN** the interface opens a form contextualized to that carteira

#### Scenario: Usuário confirma uma venda
- **WHEN** the user submits a valid sale form
- **THEN** the interface asks for confirmation that identifies the quantity and ticker before the request is sent
