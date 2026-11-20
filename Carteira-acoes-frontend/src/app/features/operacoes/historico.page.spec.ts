import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PosicaoAcao } from '../../core/models/domain.models';
import { CarteiraService } from '../../core/services/carteira.service';
import { OperacaoService } from '../../core/services/operacao.service';
import { HistoricoPage } from './historico.page';

describe('HistoricoPage', () => {
  const history = vi.fn(() => of({
    content: [
      { id: 4, carteiraId: 7, acaoId: 1, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL' as const, moeda: 'BRL' as const, tipo: 'VENDA' as const, quantidade: 1, precoUnitario: 32, valorBruto: 32, corretagem: 1, impostos: 2, valorAdicional: 0, valorLiquido: 29, dataHora: '2026-09-06T17:00:00Z', createdAt: '2026-09-06T17:00:00Z' },
      { id: 3, carteiraId: 7, acaoId: 2, ticker: 'AAPL', nomeEmpresa: 'Apple', mercado: 'EUA' as const, moeda: 'USD' as const, tipo: 'COMPRA' as const, quantidade: 2, precoUnitario: 100, valorBruto: 200, corretagem: 0, impostos: 0, valorAdicional: 0, valorLiquido: 200, dataHora: '2026-09-05T17:00:00Z', createdAt: '2026-09-05T17:00:00Z' },
    ], totalElements: 42, totalPages: 3, size: 20, number: 1, first: false, last: false, numberOfElements: 2, empty: false,
  }));
  const allPositions = vi.fn<() => Observable<readonly PosicaoAcao[]>>(() => of([
    { carteiraId: 7, acaoId: 2, ticker: 'AAPL', nomeEmpresa: 'Apple', mercado: 'EUA' as const, moeda: 'USD' as const, quantidade: 2, cotacaoAtual: 120, dataHoraCotacao: '2026-09-06T17:00:00Z', valorAtual: 240, precoMedio: 95, precoMedioVenda: 110, lucroPrejuizoRealizado: 0, lucroPrejuizoNaoRealizado: 50, lucroPrejuizo: 50 },
  ] as PosicaoAcao[]));

  beforeEach(() => {
    history.mockClear();
    allPositions.mockClear();
    TestBed.configureTestingModule({ providers: [
      { provide: ActivatedRoute, useValue: { snapshot: { paramMap: new Map([['id', '7']]) } } },
      { provide: OperacaoService, useValue: { history } },
      { provide: CarteiraService, useValue: { allPositions } },
    ] });
  });

  it('uses the paginated response content and exposes its navigation metadata', () => {
    const fixture: ComponentFixture<HistoricoPage> = TestBed.createComponent(HistoricoPage);
    fixture.detectChanges();

    expect(history).toHaveBeenCalledWith(7, { page: 0, size: 20 });
    expect(fixture.nativeElement.textContent).toContain('PETR4');
    expect(fixture.nativeElement.textContent).toContain('Petrobras');
    expect(fixture.nativeElement.textContent).toContain('Página 2 de 3');
  });

  it('filters the current page by operation type without claiming a historical aggregate', () => {
    const fixture = TestBed.createComponent(HistoricoPage);
    fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { filter: { set(value: 'COMPRA'): void } };
    page.filter.set('COMPRA');
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('AAPL');
    expect(fixture.nativeElement.textContent).not.toContain('PETR4');
    expect(fixture.nativeElement.textContent).toContain('Valores desta página');
    expect(fixture.nativeElement.textContent).toContain('USD');
  });

  it('@spec:AC-001 shows the authoritative purchase average only for purchases', () => {
    const fixture = TestBed.createComponent(HistoricoPage);
    fixture.detectChanges();

    const entries = fixture.nativeElement.querySelectorAll('.operation-history-item');
    expect(entries[0].textContent).not.toContain('Preço médio de compra');
    expect(entries[1].textContent).toContain('Preço médio de compra');
    expect(entries[1].textContent).toMatch(/\$\s?95[,.]00/);
    expect(allPositions).toHaveBeenCalledWith(7);
  });

  it('@spec:AC-002 shows an unavailable purchase average without using the sale average', () => {
    allPositions.mockReturnValueOnce(of([
      { carteiraId: 7, acaoId: 2, ticker: 'AAPL', nomeEmpresa: 'Apple', mercado: 'EUA' as const, moeda: 'USD' as const, quantidade: 2, cotacaoAtual: 120, dataHoraCotacao: '2026-09-06T17:00:00Z', valorAtual: 240, precoMedio: null, precoMedioVenda: 110, lucroPrejuizoRealizado: 0, lucroPrejuizoNaoRealizado: 50, lucroPrejuizo: 50 },
    ] as PosicaoAcao[]));
    const fixture = TestBed.createComponent(HistoricoPage);
    fixture.detectChanges();

    const entries = fixture.nativeElement.querySelectorAll('.operation-history-item');
    expect(entries[1].textContent).toContain('Preço médio de compra');
    expect(entries[1].textContent).toContain('—');
    expect(entries[1].textContent).not.toContain('110');
  });

  it('keeps the history available when positions cannot be loaded', () => {
    allPositions.mockReturnValueOnce(throwError(() => new Error('Posições indisponíveis.')));
    const fixture = TestBed.createComponent(HistoricoPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('AAPL');
    expect(fixture.nativeElement.textContent).toContain('Preço médio de compra');
    expect(fixture.nativeElement.textContent).toContain('—');
  });
});
