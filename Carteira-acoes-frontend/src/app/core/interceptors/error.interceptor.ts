import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiError } from '../models/domain.models';
export const errorInterceptor: HttpInterceptorFn = (request, next) => next(request).pipe(catchError((error: unknown) => {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as Partial<ApiError> | null;
    const message = error.status === 0
      ? 'Não foi possível conectar ao backend. Verifique se o serviço está disponível.'
      : body?.message || 'Não foi possível concluir a operação.';
    const normalized: ApiError = { status: error.status, message, error: body?.error, timestamp: body?.timestamp, path: body?.path };
    return throwError(() => normalized);
  }
  return throwError(() => error);
}));
