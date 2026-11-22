import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { apiConfig } from '../config/api.config';
import { normalizeTicker, normalizeTickerPage } from '../mappers/ticker.mapper';
import { CompraRequest, Operacao, Page, Pageable, VendaRequest } from '../models/domain.models';
import { pageableParams } from './pageable';
import { forkJoin, map, of, switchMap } from 'rxjs';
@Service()
export class OperacaoService {
  private readonly http = inject(HttpClient);
  buy(request: CompraRequest) { return this.http.post<Operacao>(apiConfig.endpoint('/operacoes/compras'), request).pipe(map(normalizeTicker)); }
  sell(request: VendaRequest) { return this.http.post<Operacao>(apiConfig.endpoint('/operacoes/vendas'), request).pipe(map(normalizeTicker)); }
  get(id: number) { return this.http.get<Operacao>(apiConfig.endpoint(`/operacoes/${id}`)).pipe(map(normalizeTicker)); }
  history(carteiraId: number, pageable?: Pageable) { return this.http.get<Page<Operacao>>(apiConfig.endpoint(`/operacoes/carteiras/${carteiraId}`), { params: pageableParams({ sort: 'dataHora,desc', ...pageable }) }).pipe(map(normalizeTickerPage)); }
  allHistory(carteiraId: number) {
    const size = 100;
    return this.history(carteiraId, { page: 0, size, sort: 'dataHora,asc' }).pipe(switchMap((first) => first.totalPages <= 1 ? of(first.content) : forkJoin(Array.from({ length: first.totalPages - 1 }, (_, index) => this.history(carteiraId, { page: index + 1, size, sort: 'dataHora,asc' }))).pipe(map((pages) => [ ...first.content, ...pages.flatMap((page) => page.content) ]))));
  }
}
