import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { afterEach, describe, expect, it } from 'vitest';
import { Page, UsuarioResumo } from '../models/domain.models';
import { UsuarioService } from './usuario.service';

const response: Page<UsuarioResumo> = {
  content: [{ id: 4, login: 'ana@exemplo.com', papel: 'ROLE_USER', ativo: true }],
  totalElements: 1,
  totalPages: 1,
  size: 10,
  number: 0,
  first: true,
  last: true,
  numberOfElements: 1,
  empty: false,
};

describe('UsuarioService', () => {
  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it('uses the published paginated user resource and preserves its response', () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const service = TestBed.inject(UsuarioService);
    const http = TestBed.inject(HttpTestingController);
    let result: Page<UsuarioResumo> | undefined;

    service.list({ page: 0, size: 10, sort: 'login,asc' }).subscribe((page) => result = page);

    const request = http.expectOne((candidate) => candidate.url === '/api/usuarios');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('page')).toBe('0');
    expect(request.request.params.get('size')).toBe('10');
    expect(request.request.params.get('sort')).toBe('login,asc');
    request.flush(response);
    expect(result).toEqual(response);
  });

  it('does not replace a forbidden response with user records', () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const service = TestBed.inject(UsuarioService);
    const http = TestBed.inject(HttpTestingController);
    let status: number | undefined;

    service.list().subscribe({ error: (error: { status: number }) => status = error.status });

    const request = http.expectOne((candidate) => candidate.url === '/api/usuarios');
    request.flush({ message: 'Acesso negado.' }, { status: 403, statusText: 'Forbidden' });
    expect(status).toBe(403);
  });
});
