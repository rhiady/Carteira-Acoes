import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CarteiraService } from '../../core/services/carteira.service';
import { AuthService } from '../../core/services/auth.service';
import { InicioPage } from './inicio.page';

describe('InicioPage', () => {
  const auth = { currentUser: vi.fn() };
  const portfolios = { listForUser: vi.fn(), summary: vi.fn() };

  beforeEach(() => {
    auth.currentUser.mockReset(); portfolios.listForUser.mockReset(); portfolios.summary.mockReset();
    TestBed.configureTestingModule({ providers: [
      provideRouter([]),
      { provide: AuthService, useValue: auth },
      { provide: CarteiraService, useValue: portfolios },
    ] });
  });

  it('composes portfolio entry cards from published user, portfolio and summary responses', () => {
    auth.currentUser.mockReturnValue({ id: 2, nome: 'Ana', email: 'ana@email.com' });
    portfolios.listForUser.mockReturnValue(of({ content: [{ id: 7, nome: 'Longo prazo', corretoraId: 3 }] }));
    portfolios.summary.mockReturnValue(of({ carteiraId: 7, nome: 'Longo prazo', quantidadeAtivos: 2, resumosPorMoeda: [{ moeda: 'BRL', valorAtual: 100, lucroPrejuizo: 5 }] }));

    const fixture: ComponentFixture<InicioPage> = TestBed.createComponent(InicioPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Longo prazo');
    expect(fixture.nativeElement.textContent).toContain('R$ 100,00');
    expect(fixture.nativeElement.querySelector('a[href="/carteiras/7"]')).not.toBeNull();
  });

  it('keeps a truthful unavailable state when a portfolio summary fails', () => {
    auth.currentUser.mockReturnValue({ id: 2, nome: 'Ana', email: 'ana@email.com' });
    portfolios.listForUser.mockReturnValue(of({ content: [{ id: 7, nome: 'Longo prazo', corretoraId: 3 }] }));
    portfolios.summary.mockReturnValue(throwError(() => new Error('summary unavailable')));

    const fixture = TestBed.createComponent(InicioPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Resumo financeiro indisponível.');
    expect(fixture.nativeElement.textContent).not.toContain('R$ 0,00');
  });
});
