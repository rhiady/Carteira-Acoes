import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Subject, of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AcaoService } from '../../core/services/acao.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { Page, PosicaoAcao } from '../../core/models/domain.models';
import { CarteiraDetailPage } from './carteira-detail.page';

describe('CarteiraDetailPage', () => {
  const refresh = new Subject<{ total: number; atualizadas: number; falhas: number; dataHora: string }>();
  const carteiraService = {
    get: vi.fn(() => of({ id: 7 })),
    summary: vi.fn(() => of({ carteiraId: 7, nome: 'Longo prazo', quantidadeAtivos: 2, resumosPorMoeda: [{ moeda: 'BRL', valorAtual: 10, lucroPrejuizo: 2 }, { moeda: 'USD', valorAtual: 10, lucroPrejuizo: -1 }] })),
    positions: vi.fn(() => of({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0, first: true, last: true, numberOfElements: 0, empty: true } as Page<PosicaoAcao>)),
    allPositions: vi.fn(() => of([
      { carteiraId: 7, acaoId: 1, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL', moeda: 'BRL', quantidade: 1, cotacaoAtual: 10, dataHoraCotacao: '2026-09-03T00:00:00Z', valorAtual: 10, precoMedio: 8, precoMedioVenda: null, lucroPrejuizoRealizado: null, lucroPrejuizoNaoRealizado: 2, lucroPrejuizo: 2 },
      { carteiraId: 7, acaoId: 2, ticker: 'AAPL', nomeEmpresa: 'Apple', mercado: 'EUA', moeda: 'USD', quantidade: 1, cotacaoAtual: 10, dataHoraCotacao: '2026-09-03T00:00:00Z', valorAtual: 10, precoMedio: 12, precoMedioVenda: null, lucroPrejuizoRealizado: null, lucroPrejuizoNaoRealizado: -1, lucroPrejuizo: -1 },
    ])),
  };
  const acaoService = { updateAllQuotes: vi.fn(() => refresh) };

  beforeEach(() => {
    carteiraService.get.mockClear(); carteiraService.summary.mockClear(); carteiraService.positions.mockClear(); carteiraService.allPositions.mockClear();
    acaoService.updateAllQuotes.mockClear();
    TestBed.configureTestingModule({ providers: [
      { provide: ActivatedRoute, useValue: { snapshot: { paramMap: new Map([['id', '7']]) } } },
      { provide: CarteiraService, useValue: carteiraService },
      { provide: AcaoService, useValue: acaoService },
      { provide: FeedbackService, useValue: { success: vi.fn(), error: vi.fn() } },
    ] });
  });

  it('reloads the portfolio summary and positions after a quote refresh', () => {
    const fixture: ComponentFixture<CarteiraDetailPage> = TestBed.createComponent(CarteiraDetailPage);
    fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { refreshQuotes(): void };

    page.refreshQuotes();
    expect(acaoService.updateAllQuotes).toHaveBeenCalledOnce();
    refresh.next({ total: 2, atualizadas: 2, falhas: 0, dataHora: '2026-09-03T00:00:00Z' });
    refresh.complete();

    expect(carteiraService.get).toHaveBeenCalledTimes(2);
    expect(carteiraService.summary).toHaveBeenCalledTimes(2);
    expect(carteiraService.positions).toHaveBeenCalledTimes(2);
    expect(carteiraService.allPositions).toHaveBeenCalledTimes(2);
  });

  it('renders an independent accessible chart pair for each currency', () => {
    const fixture = TestBed.createComponent(CarteiraDetailPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('.analytics-currency-group')).toHaveLength(2);
    expect(fixture.nativeElement.querySelectorAll('.chart-grid app-chart-panel')).toHaveLength(6);
    expect(fixture.nativeElement.textContent).toContain('- US$');
    const page = fixture.componentInstance as unknown as { resultPlotOptions: { bar: { colors: { ranges: unknown[] } } } };
    expect(page.resultPlotOptions.bar.colors.ranges).toHaveLength(3);
  });

  it('renders authoritative investment totals with distinct labels', () => {
    carteiraService.positions.mockReturnValueOnce(of({ content: [{ carteiraId: 7, acaoId: 1, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL', moeda: 'BRL', quantidade: 15, cotacaoAtual: 45, dataHoraCotacao: '2026-09-03T00:00:00Z', valorAtual: 675, precoMedio: 35, valorInvestidoAtual: 525, valorTotalComprado: 700, valorTotalVendido: 225, precoMedioVenda: 45, lucroPrejuizoRealizado: 50, lucroPrejuizoNaoRealizado: 150, lucroPrejuizo: 200 }], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false } as Page<PosicaoAcao>));
    const fixture = TestBed.createComponent(CarteiraDetailPage);
    fixture.detectChanges();

    const text = fixture.nativeElement.textContent.replace(/\u00a0/g, ' ');
    expect(text).toContain('Investido atualmente');
    expect(text).toContain('Total comprado');
    expect(text).toContain('Total vendido');
    expect(text).toContain('Resultado realizado');
    expect(text).toContain('Resultado não realizado');
    expect(text).toContain('Resultado total');
    expect(text).toContain('R$ 525,00');
    expect(text).toContain('R$ 225,00');
    expect(text).toContain('+ R$ 50,00');
  });
});
