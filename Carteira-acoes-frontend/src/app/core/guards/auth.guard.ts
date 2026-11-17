import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const login = router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  if (auth.isAuthenticated()) return true;
  if (!auth.hasValidToken()) return login;
  return auth.restoreSession().pipe(map(() => true), catchError(() => of(login)));
};

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.hasAuthority(route.data['role']) ? true : router.createUrlTree(['/acesso-negado']);
};
