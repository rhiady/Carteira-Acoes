import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { apiConfig } from '../config/api.config';
import { Acao, AtualizacaoCotacoes, HistoricoCotacao, HistoricoCotacaoParams, Page, Pageable } from '../models/domain.models';
import { pageableParams } from './pageable';
@Service()
export class AcaoService {
  private readonly http = inject(HttpClient);
  list(pageable?: Pageable) { return this.http.get<Page<Acao>>(apiConfig.endpoint('/acoes'), { params: pageableParams(pageable) }); }
  get(id: number) { return this.http.get<Acao>(apiConfig.endpoint(`/acoes/${id}`)); }
  getByTicker(ticker: string) { return this.http.get<Acao>(apiConfig.endpoint(`/acoes/ticker/${encodeURIComponent(ticker)}`)); }
  updateQuote(id: number) { return this.http.post<Acao>(apiConfig.endpoint(`/acoes/${id}/cotacao`), {}); }
  delete(id: number) { return this.http.delete<void>(apiConfig.endpoint(`/acoes/${id}`)); }
  history(id: number, params: HistoricoCotacaoParams = {}) {
    const { inicio, fim, ...pageable } = params;
    let query = pageableParams({ page: 0, size: 100, sort: 'dataHora,asc', ...pageable });
    if (inicio) query = query.set('inicio', inicio);
    if (fim) query = query.set('fim', fim);
    return this.http.get<Page<HistoricoCotacao>>(apiConfig.endpoint(`/acoes/${id}/historico`), { params: query });
  }
  updateAllQuotes() { return this.http.post<AtualizacaoCotacoes>(apiConfig.endpoint('/acoes/atualizar-cotacoes'), null); }
}
