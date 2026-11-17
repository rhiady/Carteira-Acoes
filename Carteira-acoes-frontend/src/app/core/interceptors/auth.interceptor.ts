import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  const token = request.url.endsWith('/auth/login') ? null : auth.getToken();
  const authenticatedRequest = token
    ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : request;
  return next(authenticatedRequest).pipe(catchError((error: { status?: number }) => {
    if (error.status === 401 && !request.url.endsWith('/auth/login')) auth.expireSession();
    return throwError(() => error);
  }));
};
