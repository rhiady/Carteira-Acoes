import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { describe, expect, it, beforeEach } from 'vitest';
import axe from 'axe-core';
import { ThemeService } from '../../core/services/theme.service';
import { LoginPage } from './login.page';

describe('LoginPage', () => {
  let fixture: ComponentFixture<LoginPage>;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ imports: [LoginPage], providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])] });
    fixture = TestBed.createComponent(LoginPage);
    fixture.detectChanges();
  });

  it('starts with only email and hidden password fields', () => {
    expect(fixture.nativeElement.querySelectorAll('input')).toHaveLength(2);
    expect((fixture.nativeElement.querySelector('#login-password') as HTMLInputElement).type).toBe('password');
  });

  it('shows understandable validation and toggles password visibility', () => {
    (fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement).click();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Informe seu email.');
    expect(fixture.nativeElement.textContent).toContain('Informe sua senha.');

    (fixture.nativeElement.querySelector('.password-toggle') as HTMLButtonElement).click();
    fixture.detectChanges();
    expect((fixture.nativeElement.querySelector('#login-password') as HTMLInputElement).type).toBe('text');
  });

  it('preserves the filled form while switching themes on login', () => {
    const email = fixture.nativeElement.querySelector('#login-email') as HTMLInputElement;
    const password = fixture.nativeElement.querySelector('#login-password') as HTMLInputElement;
    const themeToggle = fixture.nativeElement.querySelector('.auth-theme-toggle button') as HTMLButtonElement;

    email.value = 'pessoa@exemplo.com';
    email.dispatchEvent(new Event('input'));
    password.value = 'senha-segura';
    password.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    themeToggle.click();
    fixture.detectChanges();

    expect(document.documentElement.dataset['theme']).toBe('dark');
    expect(email.value).toBe('pessoa@exemplo.com');
    expect(password.value).toBe('senha-segura');
    expect((fixture.nativeElement.querySelector('.login-submit') as HTMLButtonElement).disabled).toBe(false);
  });

  it('keeps the loading CTA readable and disabled in both themes', () => {
    const theme = TestBed.inject(ThemeService);
    const component = fixture.componentInstance as LoginPage & { loading: { set(value: boolean): void } };
    component.loading.set(true);

    for (const selectedTheme of ['light', 'dark'] as const) {
      theme.setTheme(selectedTheme);
      fixture.detectChanges();
      const submit = fixture.nativeElement.querySelector('.login-submit') as HTMLButtonElement;

      expect(submit.disabled).toBe(true);
      expect(submit.textContent?.trim()).toBe('Entrando…');
      expect(submit.classList).toContain('login-submit');
    }
  });

  it('declares deterministic Material and native CTA states', () => {
    const styles = Array.from(document.head.querySelectorAll('style'))
      .map((style) => style.textContent)
      .join('\n')
      .replace(/\s+/g, '');

    expect(styles).toContain('--mat-button-filled-container-color:var(--login-submit-background)');
    expect(styles).toContain('--mat-button-filled-label-text-color:var(--login-submit-foreground)');
    expect(styles).toMatch(/\.login-submit[^:]*:not\(:disabled\):hover/);
    expect(styles).toMatch(/\.login-submit[^:]*:not\(:disabled\):active/);
    expect(styles).toMatch(/\.login-submit[^:]*:focus-visible/);
    expect(styles).toMatch(/\.login-submit[^:]*:disabled/);
  });

  it('has no automated accessibility violations', async () => {
    const results = await axe.run(fixture.nativeElement as HTMLElement, { rules: { 'color-contrast': { enabled: false } } });
    expect(results.violations).toEqual([]);
  });
});
