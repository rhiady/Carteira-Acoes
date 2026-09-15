## Context

See `proposal.md` and the capability specs. A previous change established typed services, authoritative analytics, ApexCharts, reusable states, and the current Angular Material shell. This change must compose and restyle those surfaces without introducing a second data authority, new backend endpoints or a second chart library.

## Goals / Non-Goals

**Goals:**

- Establish a Swiss / financial editorial system with neutral surfaces, visible rules, a disciplined action accent and tabular financial numerals.
- Introduce an `Início` route while preserving existing lazy routes and contextual links.
- Make Dashboard, Carteiras, Detalhe da carteira and Operações the strongest investment-reading surfaces.
- Reuse existing chart, state, formatter, dialog and service boundaries.
- Keep loading, empty, error, retry, focus and reduced-motion behavior accessible and testable in runtime.

**Non-Goals:**

- No authentication, JWT, login, notification system, exchange-rate conversion or new financial calculation.
- No endpoint, DTO authority or backend rule changes.
- No replacement of ApexCharts, Angular Material behavior or the existing feature architecture.
- No broad visual rewrite of Corretoras or Usuários beyond shared shell/tokens and targeted consistency fixes.

## Decisions

### Preserve the existing service and chart boundaries

Pages continue to obtain data from existing services and pass authoritative values into existing shared components. A new `Início` page may compose existing user, portfolio and summary data, but it must show unavailable states when a resource does not provide enough information. Creating a dashboard endpoint or local financial aggregation would violate the backend-authority boundary.

### Use one Swiss visual system through shared CSS tokens

Global CSS owns canvas, surface, rule, text, semantic result, spacing and typography tokens. Material remains responsible for keyboard behavior, dialogs, overlays, controls and focus management. Alternatives considered were replacing Material controls or introducing a utility CSS framework; both would broaden the architecture and duplicate accessibility behavior.

### Prefer contextual surfaces over generic detail actions

Portfolio cards, position cards and operation records expose labels such as the ticker, company or portfolio name in their primary action. Simple forms and contextual detail can use existing dialogs when the content does not require a full route; resource-heavy or navigational flows remain pages. This avoids modalizing workflows that need deep links or page-level state.

## Legacy Screen Modernization

A refatoração SHALL incluir uma auditoria visual de todas as rotas existentes.

A aplicação não deverá possuir simultaneamente o novo shell/design system e páginas internas que ainda apresentem a aparência visual anterior.

Uma página SHALL ser considerada legada quando apresentar um ou mais dos seguintes problemas:

- conteúdo semelhante a HTML cru;
- labels e valores simplesmente empilhados;
- ausência de agrupamento visual;
- títulos desproporcionalmente grandes;
- uso excessivo de espaço vazio sem intenção;
- IDs ou informações técnicas como elementos principais;
- botões `Voltar` isolados;
- botões genéricos `Detalhes`;
- componentes Angular Material sem customização;
- ausência de hierarquia entre informações principais e secundárias;
- ausência de estados semânticos;
- inconsistência de espaçamento, tipografia ou cores;
- layout que não utiliza adequadamente a largura disponível.

Essas páginas SHALL ser modernizadas utilizando o mesmo design system definido nesta change.

A implementação SHALL decidir entre:

1. manter e redesenhar a página de detalhe; ou
2. substituir detalhes simples por dialog contextual.

A decisão deverá considerar quantidade de informação, necessidade de deep link, ações disponíveis e importância daquela entidade.

## Detail Page Experience

Páginas de detalhe SHALL apresentar informação estruturada e não apenas uma representação visual dos campos retornados pela API.

A hierarquia recomendada é:

1. identidade da entidade;
2. informações ou métricas principais;
3. status;
4. ações disponíveis;
5. grupos de informações relacionadas;
6. metadata;
7. identificadores técnicos.

Labels SHALL possuir aparência secundária em relação aos respectivos valores.

Informações relacionadas SHALL ser agrupadas semanticamente, incluindo informações cadastrais, contato, endereço, situação, dados financeiros e metadata quando aplicável.

