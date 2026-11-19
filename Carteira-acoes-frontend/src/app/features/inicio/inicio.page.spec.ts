import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { of, Subject, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../core/services/auth.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { InicioPage } from './inicio.page';

describe('InicioPage', () => {
  const positions = [
    { carteiraId: 1, acaoId: 10, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL' as const, moeda: 'BRL' as const, quantidade: 1, cotacaoAtual: 10, dataHoraCotacao: '2026-09-01T00:00:00Z', valorAtual: 10, precoMedio: 8, precoMedioVenda: null, lucroPrejuizoRealizado: null, lucroPrejuizoNaoRealizado: 2, lucroPrejuizo: 2 },
    { carteiraId: 1, acaoId: 11, ticker: 'AAPL', nomeEmpresa: 'Apple', mercado: 'EUA' as const, moeda: 'USD' as const, quantidade: 1, cotacaoAtual: 10, dataHoraCotacao: '2026-09-01T00:00:00Z', valorAtual: 10, precoMedio: 12, precoMedioVenda: null, lucroPrejuizoRealizado: null, lucroPrejuizoNaoRealizado: -1, lucroPrejuizo: -1 },
  ];
  const auth = { currentUser: vi.fn() };
  const portfolios = { listForUser: vi.fn(), summary: vi.fn(), allPositions: vi.fn() };

  beforeEach(() => {
    auth.currentUser.mockReset(); portfolios.listForUser.mockReset(); portfolios.summary.mockReset(); portfolios.allPositions.mockReset();
    auth.currentUser.mockReturnValue({ id: 2, login: 'ana@email.com', authorities: ['ROLE_USER'] });
    portfolios.listForUser.mockReturnValue(of({ content: [{ id: 1, nome: 'Longo prazo', corretoraId: 3 }] }));
    portfolios.summary.mockReturnValue(of({ carteiraId: 1, nome: 'Longo prazo', quantidadeAtivos: 2, resumosPorMoeda: [{ moeda: 'BRL', valorAtual: 10, lucroPrejuizo: 2 }, { moeda: 'USD', valorAtual: 10, lucroPrejuizo: -1 }] }));
    portfolios.allPositions.mockReturnValue(of(positions));
    TestBed.configureTestingModule({ providers: [
      provideRouter([]),
      { provide: AuthService, useValue: auth },
      { provide: CarteiraService, useValue: portfolios },
    ] });
  });

  it('renders identity, summary and a chart pair for every available currency', async () => {
    const fixture: ComponentFixture<InicioPage> = TestBed.createComponent(InicioPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('ana@email.com');
    expect(fixture.nativeElement.textContent).toContain('Longo prazo');
    expect(fixture.nativeElement.querySelectorAll('.analytics-currency-group')).toHaveLength(2);
    expect(fixture.nativeElement.querySelectorAll('app-chart-panel')).toHaveLength(4);
    expect(fixture.nativeElement.textContent).toContain('- US$');
    expect((fixture.componentInstance as unknown as { compositionChart: { type: string } }).compositionChart.type).toBe('donut');
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });

  it('synchronizes summary, charts and links when the selected portfolio changes', () => {
    portfolios.listForUser.mockReturnValue(of({ content: [{ id: 1, nome: 'Longo prazo', corretoraId: 3 }, { id: 2, nome: 'Reserva', corretoraId: 3 }] }));
    portfolios.summary.mockImplementation((id: number) => of({ carteiraId: id, nome: id === 1 ? 'Longo prazo' : 'Reserva', quantidadeAtivos: id, resumosPorMoeda: [{ moeda: 'BRL', valorAtual: id * 100, lucroPrejuizo: id }] }));
    portfolios.allPositions.mockImplementation((id: number) => of(positions.map((position) => ({ ...position, carteiraId: id }))));
    const fixture = TestBed.createComponent(InicioPage);
    fixture.detectChanges();
    const select = fixture.nativeElement.querySelector('select') as HTMLSelectElement;

    select.value = '2'; select.dispatchEvent(new Event('change')); fixture.detectChanges();

    expect(portfolios.summary).toHaveBeenLastCalledWith(2);
    expect(portfolios.allPositions).toHaveBeenLastCalledWith(2);
    expect(fixture.nativeElement.querySelector('a[href="/carteiras/2/comprar"]')).not.toBeNull();
    expect(fixture.nativeElement.textContent).toContain('R$ 200,00');
  });

  it('keeps analytics visible when only the financial summary fails', () => {
    portfolios.summary.mockReturnValue(throwError(() => ({ message: 'Resumo financeiro indisponível.' })));
    const fixture = TestBed.createComponent(InicioPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Resumo financeiro indisponível.');
    expect(fixture.nativeElement.querySelectorAll('app-chart-panel')).toHaveLength(4);
    expect(fixture.nativeElement.textContent).not.toContain('R$ 0,00');
  });

  it('shows truthful empty and loading analytics states', () => {
    const pendingPositions = new Subject<typeof positions>();
    portfolios.allPositions.mockReturnValue(pendingPositions);
    const fixture = TestBed.createComponent(InicioPage);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Carregando gráficos da carteira…');

    pendingPositions.next([]); pendingPositions.complete(); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Ainda não há posições com valor atual disponível nesta moeda.');
  });

  it('offers the first portfolio creation when the account has no portfolios', () => {
    portfolios.listForUser.mockReturnValue(of({ content: [] }));
    const fixture = TestBed.createComponent(InicioPage);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Você ainda não possui carteiras');
    expect(fixture.nativeElement.querySelector('a[href="/carteiras/nova"]')).not.toBeNull();
    expect(portfolios.summary).not.toHaveBeenCalled();
  });

  it('keeps a neutral result semantically distinct from positive and negative bars', () => {
    const fixture = TestBed.createComponent(InicioPage);
    fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { resultPlotOptions: { bar: { colors: { ranges: { color: string }[] } } }; resultDescription(value: number): string };
    expect(page.resultPlotOptions.bar.colors.ranges).toHaveLength(3);
    expect(page.resultDescription(0)).toBe('resultado neutro');
    expect(page.resultDescription(-1)).toBe('resultado negativo');
  });
});
