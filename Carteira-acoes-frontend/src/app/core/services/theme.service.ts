import { DOCUMENT } from '@angular/common';
import { Service, computed, effect, inject, signal } from '@angular/core';
import { OverlayContainer } from '@angular/cdk/overlay';

export type Theme = 'light' | 'dark';
const STORAGE_KEY = 'carteira-acoes-theme';

@Service()
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly overlayContainer = inject(OverlayContainer);
  private readonly preference = signal<Theme>(this.initialTheme());
  readonly theme = computed(() => this.preference());
  readonly isDark = computed(() => this.theme() === 'dark');

  constructor() { effect(() => this.apply(this.preference())); }
  toggle() { this.setTheme(this.isDark() ? 'light' : 'dark'); }
  setTheme(theme: Theme) { this.preference.set(theme); this.document.defaultView?.localStorage.setItem(STORAGE_KEY, theme); this.apply(theme); }

  private initialTheme(): Theme {
    const stored = this.document.defaultView?.localStorage.getItem(STORAGE_KEY);
    if (stored === 'light' || stored === 'dark') return stored;
    const prefersDark = this.document.defaultView?.matchMedia?.('(prefers-color-scheme: dark)').matches ?? false;
    return prefersDark ? 'dark' : 'light';
  }
  private apply(theme: Theme) { this.document.documentElement.dataset['theme'] = theme; this.document.documentElement.style.colorScheme = theme; this.overlayContainer.getContainerElement().dataset['theme'] = theme; }
}
