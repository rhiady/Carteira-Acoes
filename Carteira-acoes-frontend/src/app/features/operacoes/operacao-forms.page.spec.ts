import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { OperacaoService } from '../../core/services/operacao.service';
import { CompraPage } from './compra.page';
import { VendaPage } from './venda.page';

const route = { snapshot: { paramMap: new Map([['id', '7']]), queryParamMap: new Map<string, string>() } };
const operationService = { buy: vi.fn(), sell: vi.fn() };
const feedback = { success: vi.fn() };
const router = { navigate: vi.fn() };
const dialog = { open: vi.fn(() => ({ afterClosed: () => of(false) })) };
const positions = { positions: vi.fn(() => of({ content: [{ carteiraId: 7, acaoId: 9, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL', moeda: 'BRL', quantidade: 2, cotacaoAtual: 25, dataHoraCotacao: '2026-09-08T12:00:00-03:00', valorAtual: 50, precoMedio: 20, precoMedioVenda: null, lucroPrejuizoRealizado: 0, lucroPrejuizoNaoRealizado: 10, lucroPrejuizo: 10 }], totalElements: 1, totalPages: 1, size: 20, number: 0, first: true, last: true, numberOfElements: 1, empty: false })) };

describe('operation forms', () => {
  beforeEach(() => {
    operationService.buy.mockReset(); operationService.sell.mockReset(); feedback.success.mockReset(); router.navigate.mockReset(); dialog.open.mockClear();
    TestBed.configureTestingModule({
      providers: [
        { provide: ActivatedRoute, useValue: route }, { provide: OperacaoService, useValue: operationService },
        { provide: CarteiraService, useValue: positions }, { provide: FeedbackService, useValue: feedback },
        { provide: Router, useValue: router }, { provide: MatDialog, useValue: dialog },
      ],
    });
  });

  it('prevents conflicting purchase asset references', () => {
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void }; submit(): void };
    page.form.patchValue({ acaoId: 2, ticker: 'PETR4', mercado: 'BRASIL', quantidade: 1 }); page.submit();
    expect(operationService.buy).not.toHaveBeenCalled();
  });

  it('maps a ticker purchase to exactly one asset reference', () => {
    operationService.buy.mockReturnValue(of({ id: 1 }));
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void }; submit(): void };
    page.form.patchValue({ ticker: ' petr4 ', mercado: 'BRASIL', quantidade: 1, corretagem: 2 }); page.submit();
    expect(operationService.buy).toHaveBeenCalledWith({ carteiraId: 7, ticker: 'PETR4', mercado: 'BRASIL', quantidade: 1, corretagem: 2 });
  });

  it('prevents sales above the available position quantity', () => {
    const fixture: ComponentFixture<VendaPage> = TestBed.createComponent(VendaPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void }; confirmSale(): void };
    page.form.patchValue({ acaoId: 9, quantidade: 2.1 }); page.confirmSale();
    expect(dialog.open).not.toHaveBeenCalled();
    expect(operationService.sell).not.toHaveBeenCalled();
  });

  it('updates the estimated sale total when form values change', () => {
    const fixture: ComponentFixture<VendaPage> = TestBed.createComponent(VendaPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void } };
    page.form.patchValue({ acaoId: 9, quantidade: 1, corretagem: 2 });
    fixture.detectChanges();
    const summary = fixture.nativeElement.querySelector('.trade-summary--sale strong') as HTMLElement | null;
    expect(summary?.textContent).toMatch(/23[,.]00/);
  });
});
