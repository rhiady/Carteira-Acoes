import { ComponentFixture, TestBed } from '@angular/core/testing';
import axe from 'axe-core';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { CorretoraService } from '../../core/services/corretora.service';
import { CorretorasPage } from './corretoras.page';

describe('CorretorasPage', () => {
  let fixture: ComponentFixture<CorretorasPage>;
  const service = { list: vi.fn(() => of({ content: [{ id: 1, cnpj: '00.000.000/0001-00', nomeFantasia: 'Corretora teste' }], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false })) };
  beforeEach(async () => { await TestBed.configureTestingModule({ imports: [CorretorasPage], providers: [provideRouter([]), { provide: CorretoraService, useValue: service }] }).compileComponents(); fixture = TestBed.createComponent(CorretorasPage); fixture.detectChanges(); await fixture.whenStable(); });
  it('renders a pageable broker table', () => { expect(fixture.nativeElement.querySelector('table')).toBeTruthy(); expect(fixture.nativeElement.textContent).toContain('Corretora teste'); });
  it('has no Axe violations in the representative table state', async () => { const result = await axe.run(fixture.nativeElement as HTMLElement, { rules: { 'color-contrast': { enabled: false } } }); expect(result.violations).toEqual([]); });
});
