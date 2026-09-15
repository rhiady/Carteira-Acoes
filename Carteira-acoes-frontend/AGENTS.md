# Frontend project instructions

Este é um projeto Angular existente.

## Regra principal

Antes de criar, mover, remover ou alterar qualquer arquivo:

1. Leia `openspec/config.yaml`.
2. Leia `openspec/specs/**`.
3. Leia todas as changes ativas em `openspec/changes/**`.
4. Identifique se a solicitação atual pertence a uma change já existente.
5. Leia os arquivos Angular relacionados antes de propor novos componentes.
6. Preserve a arquitetura atual do projeto.

## Estrutura existente

O projeto utiliza:

- `src/app/core` para infraestrutura, models, services, mappers e formatters.
- `src/app/features` para páginas organizadas por domínio.
- `src/app/shared/components` para componentes reutilizáveis.

Não crie uma nova arquitetura como `pages/`, `components/`, `dialogs/`,
`services/` dentro de cada feature apenas para seguir um template.
Primeiro reutilize a organização existente.

## OpenSpec

OpenSpec é a fonte de verdade das mudanças planejadas.

Antes de implementar uma funcionalidade:

- verifique changes existentes;
- atualize uma change existente quando ela já representar a mesma iniciativa;
- crie uma nova change somente quando a mudança possuir escopo realmente distinto;
- não edite diretamente specs consolidadas durante uma change não arquivada;
- preserve requisitos que não estejam sendo modificados.

O fluxo esperado é:

proposal -> specs/design -> tasks -> implementação -> validação -> archive.

## Frontend Design

Utilize a frontend-design skill para decisões visuais.

A skill deve:

- respeitar o OpenSpec;
- respeitar a estrutura existente;
- ler os componentes atuais antes de criar novos;
- manter Angular Material;
- reutilizar componentes compartilhados quando adequado;
- manter consistência global de spacing, tipografia e tokens.

A skill não pode:

- inventar endpoints;
- inventar campos de API;
- implementar regras financeiras localmente quando pertencem ao backend;
- recriar componentes que já existem sem justificar a substituição;
- reorganizar o projeto inteiro sem que a change peça isso.

## Backend contract

Antes de integrar uma nova funcionalidade:

1. leia os models existentes em `core/models`;
2. leia os services existentes em `core/services`;
3. confira o contrato OpenAPI/backend fornecido ao projeto;
4. não presuma endpoints ainda não implementados.

Se uma funcionalidade depender de uma mudança futura do backend,
mantenha essa dependência explícita no OpenSpec.




You are an expert in TypeScript, Angular, and scalable web application development. You write functional, maintainable, performant, and accessible code following Angular and TypeScript best practices.

## TypeScript Best Practices

- Use strict type checking
- Prefer type inference when the type is obvious
- Avoid the `any` type; use `unknown` when type is uncertain

## Angular Best Practices

- Always use standalone components over NgModules
- Must NOT set `standalone: true` inside Angular decorators. It's the default in Angular v20+.
- Do NOT set `changeDetection: ChangeDetectionStrategy.OnPush` explicitly. `OnPush` is the default in Angular v22+.
- Use signals for state management
- Implement lazy loading for feature routes
- Do NOT use the `@HostBinding` and `@HostListener` decorators. Put host bindings inside the `host` object of the `@Component` or `@Directive` decorator instead
- Use `NgOptimizedImage` for all static images.
  - `NgOptimizedImage` does not work for inline base64 images.

## Accessibility Requirements

- It MUST pass all AXE checks.
- It MUST follow all WCAG AA minimums, including focus management, color contrast, and ARIA attributes.

### Components

- Keep components small and focused on a single responsibility
- Use `input()` and `output()` functions instead of decorators
- Use `model()` for two-way bound properties with `[(prop)]` syntax instead of pairing `input()` with `output()`
- Use `computed()` for derived state
- Use `linkedSignal()` for state derived from multiple reactive sources that must stay synchronized
- Prefer inline templates for small components
- Prefer Signal Forms (`@angular/forms/signals`) for new forms. They are stable in Angular v22+ and provide signal-based state, type-safe field access, and schema-based validation
- When not using Signal Forms, prefer Reactive forms instead of Template-driven ones
- Do NOT use `ngClass`, use `class` bindings instead
- Do NOT use `ngStyle`, use `style` bindings instead
- When using external templates/styles, use paths relative to the component TS file.

## State Management

- Use signals for local component state
- Use `computed()` for derived state
- Keep state transformations pure and predictable
- Do NOT use `mutate` on signals, use `update` or `set` instead

## Templates

- Keep templates simple and avoid complex logic
- Use native control flow (`@if`, `@for`, `@switch`) instead of `*ngIf`, `*ngFor`, `*ngSwitch`
- Use the async pipe to handle observables
- Do not assume globals like (`new Date()`) are available.

## Services

- Design services around a single responsibility
- Use the `providedIn: 'root'` option for singleton services
- Prefer the `@Service` decorator over `@Injectable({providedIn: 'root'})` for new singleton services (Angular v22+)
- Use the `inject()` function instead of constructor injection
