import { ComponentFixture, TestBed } from '@angular/core/testing';
import axe from 'axe-core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { describe, expect, it, beforeEach, vi } from 'vitest';
import { AuthService } from '../../core/services/auth.service';
import { CarteiraService } from '../../core/services/carteira.service';
import { CorretoraService } from '../../core/services/corretora.service';
import { CarteiraDialogComponent, CorretoraDialogComponent } from './registration-dialogs.component';

describe('CorretoraDialogComponent', () => {
  let fixture: ComponentFixture<CorretoraDialogComponent>;
  beforeEach(async () => { await TestBed.configureTestingModule({ imports: [CorretoraDialogComponent, MatSnackBarModule], providers: [{ provide: MatDialogRef, useValue: { close() {} } }, { provide: CorretoraService, useValue: { create() {} } }] }).compileComponents(); fixture = TestBed.createComponent(CorretoraDialogComponent); fixture.detectChanges(); });
  it('has no Axe violations in the registration dialog', async () => { const result = await axe.run(fixture.nativeElement as HTMLElement, { rules: { 'color-contrast': { enabled: false } } }); expect(result.violations).toEqual([]); });
});

describe('CarteiraDialogComponent', () => {
  const create = vi.fn(() => of({ id: 10 }));
  const auth = { currentUser: vi.fn((): { id: number; login: string; authorities: readonly ('ROLE_ADMIN' | 'ROLE_USER')[] } | null => ({ id: 7, login: 'investidor@carteira.local', authorities: ['ROLE_ADMIN'] })) };
  const brokers = vi.fn(() => of({ content: [{ id: 4, nomeFantasia: 'Corretora Teste' }], totalElements: 1, totalPages: 1, size: 100, number: 0, first: true, last: true, numberOfElements: 1, empty: false }));

  beforeEach(() => {
    create.mockClear(); auth.currentUser.mockReset(); auth.currentUser.mockReturnValue({ id: 7, login: 'admin@carteira.local', authorities: ['ROLE_ADMIN'] });
    TestBed.configureTestingModule({ imports: [CarteiraDialogComponent, MatSnackBarModule], providers: [
      { provide: MatDialogRef, useValue: { close() {} } },
      { provide: AuthService, useValue: auth },
      { provide: CarteiraService, useValue: { create } },
      { provide: CorretoraService, useValue: { list: brokers } },
    ] });
  });

  it('uses the authenticated identity and does not expose a user selector', () => {
    const fixture = TestBed.createComponent(CarteiraDialogComponent);
    fixture.detectChanges();
    const dialog = fixture.componentInstance as unknown as { form: { setValue(value: { nome: string; corretoraId: number }): void }; submit(): void };
    dialog.form.setValue({ nome: 'Longo prazo', corretoraId: 4 });
    dialog.submit();

    expect(create).toHaveBeenCalledWith({ nome: 'Longo prazo', corretoraId: 4, usuarioId: 7 });
    expect(fixture.nativeElement.querySelector('#dialog-usuario')).toBeNull();
  });

  it('blocks creation when the authenticated identity is unavailable', () => {
    auth.currentUser.mockReturnValue(null);
    const fixture = TestBed.createComponent(CarteiraDialogComponent);
    fixture.detectChanges();
    const dialog = fixture.componentInstance as unknown as { form: { setValue(value: { nome: string; corretoraId: number }): void }; submit(): void };
    dialog.form.setValue({ nome: 'Longo prazo', corretoraId: 4 });
    dialog.submit();
    fixture.detectChanges();

    expect(create).not.toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain('Sua sessão não está disponível');
  });
});
