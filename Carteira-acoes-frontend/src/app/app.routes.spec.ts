import { describe, expect, it } from 'vitest';
import { routes } from './app.routes';
import { authGuard, roleGuard } from './core/guards/auth.guard';

describe('application routes', () => {
  it('keeps login public and protects the application entry points', () => {
    expect(routes.find((route) => route.path === 'login')?.canActivate).toBeUndefined();
    expect(routes.find((route) => route.path === 'inicio')?.canActivate).toEqual([authGuard, roleGuard]);
    expect(routes.find((route) => route.path === 'dashboard')?.canActivate).toEqual([authGuard, roleGuard]);
    expect(routes.find((route) => route.path === 'admin')?.canActivate).toEqual([authGuard, roleGuard]);
    expect(routes.find((route) => route.path === 'admin')?.data).toEqual({ role: 'ROLE_ADMIN' });
  });
});
