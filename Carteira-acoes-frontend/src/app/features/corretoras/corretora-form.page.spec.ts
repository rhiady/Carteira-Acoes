import { ComponentFixture, TestBed } from '@angular/core/testing';
import axe from 'axe-core';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CorretoraService } from '../../core/services/corretora.service';
import { CorretoraFormPage } from './corretora-form.page';

describe('CorretoraFormPage', () => {
  let fixture: ComponentFixture<CorretoraFormPage>;
  const service = { create: vi.fn() };
  let navigateByUrl: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    service.create.mockReset();
    await TestBed.configureTestingModule({
      imports: [CorretoraFormPage],
      providers: [provideRouter([]), { provide: CorretoraService, useValue: service }],
    }).compileComponents();
    fixture = TestBed.createComponent(CorretoraFormPage);
    navigateByUrl = vi.spyOn(TestBed.inject(Router), 'navigateByUrl');
    fixture.detectChanges();
  });

  it('does not submit without a CNPJ', () => {
    submitForm(fixture);
    fixture.detectChanges();

    expect(service.create).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Informe o CNPJ.');
  });

  it('submits only the CNPJ and navigates after success', () => {
    service.create.mockReturnValue(of({ id: 1, cnpj: '12.345.678/0001-90' }));
    setCnpj(fixture, '12.345.678/0001-90');
    submitForm(fixture);

    expect(service.create).toHaveBeenCalledWith({ cnpj: '12.345.678/0001-90' });
    expect(navigateByUrl).toHaveBeenCalledWith('/corretoras');
  });

  it('keeps the form visible and shows the API error when creation fails', () => {
    service.create.mockReturnValue(throwError(() => ({ message: 'Corretora não registrada na CVM.' })));
    setCnpj(fixture, '12.345.678/0001-90');
    submitForm(fixture);
    fixture.detectChanges();

    expect(navigateByUrl).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Corretora não registrada na CVM.');
    expect((fixture.nativeElement.querySelector('#cnpj') as HTMLInputElement).value).toBe('12.345.678/0001-90');
  });

  it('has no Axe violations in the representative registration form', async () => {
    const results = await axe.run(fixture.nativeElement as HTMLElement, {
      rules: { 'color-contrast': { enabled: false } },
    });

    expect(results.violations).toEqual([]);
  });
});

function setCnpj(fixture: ComponentFixture<CorretoraFormPage>, cnpj: string) {
  const input = fixture.nativeElement.querySelector('#cnpj') as HTMLInputElement;
  input.value = cnpj;
  input.dispatchEvent(new Event('input'));
  fixture.detectChanges();
}

function submitForm(fixture: ComponentFixture<CorretoraFormPage>) {
  const form = fixture.nativeElement.querySelector('form') as HTMLFormElement;
  form.dispatchEvent(new Event('submit', { cancelable: true }));
}
