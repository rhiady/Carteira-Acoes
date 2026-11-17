import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { MyAccountPage } from './my-account.page';

describe('MyAccountPage', () => {
  const changePassword = vi.fn();

  beforeEach(() => {
    changePassword.mockReset();
    TestBed.configureTestingModule({ providers: [
      provideRouter([]),
      { provide: AuthService, useValue: { currentUser: () => ({ id: 7, login: 'ana@email.com', authorities: ['ROLE_USER'] }), changePassword } },
    ] });
  });

  it('shows only the authenticated identity and no user directory', async () => {
    const fixture: ComponentFixture<MyAccountPage> = TestBed.createComponent(MyAccountPage);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('ana@email.com');
    expect(fixture.nativeElement.textContent).toContain('7');
    expect(fixture.nativeElement.textContent).not.toContain('Usuários');
    expect(fixture.nativeElement.querySelector('input#new-password')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('input#password-confirmation')).toBeTruthy();
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });

  it('prevents mismatched passwords without calling the backend', () => {
    const fixture = TestBed.createComponent(MyAccountPage);
    fixture.detectChanges();
    const inputs = fixture.nativeElement.querySelectorAll('input');
    inputs[0].value = 'senha-valida';
    inputs[0].dispatchEvent(new Event('input'));
    inputs[1].value = 'outra-senha';
    inputs[1].dispatchEvent(new Event('input'));
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
    expect(changePassword).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('As senhas não coincidem');
  });

  it('submits the new password, clears fields and confirms success', () => {
    changePassword.mockReturnValue(of(void 0));
    const fixture = TestBed.createComponent(MyAccountPage);
    fixture.detectChanges();
    const inputs = fixture.nativeElement.querySelectorAll('input');
    for (const input of inputs) {
      input.value = 'senha-valida';
      input.dispatchEvent(new Event('input'));
    }
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
    expect(changePassword).toHaveBeenCalledWith({ password: 'senha-valida' });
    expect(inputs[0].value).toBe('');
    expect(inputs[1].value).toBe('');
    expect(fixture.nativeElement.textContent).toContain('Senha redefinida com sucesso');
  });

  it('shows backend errors and preserves the authenticated context', () => {
    changePassword.mockReturnValue(throwError(() => ({ message: 'Senha inválida.' })));
    const fixture = TestBed.createComponent(MyAccountPage);
    fixture.detectChanges();
    const inputs = fixture.nativeElement.querySelectorAll('input');
    for (const input of inputs) {
      input.value = 'senha-valida';
      input.dispatchEvent(new Event('input'));
    }
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Senha inválida.');
    expect(fixture.nativeElement.textContent).toContain('ana@email.com');
  });
});
