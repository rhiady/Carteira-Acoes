import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { CorretoraService } from './corretora.service';

describe('CorretoraService', () => {
  let service: CorretoraService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CorretoraService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('sends only the CNPJ when creating a broker', () => {
    service.create({ cnpj: '12.345.678/0001-90' }).subscribe();

    const request = httpTesting.expectOne('/api/corretoras');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ cnpj: '12.345.678/0001-90' });
    request.flush({ id: 1, cnpj: '12.345.678/0001-90' });
  });
});
