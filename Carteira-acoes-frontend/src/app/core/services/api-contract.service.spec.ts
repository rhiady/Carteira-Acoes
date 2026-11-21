import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { AcaoService } from './acao.service';
import { CarteiraService } from './carteira.service';
import { CorretoraService } from './corretora.service';
import { OperacaoService } from './operacao.service';
import { UsuarioService } from './usuario.service';

describe('API service contract', () => {
  let http: HttpTestingController;
  let acao: AcaoService;
  let carteira: CarteiraService;
  let corretora: CorretoraService;
  let operacao: OperacaoService;
  let usuario: UsuarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    http = TestBed.inject(HttpTestingController);
    acao = TestBed.inject(AcaoService); carteira = TestBed.inject(CarteiraService); corretora = TestBed.inject(CorretoraService); operacao = TestBed.inject(OperacaoService); usuario = TestBed.inject(UsuarioService);
  });
  afterEach(() => http.verify());
  const page = { content: [], totalElements: 0, totalPages: 0, size: 20, number: 0, first: true, last: true, numberOfElements: 0, empty: true };

  it('sends pageable collection requests with page, size and sort', () => {
    usuario.list({ page: 2, size: 5, sort: ['nome,asc'] }).subscribe();
    const request = http.expectOne('/api/usuarios?page=2&size=5&sort=nome,asc');
    expect(request.request.method).toBe('GET'); request.flush(page);
  });
  it('uses the published broker and user lookup paths', () => {
    corretora.getByCnpj('12/34').subscribe(); usuario.portfolios(8).subscribe();
    const brokerRequest = http.expectOne('/api/corretoras/cnpj/12%2F34');
    expect(brokerRequest.request.method).toBe('GET'); brokerRequest.flush({ id: 1, cnpj: '12/34' }); http.expectOne('/api/usuarios/8/carteiras?page=0&size=20&sort=id,desc').flush(page);
  });
  it('uses portfolio positions and operation history paths', () => {
    carteira.positions(3, { size: 10 }).subscribe(); operacao.history(3, { page: 1 }).subscribe();
    http.expectOne('/api/carteiras/3/acoes?page=0&size=10&sort=id,desc').flush(page); http.expectOne('/api/operacoes/carteiras/3?page=1&size=20&sort=dataHora,desc').flush(page);
  });
  it('normalizes ticker casing returned by the API', () => {
    const tickers: string[] = [];
    acao.list().subscribe((response) => tickers.push(response.content[0].ticker));
    carteira.positions(3).subscribe((response) => tickers.push(response.content[0].ticker));
    operacao.history(3).subscribe((response) => tickers.push(response.content[0].ticker));
    http.expectOne('/api/acoes?page=0&size=20&sort=id,desc').flush({ ...page, content: [{ ticker: 'petr4' }] });
    http.expectOne('/api/carteiras/3/acoes?page=0&size=20&sort=id,desc').flush({ ...page, content: [{ ticker: 'vale3' }] });
    http.expectOne('/api/operacoes/carteiras/3?page=0&size=20&sort=dataHora,desc').flush({ ...page, content: [{ ticker: 'itsa4' }] });
    expect(tickers).toEqual(['PETR4', 'VALE3', 'ITSA4']);
  });
  it('retrieves every position page for portfolio analytics', () => {
    const received: number[] = [];
    carteira.allPositions(3).subscribe((positions) => received.push(...positions.map((position) => position.acaoId)));
    http.expectOne('/api/carteiras/3/acoes?page=0&size=100&sort=id,desc').flush({ ...page, content: [{ acaoId: 1, ticker: 'petr4' }], totalPages: 2, empty: false });
    http.expectOne('/api/carteiras/3/acoes?page=1&size=100&sort=id,desc').flush({ ...page, content: [{ acaoId: 2, ticker: 'vale3' }], totalPages: 2, number: 1, empty: false });
    expect(received).toEqual([1, 2]);
  });
  it('uses POST for quote refresh and both trading commands', () => {
    acao.updateQuote(7).subscribe(); operacao.buy({ carteiraId: 1, acaoId: 2, quantidade: 1 }).subscribe(); operacao.sell({ carteiraId: 1, acaoId: 2, quantidade: 1 }).subscribe();
    const quoteRequest = http.expectOne('/api/acoes/7/cotacao');
    expect(quoteRequest.request.method).toBe('POST'); quoteRequest.flush({ ticker: 'PETR4' });
    const purchaseRequest = http.expectOne('/api/operacoes/compras');
    expect(purchaseRequest.request.method).toBe('POST'); expect(purchaseRequest.request.body).toEqual({ carteiraId: 1, acaoId: 2, quantidade: 1 }); expect(purchaseRequest.request.body).not.toHaveProperty('ticker'); expect(purchaseRequest.request.body).not.toHaveProperty('mercado'); purchaseRequest.flush({ ticker: 'PETR4' });
    http.expectOne('/api/operacoes/vendas').flush({ ticker: 'PETR4' });
  });
  it('creates a global action without financial-operation fields', () => {
    acao.create({ ticker: 'PETR4', mercado: 'BRASIL' }).subscribe();
    const request = http.expectOne('/api/acoes');
    expect(request.request.method).toBe('POST'); expect(request.request.body).toEqual({ ticker: 'PETR4', mercado: 'BRASIL' }); expect(request.request.body).not.toHaveProperty('carteiraId'); expect(request.request.body).not.toHaveProperty('acaoId'); request.flush({ ticker: 'PETR4' });
  });
  it('uses the published analytics, history and global refresh contracts', () => {
    carteira.summary(3).subscribe(); carteira.positions(3).subscribe(); acao.history(7, { inicio: '2026-09-01T00:00:00Z' }).subscribe(); acao.updateAllQuotes().subscribe();
    http.expectOne('/api/carteiras/3/resumo').flush({ carteiraId: 3, nome: 'Longo prazo', quantidadeAtivos: 0, resumosPorMoeda: [] });
    http.expectOne('/api/carteiras/3/acoes?page=0&size=20&sort=id,desc').flush(page);
    http.expectOne('/api/acoes/7/historico?page=0&size=100&sort=dataHora,asc&inicio=2026-09-01T00:00:00Z').flush(page);
    const refresh = http.expectOne('/api/acoes/atualizar-cotacoes'); expect(refresh.request.method).toBe('POST'); expect(refresh.request.body).toBeNull(); refresh.flush({ total: 0, atualizadas: 0, falhas: 0, dataHora: '2026-09-03T00:00:00Z' });
  });
});
