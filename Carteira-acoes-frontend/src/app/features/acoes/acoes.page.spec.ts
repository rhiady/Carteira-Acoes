import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AcaoService } from '../../core/services/acao.service';
import { AuthService } from '../../core/services/auth.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { AcoesPage } from './acoes.page';

const asset = { id: 7, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL' as const, moeda: 'BRL' as const, cotacaoAtual: 25, dataHoraCotacao: '2026-11-21T12:00:00Z', createdAt: '2026-11-21T12:00:00Z', updatedAt: '2026-11-21T12:00:00Z' };
const page = { content: [asset], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false };

describe('AcoesPage', () => {
  const service = { list: vi.fn(() => of(page)), create: vi.fn(), getByTicker: vi.fn(), updateAllQuotes: vi.fn(), delete: vi.fn() };
  const auth = { hasAuthority: vi.fn(() => true) };
  const feedback = { success: vi.fn(), error: vi.fn() };

  beforeEach(() => {
    service.list.mockReset(); service.list.mockReturnValue(of(page)); service.create.mockReset();
    auth.hasAuthority.mockReset(); auth.hasAuthority.mockReturnValue(true);
    feedback.success.mockReset(); feedback.error.mockReset();
    TestBed.configureTestingModule({ providers: [
      provideRouter([]), { provide: AcaoService, useValue: service }, { provide: AuthService, useValue: auth },
      { provide: FeedbackService, useValue: feedback }, { provide: MatDialog, useValue: { open: vi.fn() } },
    ] });
  });

  it('shows the global-action form only to administrators', () => {
    const fixture: ComponentFixture<AcoesPage> = TestBed.createComponent(AcoesPage); fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('#ticker-create')).toBeTruthy();

    auth.hasAuthority.mockReturnValue(false);
    const commonFixture = TestBed.createComponent(AcoesPage); commonFixture.detectChanges();
    expect(commonFixture.nativeElement.querySelector('#ticker-create')).toBeNull();
  });

  it('posts only the normalized global-action payload and refreshes the catalog', () => {
    service.create.mockReturnValue(of(asset));
    const fixture: ComponentFixture<AcoesPage> = TestBed.createComponent(AcoesPage); fixture.detectChanges();
    const component = fixture.componentInstance as unknown as { createForm: { patchValue(value: object): void }; create(): void };
    component.createForm.patchValue({ ticker: ' petr4 ', mercado: 'BRASIL' }); component.create();

    expect(service.create).toHaveBeenCalledWith({ ticker: 'PETR4', mercado: 'BRASIL' });
    expect(service.list).toHaveBeenCalledTimes(2);
    expect(feedback.success).toHaveBeenCalledWith('Ação cadastrada com sucesso.');
  });

  it('keeps validation feedback and backend errors in the action form', () => {
    const fixture: ComponentFixture<AcoesPage> = TestBed.createComponent(AcoesPage); fixture.detectChanges();
    const component = fixture.componentInstance as unknown as { create(): void; createForm: { patchValue(value: object): void } };
    component.create(); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Informe o ticker.');

    service.create.mockReturnValue(throwError(() => ({ message: 'Ação já cadastrada.' })));
    component.createForm.patchValue({ ticker: 'PETR4' }); component.create(); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Ação já cadastrada.');
  });

  it('has no detectable accessibility violations for the administrative catalog', async () => {
    const fixture: ComponentFixture<AcoesPage> = TestBed.createComponent(AcoesPage); fixture.detectChanges();
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });
});
