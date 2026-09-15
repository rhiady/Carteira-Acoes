import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { apiConfig } from '../config/api.config';
import { CompraRequest, Operacao, Page, Pageable, VendaRequest } from '../models/domain.models';
import { pageableParams } from './pageable';
@Service()
export class OperacaoService {
  private readonly http = inject(HttpClient);
  buy(request: CompraRequest) { return this.http.post<Operacao>(apiConfig.endpoint('/operacoes/compras'), request); }
  sell(request: VendaRequest) { return this.http.post<Operacao>(apiConfig.endpoint('/operacoes/vendas'), request); }
  get(id: number) { return this.http.get<Operacao>(apiConfig.endpoint(`/operacoes/${id}`)); }
  history(carteiraId: number, pageable?: Pageable) { return this.http.get<Page<Operacao>>(apiConfig.endpoint(`/operacoes/carteiras/${carteiraId}`), { params: pageableParams({ sort: 'dataHora,desc', ...pageable }) }); }
}
