import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { apiConfig } from '../config/api.config';
import { Corretora, CorretoraRequest, Page, Pageable } from '../models/domain.models';
import { pageableParams } from './pageable';
@Service()
export class CorretoraService {
  private readonly http = inject(HttpClient);
  list(pageable?: Pageable) { return this.http.get<Page<Corretora>>(apiConfig.endpoint('/corretoras'), { params: pageableParams(pageable) }); }
  get(id: number) { return this.http.get<Corretora>(apiConfig.endpoint(`/corretoras/${id}`)); }
  getByCnpj(cnpj: string) { return this.http.get<Corretora>(apiConfig.endpoint(`/corretoras/cnpj/${encodeURIComponent(cnpj)}`)); }
  create(request: CorretoraRequest) { return this.http.post<Corretora>(apiConfig.endpoint('/corretoras'), request); }
  delete(id: number) { return this.http.delete<void>(apiConfig.endpoint(`/corretoras/${id}`)); }
}
