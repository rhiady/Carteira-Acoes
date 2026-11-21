import { HttpClient } from '@angular/common/http';
import { Service, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, finalize, map, Observable, of, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { apiConfig } from '../config/api.config';
import { AuthenticatedUser, Authority, LoginRequest, LoginResponse, PasswordChangeRequest, Usuario, UsuarioRequest } from '../models/domain.models';

const TOKEN_KEY = 'authToken';
const TOKEN_EXPIRY_KEY = 'authTokenExpiresAt';

@Service()
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly storedUser = signal<AuthenticatedUser | null>(null);
  private readonly token = signal<string | null>(this.readToken());
  private restoreRequest: Observable<AuthenticatedUser> | null = null;

  readonly currentUser = this.storedUser.asReadonly();

  constructor() {
    if (this.hasValidToken()) this.restoreSession().subscribe({ error: () => undefined });
  }

  login(request: LoginRequest): Observable<AuthenticatedUser> {
    return this.http.post<LoginResponse>(apiConfig.endpoint('/auth/login'), request).pipe(
      tap((response) => this.storeToken(response)),
      switchMap(() => this.restoreSession()),
      catchError((error: unknown) => {
        this.clearSession(false);
        return throwError(() => error);
      })
    );
  }

  changePassword(request: PasswordChangeRequest): Observable<void> {
    return this.http.put<void>(apiConfig.endpoint('/auth/senha'), request);
  }

  register(request: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(apiConfig.endpoint('/auth/cadastro'), request);
  }

  getToken(): string | null { return this.hasValidToken() ? this.token() : null; }

  isAuthenticated(): boolean {
    return this.currentUser() !== null && this.hasValidToken();
  }

  hasValidToken(): boolean {
    const value = this.token();
    const expiresAt = Number(sessionStorage.getItem(TOKEN_EXPIRY_KEY));
    if (!value || !Number.isFinite(expiresAt) || expiresAt <= Date.now()) {
      if (value || sessionStorage.getItem(TOKEN_EXPIRY_KEY)) this.clearSession(false);
      return false;
    }
    return true;
  }

  hasAuthority(authority: Authority): boolean {
    return this.currentUser()?.authorities.includes(authority) ?? false;
  }

  restoreSession(): Observable<AuthenticatedUser> {
    if (this.currentUser() && this.hasValidToken()) return of(this.currentUser()!);
    if (!this.hasValidToken()) return throwError(() => new Error('Sessão ausente.'));
    if (this.restoreRequest) return this.restoreRequest;
    this.restoreRequest = this.http.get<AuthenticatedUser>(apiConfig.endpoint('/auth/me')).pipe(
      tap((user) => this.setUser(user)),
      finalize(() => { this.restoreRequest = null; }),
      shareReplay({ bufferSize: 1, refCount: false })
    );
    return this.restoreRequest;
  }

  logout(): void {
    this.clearSession(false);
    void this.router.navigateByUrl('/login');
  }

  expireSession(): void {
    this.clearSession(true);
  }

  clearFinancialContext(): void {
    for (const key of ['selectedWalletId', 'selectedPortfolioId', 'financialFilters']) {
      localStorage.removeItem(key);
      sessionStorage.removeItem(key);
    }
  }

  private storeToken(response: LoginResponse): void {
    this.token.set(response.token);
    sessionStorage.setItem(TOKEN_KEY, response.token);
    sessionStorage.setItem(TOKEN_EXPIRY_KEY, String(Date.now() + response.expiresIn * 1000));
  }

  private setUser(user: AuthenticatedUser): void {
    if (!this.isAuthenticatedUser(user)) throw new Error('Identidade autenticada inválida.');
    this.storedUser.set({ id: user.id, login: user.login, authorities: [...user.authorities] });
  }

  private clearSession(redirect: boolean): void {
    this.storedUser.set(null);
    this.token.set(null);
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_EXPIRY_KEY);
    this.clearFinancialContext();
    if (redirect) void this.router.navigateByUrl('/login');
  }

  private readToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  private isAuthenticatedUser(value: unknown): value is AuthenticatedUser {
    if (!value || typeof value !== 'object') return false;
    const candidate = value as Partial<AuthenticatedUser>;
    return typeof candidate.id === 'number' && typeof candidate.login === 'string'
      && Array.isArray(candidate.authorities)
      && candidate.authorities.every((authority) => authority === 'ROLE_ADMIN' || authority === 'ROLE_USER');
  }
}
