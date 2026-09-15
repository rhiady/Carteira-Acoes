import { HttpClient } from '@angular/common/http';
import { Service, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { apiConfig } from '../config/api.config';
import { AuthenticatedUser, LoginRequest, LoginResponse } from '../models/domain.models';

const AUTHENTICATED_USER_KEY = 'authenticatedUser';

@Service()
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly storedUser = signal<AuthenticatedUser | null>(this.readStoredUser());

  readonly currentUser = this.storedUser.asReadonly();

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(apiConfig.endpoint('/auth/login'), request).pipe(
      tap((user) => this.setUser(user)),
      catchError((error: unknown) => throwError(() => error))
    );
  }

  isAuthenticated(): boolean {
    return this.currentUser() !== null;
  }

  logout(): void {
    this.storedUser.set(null);
    this.clearFinancialContext();
    localStorage.removeItem(AUTHENTICATED_USER_KEY);
    void this.router.navigateByUrl('/login');
  }

  clearFinancialContext(): void {
    for (const key of ['selectedWalletId', 'selectedPortfolioId', 'financialFilters']) {
      localStorage.removeItem(key);
      sessionStorage.removeItem(key);
    }
  }

  private setUser(user: AuthenticatedUser): void {
    const safeUser: AuthenticatedUser = { id: user.id, nome: user.nome, email: user.email };
    this.storedUser.set(safeUser);
    localStorage.setItem(AUTHENTICATED_USER_KEY, JSON.stringify(safeUser));
  }

  private readStoredUser(): AuthenticatedUser | null {
    try {
      const raw = localStorage.getItem(AUTHENTICATED_USER_KEY);
      if (!raw) return null;
      const parsed: unknown = JSON.parse(raw);
      if (!this.isAuthenticatedUser(parsed)) {
        localStorage.removeItem(AUTHENTICATED_USER_KEY);
        return null;
      }
      return { id: parsed.id, nome: parsed.nome, email: parsed.email };
    } catch {
      localStorage.removeItem(AUTHENTICATED_USER_KEY);
      return null;
    }
  }

  private isAuthenticatedUser(value: unknown): value is AuthenticatedUser {
    if (!value || typeof value !== 'object') return false;
    const candidate = value as Partial<AuthenticatedUser>;
    return typeof candidate.id === 'number'
      && typeof candidate.nome === 'string'
      && typeof candidate.email === 'string';
  }
}
