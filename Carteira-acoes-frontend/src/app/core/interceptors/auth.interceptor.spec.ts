import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { describe, expect, it, beforeEach, afterEach } from 'vitest';
import { AuthService } from '../services/auth.service';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let testing: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({ providers: [
      provideHttpClient(withInterceptors([authInterceptor])),
      provideHttpClientTesting(),
      provideRouter([{ path: 'login', redirectTo: '', pathMatch: 'full' }]),
    ] });
    http = TestBed.inject(HttpClient);
    testing = TestBed.inject(HttpTestingController);
    sessionStorage.setItem('authToken', 'jwt-token');
    sessionStorage.setItem('authTokenExpiresAt', String(Date.now() + 3600000));
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => { testing.verify(); sessionStorage.clear(); });

  it('adds Bearer to protected requests and omits it from login', () => {
    http.get('/api/carteiras').subscribe();
    expect(testing.expectOne('/api/carteiras').request.headers.get('Authorization')).toBe('Bearer jwt-token');

    http.post('/api/auth/login', {}).subscribe();
    expect(testing.expectOne('/api/auth/login').request.headers.has('Authorization')).toBe(false);
  });

  it('clears the session on 401', () => {
    http.get('/api/private').subscribe({ error: () => undefined });
    testing.expectOne('/api/private').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(auth.getToken()).toBeNull();
  });

  it('keeps the session on 403', () => {
    http.get('/api/admin').subscribe({ error: () => undefined });
    testing.expectOne('/api/admin').flush({}, { status: 403, statusText: 'Forbidden' });
    expect(auth.getToken()).toBe('jwt-token');
  });
});
