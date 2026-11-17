import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { provideRouter } from '@angular/router';
import { describe, expect, it, vi } from 'vitest';
import { firstValueFrom, of } from 'rxjs';
import { authGuard, roleGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authorization guards', () => {
  const state = { url: '/usuarios' } as RouterStateSnapshot;

  function runGuard(guard: typeof authGuard | typeof roleGuard, auth: Partial<AuthService>, route = { data: { role: 'ROLE_ADMIN' } } as unknown as ActivatedRouteSnapshot) {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [provideRouter([]), { provide: AuthService, useValue: auth }] });
    return TestBed.runInInjectionContext(() => guard(route, state));
  }

  it('redirects anonymous navigation to login with returnUrl', () => {
    const result = runGuard(authGuard, { isAuthenticated: () => false, hasValidToken: () => false });
    expect(TestBed.inject(Router).serializeUrl(result as ReturnType<Router['createUrlTree']>)).toContain('/login?returnUrl=%2Fusuarios');
  });

  it('allows an authenticated session through the auth guard', () => {
    expect(runGuard(authGuard, { isAuthenticated: () => true })).toBe(true);
  });

  it('allows admins and blocks common users on admin routes', () => {
    expect(runGuard(roleGuard, { hasAuthority: (role) => role === 'ROLE_ADMIN' })).toBe(true);
    const blocked = runGuard(roleGuard, { hasAuthority: () => false });
    expect(TestBed.inject(Router).serializeUrl(blocked as ReturnType<Router['createUrlTree']>)).toBe('/acesso-negado');
  });

  it('restores a token-backed session before allowing navigation', async () => {
    const restoreSession = vi.fn(() => of({ id: 1, login: 'ana@email.com', authorities: ['ROLE_USER'] as const }));
    const result = runGuard(authGuard, { isAuthenticated: () => false, hasValidToken: () => true, restoreSession });
    expect(restoreSession).toHaveBeenCalled();
    await expect(firstValueFrom(result as ReturnType<typeof of>)).resolves.toBe(true);
  });
});
