import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Subject, of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AcaoService } from '../../core/services/acao.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { AcaoDetailPage } from './acao-detail.page';

describe('AcaoDetailPage', () => {
  let update = new Subject<object>();
  const service = { get: vi.fn(() => of({ id: 5, ticker: 'PETR4', nomeEmpresa: 'Petrobras', mercado: 'BRASIL', moeda: 'BRL', cotacaoAtual: 10, dataHoraCotacao: '2026-01-01T00:00:00Z' })), history: vi.fn(() => of({ content: [], totalElements: 0, totalPages: 0, size: 100, number: 0, first: true, last: true, numberOfElements: 0, empty: true })), updateQuote: vi.fn(() => update) };

  beforeEach(() => {
    update = new Subject<object>();
    service.get.mockClear(); service.history.mockClear(); service.updateQuote.mockClear();
    TestBed.configureTestingModule({ providers: [
      { provide: ActivatedRoute, useValue: { snapshot: { paramMap: new Map([['id', '5']]) } } },
      { provide: AcaoService, useValue: service }, { provide: FeedbackService, useValue: { success: vi.fn(), error: vi.fn() } },
    ] });
  });

  it('prevents a duplicate quote refresh while the first request is pending', () => {
    const fixture: ComponentFixture<AcaoDetailPage> = TestBed.createComponent(AcaoDetailPage); fixture.detectChanges();
    const button = [...fixture.nativeElement.querySelectorAll('button')].find((candidate: HTMLButtonElement) => candidate.textContent?.includes('Atualizar cotação')) as HTMLButtonElement;
    button.click(); fixture.detectChanges(); button.click();
    expect(service.updateQuote).toHaveBeenCalledTimes(1);
    expect(button.disabled).toBe(true);
    update.complete();
  });

  it('reloads stock details before reloading history after a quote update', () => {
    const fixture = TestBed.createComponent(AcaoDetailPage); fixture.detectChanges();
    const page = fixture.componentInstance as unknown as { updateQuote(): void };
    page.updateQuote();
    update.next({});

    expect(service.get).toHaveBeenCalledTimes(2);
    expect(service.history).toHaveBeenCalledTimes(2);
  });
});
