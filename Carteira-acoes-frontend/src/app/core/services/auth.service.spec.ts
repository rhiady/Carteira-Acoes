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
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([{ path: 'login', redirectTo: '', pathMatch: 'full' }])] });
    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => { httpTesting.verify(); localStorage.clear(); });

  it('posts credentials and stores only the returned user identity', () => {
    service.login({ email: 'ana@email.com', senha: 'segredo' }).subscribe();
    const request = httpTesting.expectOne('/api/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ email: 'ana@email.com', senha: 'segredo' });
    request.flush({ id: 2, nome: 'Ana', email: 'ana@email.com', senha: 'should-not-persist' });

    expect(service.currentUser()).toEqual({ id: 2, nome: 'Ana', email: 'ana@email.com' });
    expect(localStorage.getItem('authenticatedUser')).toBe(JSON.stringify({ id: 2, nome: 'Ana', email: 'ana@email.com' }));
    expect(localStorage.getItem('authenticatedUser')).not.toContain('senha');
  });

  it('restores a valid local user and discards malformed storage', () => {
    localStorage.setItem('authenticatedUser', JSON.stringify({ id: 3, nome: 'Bia', email: 'bia@email.com' }));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])] });
    const restored = TestBed.inject(AuthService);
    expect(restored.currentUser()).toEqual({ id: 3, nome: 'Bia', email: 'bia@email.com' });

    localStorage.setItem('authenticatedUser', JSON.stringify({ id: 'invalid' }));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])] });
    expect(TestBed.inject(AuthService).currentUser()).toBeNull();
  });

  it('propagates normalized authentication errors without storing a user', () => {
    service.login({ email: 'ana@email.com', senha: 'errada' }).subscribe({ error: () => undefined });
    const request = httpTesting.expectOne('/api/auth/login');
    request.flush({ status: 401, message: 'invalid credentials' }, { status: 401, statusText: 'Unauthorized' });
    expect(service.currentUser()).toBeNull();
    expect(localStorage.getItem('authenticatedUser')).toBeNull();
  });

  it('clears the authenticated identity and financial context on logout', () => {
    localStorage.setItem('authenticatedUser', JSON.stringify({ id: 2, nome: 'Ana', email: 'ana@email.com' }));
    localStorage.setItem('selectedWalletId', '7');
    sessionStorage.setItem('financialFilters', '{"market":"BRASIL"}');
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([{ path: 'login', redirectTo: '', pathMatch: 'full' }])] });
    const authenticated = TestBed.inject(AuthService);

    authenticated.logout();

    expect(authenticated.currentUser()).toBeNull();
    expect(localStorage.getItem('authenticatedUser')).toBeNull();
    expect(localStorage.getItem('selectedWalletId')).toBeNull();
    expect(sessionStorage.getItem('financialFilters')).toBeNull();
  });
});
