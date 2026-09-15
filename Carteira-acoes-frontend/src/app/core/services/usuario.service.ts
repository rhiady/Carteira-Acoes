import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { apiConfig } from '../config/api.config';
import { Carteira, Page, Pageable, Usuario, UsuarioRequest, UsuarioUpdateRequest } from '../models/domain.models';
import { pageableParams } from './pageable';
@Service()
export class UsuarioService {
  private readonly http = inject(HttpClient);
  list(pageable?: Pageable) { return this.http.get<Page<Usuario>>(apiConfig.endpoint('/usuarios'), { params: pageableParams(pageable) }); }
  get(id: number) { return this.http.get<Usuario>(apiConfig.endpoint(`/usuarios/${id}`)); }
  portfolios(id: number, pageable?: Pageable) { return this.http.get<Page<Carteira>>(apiConfig.endpoint(`/usuarios/${id}/carteiras`), { params: pageableParams(pageable) }); }
  create(request: UsuarioRequest) { return this.http.post<Usuario>(apiConfig.endpoint('/usuarios'), request); }
  update(id: number, request: UsuarioUpdateRequest) { return this.http.put<Usuario>(apiConfig.endpoint(`/usuarios/${id}`), request); }
}
