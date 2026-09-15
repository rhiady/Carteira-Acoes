import { TestBed } from '@angular/core/testing';
import { OverlayContainer } from '@angular/cdk/overlay';
import { beforeEach, describe, expect, it } from 'vitest';
import { ThemeService } from './theme.service';

describe('ThemeService', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [ThemeService] });
  });

  it('persists a manual theme and applies it to the document and overlays', () => {
    const service = TestBed.inject(ThemeService);
    const overlay = TestBed.inject(OverlayContainer).getContainerElement();

    service.setTheme('dark');

    expect(service.theme()).toBe('dark');
    expect(localStorage.getItem('carteira-acoes-theme')).toBe('dark');
    expect(document.documentElement.dataset['theme']).toBe('dark');
    expect(overlay.dataset['theme']).toBe('dark');
  });

  it('toggles between light and dark without changing layout state', () => {
    const service = TestBed.inject(ThemeService);
    service.setTheme('light');
    service.toggle();
    expect(service.isDark()).toBe(true);
    service.toggle();
    expect(service.isDark()).toBe(false);
  });
});
