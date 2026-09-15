import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { CarteiraService } from './carteira.service';

describe('CarteiraService', () => {
  let service: CarteiraService; let http: HttpTestingController;
  beforeEach(() => { TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] }); service = TestBed.inject(CarteiraService); http = TestBed.inject(HttpTestingController); });
  afterEach(() => http.verify());
  it('loads wallets through the authenticated user resource', () => { service.listForUser(7, { page: 0, size: 10 }).subscribe(); const request = http.expectOne('/api/usuarios/7/carteiras?page=0&size=10&sort=id,desc'); expect(request.request.method).toBe('GET'); request.flush({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0, first: true, last: true, numberOfElements: 0, empty: true }); });
  it('updates only the wallet name', () => { service.update(7, { nome: 'Longo prazo' }).subscribe(); const request = http.expectOne('/api/carteiras/7'); expect(request.request.method).toBe('PUT'); expect(request.request.body).toEqual({ nome: 'Longo prazo' }); request.flush({ id: 7, nome: 'Longo prazo' }); });
  it('supports an empty successful delete response', () => { service.delete(7).subscribe(); const request = http.expectOne('/api/carteiras/7'); expect(request.request.method).toBe('DELETE'); request.flush(null, { status: 204, statusText: 'No Content' }); });
});
