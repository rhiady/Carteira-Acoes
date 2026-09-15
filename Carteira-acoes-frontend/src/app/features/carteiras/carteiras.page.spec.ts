import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CarteiraService } from '../../core/services/carteira.service';
import { AuthService } from '../../core/services/auth.service';
import { CarteirasPage } from './carteiras.page';

describe('CarteirasPage', () => {
  const service = {
    list: vi.fn(() => of({ content: [{ id: 1, nome: 'Longo prazo', corretoraId: 2 }], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false })),
    listForUser: vi.fn(() => of({ content: [{ id: 1, nome: 'Longo prazo', corretoraId: 2 }], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false })),
    summary: vi.fn(() => of({ carteiraId: 1, nome: 'Longo prazo', quantidadeAtivos: 5, resumosPorMoeda: [{ moeda: 'BRL', valorAtual: 100, lucroPrejuizo: 10 }] })),
    positions: vi.fn(() => of({ content: [{ ticker: 'PETR4' }, { ticker: 'VALE3' }, { ticker: 'ITUB4' }], totalElements: 5, totalPages: 2, size: 3, number: 0, first: true, last: false, numberOfElements: 3, empty: false })),
  };

  beforeEach(() => {
    service.list.mockClear(); service.listForUser.mockClear(); service.summary.mockClear(); service.positions.mockClear();
    TestBed.configureTestingModule({ providers: [
      provideRouter([]),
      { provide: CarteiraService, useValue: service },
      { provide: AuthService, useValue: { currentUser: vi.fn(() => ({ id: 7, nome: 'Ana', email: 'ana@email.com' })) } },
      { provide: MatDialog, useValue: { open: vi.fn() } },
    ] });
  });

  it('shows a limited preview of authoritative position tickers and remaining assets', () => {
    const fixture: ComponentFixture<CarteirasPage> = TestBed.createComponent(CarteirasPage);
    fixture.detectChanges();

    expect(service.listForUser).toHaveBeenCalledWith(7, { page: 0, size: 10, sort: 'nome,asc' });
    expect(service.positions).toHaveBeenCalledWith(1, { page: 0, size: 3, sort: 'ticker,asc' });
    expect(fixture.nativeElement.querySelector('.ticker-preview')?.textContent).toContain('PETR4');
    expect(fixture.nativeElement.querySelector('.ticker-preview')?.textContent).toContain('+2 outros');
  });
});
