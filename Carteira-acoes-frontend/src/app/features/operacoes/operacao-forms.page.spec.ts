import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import axe from 'axe-core';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CarteiraService } from '../../core/services/carteira.service';
import { AuthService } from '../../core/services/auth.service';
import { AcaoService } from '../../core/services/acao.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { OperacaoService } from '../../core/services/operacao.service';
import { Acao, AuthenticatedUser, PosicaoAcao } from '../../core/models/domain.models';
import { CompraPage } from './compra.page';
import { VendaPage } from './venda.page';

const route = { snapshot: { paramMap: new Map([['id', '7']]), queryParamMap: new Map<string, string>() } };
const operationService = { buy: vi.fn(), sell: vi.fn() };
const feedback = { success: vi.fn() };
const router = { navigate: vi.fn() };
const dialog = { open: vi.fn(() => ({ afterClosed: () => of(false) })) };
const position: PosicaoAcao = { carteiraId: 7, acaoId: 9, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL', moeda: 'BRL', quantidade: 2, cotacaoAtual: 25, dataHoraCotacao: '2026-09-08T12:00:00-03:00', valorAtual: 50, precoMedio: 20, precoMedioVenda: null, lucroPrejuizoRealizado: 0, lucroPrejuizoNaoRealizado: 10, lucroPrejuizo: 10 };
const asset: Acao = { id: 5, ticker: 'VALE3', nomeEmpresa: 'Vale', mercado: 'BRASIL', moeda: 'BRL', cotacaoAtual: 60, dataHoraCotacao: '2026-09-08T12:00:00-03:00', createdAt: '2026-09-01T00:00:00-03:00', updatedAt: '2026-09-08T12:00:00-03:00' };
const positionPage = (item: PosicaoAcao = position) => ({ content: [item], totalElements: 1, totalPages: 1, size: 20, number: 0, first: true, last: true, numberOfElements: 1, empty: false });
const positions = { get: vi.fn(() => of({ id: 7, usuarioId: 2 })), positions: vi.fn(() => of(positionPage())) };
const actions = { list: vi.fn(() => of({ content: [asset], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false })), get: vi.fn(() => of(asset)) };
const auth = { currentUser: vi.fn<() => AuthenticatedUser>(() => ({ id: 2, login: 'admin@email.com', authorities: ['ROLE_ADMIN'] })) };

describe('operation forms', () => {
  beforeEach(() => {
    operationService.buy.mockReset(); operationService.sell.mockReset(); feedback.success.mockReset(); router.navigate.mockReset(); dialog.open.mockClear();
    positions.get.mockReset(); positions.get.mockReturnValue(of({ id: 7, usuarioId: 2 })); positions.positions.mockReset(); positions.positions.mockReturnValue(of(positionPage()));
    actions.list.mockReset(); actions.list.mockReturnValue(of({ content: [asset], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false })); actions.get.mockReset(); actions.get.mockReturnValue(of(asset));
    auth.currentUser.mockReset(); auth.currentUser.mockReturnValue({ id: 2, login: 'admin@email.com', authorities: ['ROLE_ADMIN'] });
    TestBed.configureTestingModule({
      providers: [
        { provide: ActivatedRoute, useValue: route }, { provide: OperacaoService, useValue: operationService },
        { provide: CarteiraService, useValue: positions }, { provide: FeedbackService, useValue: feedback },
        { provide: AcaoService, useValue: actions },
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router }, { provide: MatDialog, useValue: dialog },
      ],
    });
  });

  it('prevents purchase without a registered action', () => {
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void }; submit(): void };
    page.form.patchValue({ quantidade: 1 }); page.submit();
    expect(operationService.buy).not.toHaveBeenCalled();
  });

  it('maps a catalog purchase to its action ID only', () => {
    operationService.buy.mockReturnValue(of({ id: 1 }));
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void }; selectCatalogAsset(): void; submit(): void };
    page.form.patchValue({ acaoId: asset.id, quantidade: 1, corretagem: 2 }); page.selectCatalogAsset(); page.submit();
    expect(actions.list).toHaveBeenCalledWith({ page: 0, size: 10, sort: 'ticker,asc' });
    expect(operationService.buy).toHaveBeenCalledWith({ carteiraId: 7, acaoId: asset.id, quantidade: 1, precoUnitario: 60, corretagem: 2 });
  });

  it('shows one catalog selector without legacy asset fields', () => {
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#acao')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('#ticker')).toBeNull();
    expect(fixture.nativeElement.querySelector('#mercado')).toBeNull();
  });

  it('lets a common user choose an own position and override its suggested quote', () => {
    auth.currentUser.mockReturnValue({ id: 2, login: 'user@email.com', authorities: ['ROLE_USER'] });
    operationService.buy.mockReturnValue(of({ id: 1 }));
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    const choice = fixture.nativeElement.querySelector('.purchase-position') as HTMLButtonElement;
    choice.click(); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void }; submit(): void };
    page.form.patchValue({ quantidade: 3, precoUnitario: 31 }); page.submit();

    expect(positions.positions).toHaveBeenCalledWith(7, { page: 0, size: 100, sort: 'ticker,asc' });
    expect(fixture.nativeElement.querySelector('#ticker')).toBeNull();
    expect(operationService.buy).toHaveBeenCalledWith({ carteiraId: 7, acaoId: 9, quantidade: 3, precoUnitario: 31 });
  });

  it('allows a common user to enter a price when the position quote is unavailable', () => {
    auth.currentUser.mockReturnValue({ id: 2, login: 'user@email.com', authorities: ['ROLE_USER'] });
    positions.positions.mockReturnValue(of(positionPage({ ...position, cotacaoAtual: null })));
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    (fixture.nativeElement.querySelector('.purchase-position') as HTMLButtonElement).click(); fixture.detectChanges();

    expect((fixture.nativeElement.querySelector('#preco') as HTMLInputElement).value).toBe('');
    expect(fixture.nativeElement.textContent).toContain('Sem cotação sugerida');
  });

  it('renders the purchase review and one primary confirmation action', () => {
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { form: { patchValue(value: object): void } };
    page.form.patchValue({ acaoId: asset.id, quantidade: 2, precoUnitario: 10 });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.purchase-review')?.textContent).toContain('Total estimado');
    expect(fixture.nativeElement.querySelector('.purchase-actions button')?.textContent).toContain('Confirmar compra');
    expect(fixture.nativeElement.textContent).not.toContain('Nova operação');
  });

  it('has no detectable accessibility violations in the purchase flow', async () => {
    const fixture = TestBed.createComponent(CompraPage); fixture.detectChanges();
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
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