Quando houver ação de retorno, SHOULD ser utilizada navegação contextual como breadcrumb, back-link ou ação integrada ao header.

Um botão `Voltar` isolado no final ou meio da página SHOULD ser evitado.

Detalhes simples de entidades administrativas SHOULD utilizar dialogs contextuais em viewport apenas para apresentar poucos campos.

Dialogs SHALL possuir:

- identidade clara;
- informações agrupadas;
- ação de fechamento evidente;
- foco controlado;
- navegação por teclado;
- responsividade;
- transição discreta;
- loading/error quando necessário.

Quando uma entidade possuir experiência rica, a página dedicada SHALL ser mantida. Carteiras e Ações, por exemplo, SHOULD continuar como páginas dedicadas por sua quantidade de informação, necessidade de deep link e ações contextuais.

## Broker Detail Experience

A experiência de detalhe de corretora SHALL ser redesenhada.

A implementação atual que apresenta razão social como título desproporcional e campos simplesmente empilhados SHALL ser substituída.

A identidade principal SHOULD priorizar o nome fantasia quando disponível.

Exemplo conceitual:

< Corretoras

XP Investimentos

XP INVESTIMENTOS CORRETORA DE CAMBIO,
TITULOS E VALORES MOBILIARIOS S/A

02.332.886/0001-04

[ Regular ]    [ CVM 3247 ]

Informações cadastrais
────────────────────────────────────

CNPJ
02.332.886/0001-04

Razão social
XP INVESTIMENTOS CORRETORA...

Registro CVM
3247

Contato
────────────────────────────────────

Telefone
(11) 3027-2237

E-mail
...

Endereço
────────────────────────────────────

...

### Keep chart presentation semantically redundant

Chart panels retain their current public inputs and provide visible textual summaries, explicit currency labels, legends/tooltips and state-specific content. Positive, negative, neutral and unavailable results use signed text and labels in addition to color. No fabricated chart point or client-side conversion is introduced.

### Validate the rendered application

Acceptance includes browser validation with the Angular proxy and backend running, at least one populated portfolio, a multimoeda case where available, operation history, narrow viewport behavior and loading/empty/error paths. Unit tests cover route composition, shared visual states and accessible names; runtime checks confirm ApexCharts hosts actually mount.

### Expanded completion boundary

The implementation must audit every existing route before archive. Financial routes retain dedicated pages when they require deep links, rich analytics or contextual actions; simple administrative details use contextual dialogs. This applies to operation, broker and user details while preserving dedicated pages for portfolios and stocks.

The visual system uses the Swiss / financial editorial anchor: neutral light canvas and surfaces, petrol/forest primary action tones, fine rules, tabular numerals, restrained positive/negative semantics, intentional whitespace and short transitions. Angular Material remains responsible for interaction behavior, overlays, controls and focus, but its default visual treatment is not the product identity.

The final runtime review must verify Início, Dashboard, portfolios, portfolio detail, stock list/detail, operations, buy/sell, brokers, users and dialogs at desktop, tablet and mobile widths. API contract failures are external dependencies and must not be hidden with mock data.

## Risks / Trade-offs

- [The temporary user context has no authentication contract] → Keep the context explicit and avoid implying a logged-in identity.
- [Some backend environments lack positions or history] → Keep truthful empty/unavailable states and record contract/runtime limitations instead of using fixtures in production.
- [Dialogs can hide navigational context] → Use them only for simple local forms/details and retain route pages for deep-linkable financial content.
- [A visual token change affects existing forms] → Reuse the established spacing and focus tokens, run the full test suite and verify narrow viewports before rollout.

## Migration Plan

1. Add the new route and shared visual tokens without changing API services.
2. Recompose the shell, Dashboard, portfolio cards/detail and operation history incrementally.
3. Apply targeted contextual actions and dialog refinements to existing components.
4. Add tests, run browser/runtime checks against the backend, then ship as a frontend-only build.
5. Roll back by deploying the previous frontend build; no persisted data migration is needed.
