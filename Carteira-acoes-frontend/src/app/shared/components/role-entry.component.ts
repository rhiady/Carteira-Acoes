import { Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

export interface RoleEntryLink {
  readonly label: string;
  readonly path: string;
  readonly primary?: boolean;
}

@Component({
  selector: 'app-role-entry',
  imports: [MatButtonModule, RouterLink],
  template: `
    <main class="role-entry" id="main-content">
      <div class="role-entry__rail" aria-label="Contexto da sessão">
        <span class="role-entry__rule" aria-hidden="true"></span>
        <p>{{ contextLabel() }}</p>
        <strong>{{ identity() }}</strong>
      </div>
      <section class="role-entry__content" aria-labelledby="role-entry-title">
        <p class="role-entry__index">{{ indexLabel() }}</p>
        <h1 id="role-entry-title">{{ title() }}</h1>
        <p class="role-entry__description">{{ description() }}</p>
        <nav class="role-entry__navigation" [attr.aria-label]="navigationLabel()">
          @for (link of links(); track link.path) {
            <a [routerLink]="link.path" [class.role-entry__link--primary]="link.primary">{{ link.label }}</a>
          }
          <button mat-stroked-button type="button" (click)="logout.emit()">Sair</button>
        </nav>
      </section>
    </main>
  `,
  styles: `
    :host { display: block; min-height: 100%; }
    .role-entry { align-items: stretch; background: var(--surface-base); border: 1px solid var(--border-subtle); display: grid; grid-template-columns: minmax(12rem, .7fr) minmax(18rem, 1.3fr); margin: clamp(1rem, 4vw, 3rem) auto; max-width: 64rem; min-height: min(34rem, calc(100dvh - 9rem)); }
    .role-entry__rail { border-right: 1px solid var(--border-subtle); display: flex; flex-direction: column; justify-content: space-between; padding: clamp(1.5rem, 4vw, 3rem); }
    .role-entry__rule { background: var(--color-blue); display: block; height: 3rem; width: 2px; }
    .role-entry__rail p, .role-entry__index { color: var(--color-muted); font-size: .72rem; letter-spacing: .1em; margin: 0; text-transform: uppercase; }
    .role-entry__rail strong { font-size: .85rem; overflow-wrap: anywhere; }
    .role-entry__content { display: flex; flex-direction: column; justify-content: center; padding: clamp(1.5rem, 5vw, 4rem); }
    .role-entry__content h1 { font-size: clamp(2rem, 5vw, 4rem); letter-spacing: -.06em; line-height: .95; margin: .75rem 0 .75rem; }
    .role-entry__description { color: var(--color-muted); line-height: 1.5; margin: 0; max-width: 38rem; }
    .role-entry__navigation { display: flex; flex-wrap: wrap; gap: .65rem; margin-top: 2rem; }
    .role-entry__navigation a, .role-entry__navigation button { align-items: center; background: var(--surface-raised); border: 1px solid var(--border-subtle); color: var(--text-primary); display: inline-flex; font: inherit; font-weight: 700; justify-content: center; min-height: 2.75rem; padding: .65rem .9rem; text-decoration: none; }
    .role-entry__navigation a:hover, .role-entry__navigation button:hover { background: var(--surface-hover); border-color: var(--color-blue); }
    .role-entry__navigation .role-entry__link--primary { background: var(--color-blue); border-color: var(--color-blue); color: #fff; }
    .role-entry__navigation .role-entry__link--primary:hover { background: var(--color-blue-hover); border-color: var(--color-blue-hover); }
    .role-entry__navigation a:focus-visible, .role-entry__navigation button:focus-visible { outline: 3px solid var(--focus-ring); outline-offset: 3px; }
    @media (max-width: 640px) {
      .role-entry { display: block; margin: 1rem; min-height: 0; }
      .role-entry__rail { border-bottom: 1px solid var(--border-subtle); border-right: 0; gap: 1rem; min-height: 9rem; padding: 1.5rem; }
      .role-entry__rule { height: 2rem; }
      .role-entry__content { padding: 1.5rem; }
      .role-entry__navigation { display: grid; grid-template-columns: 1fr; }
      .role-entry__navigation a, .role-entry__navigation button { width: 100%; }
    }
  `,
})
export class RoleEntryComponent {
  readonly contextLabel = input.required<string>();
  readonly identity = input.required<string>();
  readonly indexLabel = input.required<string>();
  readonly title = input.required<string>();
  readonly description = input.required<string>();
  readonly navigationLabel = input.required<string>();
  readonly links = input.required<readonly RoleEntryLink[]>();
  readonly logout = output<void>();
}
