import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { firstValueFrom, throwError } from 'rxjs';
import { describe, expect, it } from 'vitest';
import { errorInterceptor } from './error.interceptor';

describe('errorInterceptor', () => {
  const request = new HttpRequest('GET', '/api/usuarios');

  it('preserves an error message returned by the API', async () => {
    const response = new HttpErrorResponse({
      status: 422,
      error: { message: 'E-mail já cadastrado.' },
    });

    await expect(firstValueFrom(errorInterceptor(request, () => throwError(() => response)))).rejects.toMatchObject({
      status: 422,
      message: 'E-mail já cadastrado.',
    });
  });

  it('normalizes errors without an HTTP response as backend unavailability', async () => {
    const response = new HttpErrorResponse({ status: 0, statusText: 'Unknown Error' });

    await expect(firstValueFrom(errorInterceptor(request, () => throwError(() => response)))).rejects.toMatchObject({
      status: 0,
      message: 'Não foi possível conectar ao backend. Verifique se o serviço está disponível.',
    });
  });
});
