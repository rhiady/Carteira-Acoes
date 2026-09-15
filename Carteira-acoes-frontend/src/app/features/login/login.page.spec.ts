import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { describe, expect, it, beforeEach } from 'vitest';
import axe from 'axe-core';
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

  it('has no automated accessibility violations', async () => {
    const results = await axe.run(fixture.nativeElement as HTMLElement, { rules: { 'color-contrast': { enabled: false } } });
    expect(results.violations).toEqual([]);
  });
});
