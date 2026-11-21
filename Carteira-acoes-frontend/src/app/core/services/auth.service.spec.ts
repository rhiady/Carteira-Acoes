import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    sessionStorage.clear(); localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([{ path: 'login', redirectTo: '', pathMatch: 'full' }])] });
    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => { httpTesting.verify(); sessionStorage.clear(); localStorage.clear(); });

  it('posts JWT credentials, stores only token metadata and loads /me', () => {
    service.login({ login: 'admin@carteira.local', password: 'segredo' }).subscribe();
    const login = httpTesting.expectOne('/api/auth/login');
    expect(login.request.body).toEqual({ login: 'admin@carteira.local', password: 'segredo' });
    login.flush({ token: 'jwt-token', tipo: 'Bearer', expiresIn: 3600 });
    const me = httpTesting.expectOne('/api/auth/me');
    me.flush({ id: 1, login: 'admin@carteira.local', authorities: ['ROLE_ADMIN'] });

    expect(service.currentUser()).toEqual({ id: 1, login: 'admin@carteira.local', authorities: ['ROLE_ADMIN'] });
    expect(sessionStorage.getItem('authToken')).toBe('jwt-token');
    expect(localStorage.length).toBe(0);
    expect(sessionStorage.getItem('authToken')).not.toContain('segredo');
  });

  it('registers a public account through the authentication endpoint', () => {
    service.register({ nome: 'Ana', email: 'ana@email.com', senha: 'senha123' }).subscribe();
    const request = httpTesting.expectOne('/api/auth/cadastro');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ nome: 'Ana', email: 'ana@email.com', senha: 'senha123' });
    request.flush({ id: 4, nome: 'Ana', email: 'ana@email.com' });
  });

  it('restores a valid token through /me after refresh', () => {
    sessionStorage.setItem('authToken', 'stored-token');
    sessionStorage.setItem('authTokenExpiresAt', String(Date.now() + 3600000));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([{ path: 'login', redirectTo: '', pathMatch: 'full' }])] });
    const restored = TestBed.inject(AuthService);
    const request = TestBed.inject(HttpTestingController).expectOne('/api/auth/me');
    request.flush({ id: 3, login: 'bia@carteira.local', authorities: ['ROLE_USER'] });
    expect(restored.currentUser()).toEqual({ id: 3, login: 'bia@carteira.local', authorities: ['ROLE_USER'] });
  });

  it('does not establish a session for invalid credentials', () => {
    service.login({ login: 'ana@email.com', password: 'errada' }).subscribe({ error: () => undefined });
    httpTesting.expectOne('/api/auth/login').flush({ status: 401, message: 'Credenciais inválidas.' }, { status: 401, statusText: 'Unauthorized' });
    expect(service.currentUser()).toBeNull();
    expect(sessionStorage.getItem('authToken')).toBeNull();
  });

  it('rejects an unsupported authority from /me', () => {
    service.login({ login: 'ana@email.com', password: 'segredo' }).subscribe({ error: () => undefined });
    httpTesting.expectOne('/api/auth/login').flush({ token: 'jwt-token', tipo: 'Bearer', expiresIn: 3600 });
    httpTesting.expectOne('/api/auth/me').flush({ id: 2, login: 'ana@email.com', authorities: ['ROLE_SUPERUSER'] });
    expect(service.currentUser()).toBeNull();
    expect(sessionStorage.getItem('authToken')).toBeNull();
  });

  it('clears an expired token without restoring a session', () => {
    sessionStorage.setItem('authToken', 'expired');
    sessionStorage.setItem('authTokenExpiresAt', String(Date.now() - 1));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([{ path: 'login', redirectTo: '', pathMatch: 'full' }])] });
    const expired = TestBed.inject(AuthService);
    expect(expired.currentUser()).toBeNull();
    expect(sessionStorage.getItem('authToken')).toBeNull();
  });

  it('clears token, identity and financial context on logout', () => {
    sessionStorage.setItem('authToken', 'jwt-token');
    sessionStorage.setItem('authTokenExpiresAt', String(Date.now() + 3600000));
    localStorage.setItem('selectedWalletId', '7');
    sessionStorage.setItem('financialFilters', '{"market":"BRASIL"}');
    service.logout();
    expect(service.currentUser()).toBeNull();
    expect(sessionStorage.getItem('authToken')).toBeNull();
    expect(localStorage.getItem('selectedWalletId')).toBeNull();
    expect(sessionStorage.getItem('financialFilters')).toBeNull();
  });
});
