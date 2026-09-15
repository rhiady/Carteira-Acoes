import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-theme-toggle',
  imports: [MatButtonModule, MatIconModule],
  template: `<button mat-stroked-button type="button" class="theme-toggle" (click)="theme.toggle()" [attr.aria-label]="theme.isDark() ? 'Ativar tema claro' : 'Ativar tema escuro'"><mat-icon aria-hidden="true">{{ theme.isDark() ? 'light_mode' : 'dark_mode' }}</mat-icon><span>{{ theme.isDark() ? 'Tema claro' : 'Tema escuro' }}</span></button>`,
  styles: `:host { display: inline-block; }.theme-toggle { align-items: center; border-color: var(--color-rule); color: var(--color-ink); display: inline-flex; gap: .4rem; min-height: 2.5rem; } .theme-toggle mat-icon { font-size: 1.05rem; height: 1.05rem; width: 1.05rem; }`,
})
export class ThemeToggleComponent {
  protected readonly theme = inject(ThemeService);
}
