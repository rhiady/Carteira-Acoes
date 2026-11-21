import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../core/services/auth.service';
import { CadastroPage } from './cadastro.page';

describe('CadastroPage', () => {
  const auth = { register: vi.fn() };
  let fixture: ComponentFixture<CadastroPage>;

  beforeEach(() => {
    auth.register.mockReset();
    TestBed.configureTestingModule({ imports: [CadastroPage], providers: [provideRouter([{ path: 'login', component: CadastroPage }]), { provide: AuthService, useValue: auth }] });
    fixture = TestBed.createComponent(CadastroPage);
    fixture.detectChanges();
  });

  it('blocks mismatched passwords and sends no request', () => {
    const page = fixture.componentInstance as unknown as { form: { setValue(value: object): void }; submit(): void };
    page.form.setValue({ nome: 'Ana', email: 'ana@email.com', senha: 'senha123', confirmarSenha: 'outra123' });
    page.submit();
    expect(auth.register).not.toHaveBeenCalled();
  });

  it('submits only the backend registration fields', () => {
    auth.register.mockReturnValue(of({ id: 4, nome: 'Ana', email: 'ana@email.com' }));
    const page = fixture.componentInstance as unknown as { form: { setValue(value: object): void }; submit(): void };
    page.form.setValue({ nome: 'Ana', email: 'ana@email.com', senha: 'senha123', confirmarSenha: 'senha123' });
    page.submit();
    expect(auth.register).toHaveBeenCalledWith({ nome: 'Ana', email: 'ana@email.com', senha: 'senha123' });
  });

  it('shows a duplicate email message', () => {
    auth.register.mockReturnValue(throwError(() => ({ status: 409 })));
    const page = fixture.componentInstance as unknown as { form: { setValue(value: object): void }; submit(): void };
    page.form.setValue({ nome: 'Ana', email: 'ana@email.com', senha: 'senha123', confirmarSenha: 'senha123' });
    page.submit(); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Já existe uma conta cadastrada');
  });
});
