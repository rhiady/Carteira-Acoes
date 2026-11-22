import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../core/services/auth.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { CorretoraService } from '../../core/services/corretora.service';
import { CarteiraFormPage } from './carteira-form.page';

describe('CarteiraFormPage', () => {
  const create = vi.fn(() => of({ id: 10 }));
  const auth = { currentUser: vi.fn() };
  const brokers = vi.fn(() => of({ content: [{ id: 4, nomeFantasia: 'Corretora Teste' }], totalElements: 1, totalPages: 1, size: 100, number: 0, first: true, last: true, numberOfElements: 1, empty: false }));

  beforeEach(() => {
    create.mockClear(); auth.currentUser.mockReset(); auth.currentUser.mockReturnValue({ id: 7, login: 'investidor@carteira.local', authorities: ['ROLE_USER'] });
    brokers.mockClear();
    TestBed.configureTestingModule({ providers: [
      provideRouter([{ path: 'carteiras', children: [] }]),
      { provide: AuthService, useValue: auth },
      { provide: CarteiraService, useValue: { create } },
      { provide: CorretoraService, useValue: { list: brokers } },
    ] });
  });

  it('uses the authenticated identity and published broker selection for a common user', () => {
    const fixture: ComponentFixture<CarteiraFormPage> = TestBed.createComponent(CarteiraFormPage);
    const page = fixture.componentInstance as unknown as { form: { setValue(value: { nome: string; corretoraId: number }): void }; submit(): void };
    page.form.setValue({ nome: 'Longo prazo', corretoraId: 4 });
    page.submit();

    expect(create).toHaveBeenCalledWith({ nome: 'Longo prazo', corretoraId: 4, usuarioId: 7 });
    expect(fixture.nativeElement.querySelector('#usuario')).toBeNull();
  });

  it('blocks creation without an authenticated identity', () => {
    auth.currentUser.mockReturnValue(null);
    const fixture = TestBed.createComponent(CarteiraFormPage);
    const page = fixture.componentInstance as unknown as { form: { setValue(value: { nome: string; corretoraId: number }): void; }; submit(): void };
    page.form.setValue({ nome: 'Longo prazo', corretoraId: 4 });
    page.submit();
    fixture.detectChanges();

    expect(create).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Sua sessão não está disponível');
  });

  it('keeps the form available when the wallet request fails', () => {
    create.mockReturnValueOnce(throwError(() => ({ message: 'Carteira duplicada.' })));
    const fixture = TestBed.createComponent(CarteiraFormPage);
    const page = fixture.componentInstance as unknown as { form: { setValue(value: { nome: string; corretoraId: number }): void; }; submit(): void };
    page.form.setValue({ nome: 'Longo prazo', corretoraId: 4 });
    page.submit();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Carteira duplicada.');
    expect(fixture.nativeElement.querySelector('#nome')).not.toBeNull();
  });
});
