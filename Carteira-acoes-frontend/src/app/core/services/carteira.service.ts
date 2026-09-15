import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { apiConfig } from '../config/api.config';
import { Carteira, CarteiraRequest, CarteiraUpdateRequest, Page, Pageable, PosicaoAcao, ResumoCarteira } from '../models/domain.models';
import { pageableParams } from './pageable';
import { forkJoin, map, of, switchMap } from 'rxjs';
@Service()
export class CarteiraService {
  private readonly http = inject(HttpClient);
  list(pageable?: Pageable) { return this.http.get<Page<Carteira>>(apiConfig.endpoint('/carteiras'), { params: pageableParams(pageable) }); }
  listForUser(userId: number, pageable?: Pageable) { return this.http.get<Page<Carteira>>(apiConfig.endpoint(`/usuarios/${userId}/carteiras`), { params: pageableParams(pageable) }); }
  get(id: number) { return this.http.get<Carteira>(apiConfig.endpoint(`/carteiras/${id}`)); }
  create(request: CarteiraRequest) { return this.http.post<Carteira>(apiConfig.endpoint('/carteiras'), request); }
  update(id: number, request: CarteiraUpdateRequest) { return this.http.put<Carteira>(apiConfig.endpoint(`/carteiras/${id}`), request); }
  delete(id: number) { return this.http.delete<void>(apiConfig.endpoint(`/carteiras/${id}`)); }
  positions(id: number, pageable?: Pageable) { return this.http.get<Page<PosicaoAcao>>(apiConfig.endpoint(`/carteiras/${id}/acoes`), { params: pageableParams(pageable) }); }
  allPositions(id: number) {
    const analyticsPageSize = 100;
    return this.positions(id, { page: 0, size: analyticsPageSize }).pipe(
      switchMap((firstPage) => {
        if (firstPage.totalPages <= 1) return of(firstPage.content);
        const remainingPages = Array.from(
          { length: firstPage.totalPages - 1 },
          (_, index) => this.positions(id, { page: index + 1, size: analyticsPageSize }),
        );
        return forkJoin(remainingPages).pipe(map((pages) => [
          ...firstPage.content,
          ...pages.flatMap((page) => page.content),
        ]));
      }),
    );
  }
  summary(id: number) { return this.http.get<ResumoCarteira>(apiConfig.endpoint(`/carteiras/${id}/resumo`)); }
}
