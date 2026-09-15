import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AcaoService } from '../../core/services/acao.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { AuthService } from '../../core/services/auth.service';
import { DashboardPage } from './dashboard.page';

describe('DashboardPage', () => {
  const positions = [
    { carteiraId: 1, acaoId: 10, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL' as const, moeda: 'BRL' as const, quantidade: 1, cotacaoAtual: 10, dataHoraCotacao: '2026-09-01T00:00:00Z', valorAtual: 10, precoMedio: 8, precoMedioVenda: null, lucroPrejuizoRealizado: null, lucroPrejuizoNaoRealizado: 2, lucroPrejuizo: 2 },
    { carteiraId: 1, acaoId: 11, ticker: 'AAPL', nomeEmpresa: 'Apple', mercado: 'EUA' as const, moeda: 'USD' as const, quantidade: 1, cotacaoAtual: 10, dataHoraCotacao: '2026-09-01T00:00:00Z', valorAtual: 10, precoMedio: 12, precoMedioVenda: null, lucroPrejuizoRealizado: null, lucroPrejuizoNaoRealizado: -1, lucroPrejuizo: -1 },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [
      provideRouter([]),
      { provide: AuthService, useValue: { currentUser: vi.fn(() => ({ id: 1, nome: 'Ana', email: 'ana@email.com' })) } },
      { provide: CarteiraService, useValue: { listForUser: vi.fn(() => of({ content: [{ id: 1, nome: 'Longo prazo', corretoraId: 2 }] })), summary: vi.fn(() => of({ carteiraId: 1, nome: 'Longo prazo', quantidadeAtivos: 2, resumosPorMoeda: [{ moeda: 'BRL', valorAtual: 10, lucroPrejuizo: 2 }, { moeda: 'USD', valorAtual: 10, lucroPrejuizo: -1 }] })), allPositions: vi.fn(() => of(positions)) } },
      { provide: AcaoService, useValue: { updateAllQuotes: vi.fn() } },
      { provide: FeedbackService, useValue: { success: vi.fn(), error: vi.fn() } },
    ] });
  });

  it('renders a donut and result pair independently for each available currency', () => {
    const fixture: ComponentFixture<DashboardPage> = TestBed.createComponent(DashboardPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('.analytics-currency-group')).toHaveLength(2);
    expect(fixture.nativeElement.querySelectorAll('app-chart-panel')).toHaveLength(4);
    expect(fixture.nativeElement.querySelectorAll('.chart-text-summary')).toHaveLength(4);
    expect(fixture.nativeElement.textContent).toContain('- US$');
    expect((fixture.componentInstance as unknown as { compositionChart: { type: string } }).compositionChart.type).toBe('donut');
  });

  it('keeps a neutral result semantically distinct from positive and negative bars', () => {
    const fixture = TestBed.createComponent(DashboardPage);
    fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { resultPlotOptions: { bar: { colors: { ranges: { color: string }[] } } }; resultDescription(value: number): string };

    expect(page.resultPlotOptions.bar.colors.ranges).toHaveLength(3);
    expect(page.resultDescription(0)).toBe('resultado neutro');
    expect(page.resultDescription(-1)).toBe('resultado negativo');
  });
});
