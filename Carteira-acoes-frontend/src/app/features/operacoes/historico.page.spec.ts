import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { OperacaoService } from '../../core/services/operacao.service';
import { HistoricoPage } from './historico.page';

describe('HistoricoPage', () => {
  const history = vi.fn(() => of({
    content: [
      { id: 4, carteiraId: 7, acaoId: 1, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL' as const, moeda: 'BRL' as const, tipo: 'VENDA' as const, quantidade: 1, precoUnitario: 32, valorBruto: 32, corretagem: 1, impostos: 2, valorAdicional: 0, valorLiquido: 29, dataHora: '2026-09-06T17:00:00Z', createdAt: '2026-09-06T17:00:00Z' },
      { id: 3, carteiraId: 7, acaoId: 2, ticker: 'AAPL', nomeEmpresa: 'Apple', mercado: 'EUA' as const, moeda: 'USD' as const, tipo: 'COMPRA' as const, quantidade: 2, precoUnitario: 100, valorBruto: 200, corretagem: 0, impostos: 0, valorAdicional: 0, valorLiquido: 200, dataHora: '2026-09-05T17:00:00Z', createdAt: '2026-09-05T17:00:00Z' },
    ], totalElements: 42, totalPages: 3, size: 20, number: 1, first: false, last: false, numberOfElements: 2, empty: false,
  }));

  beforeEach(() => {
    history.mockClear();
    TestBed.configureTestingModule({ providers: [
      { provide: ActivatedRoute, useValue: { snapshot: { paramMap: new Map([['id', '7']]) } } },
      { provide: OperacaoService, useValue: { history } },
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
});
