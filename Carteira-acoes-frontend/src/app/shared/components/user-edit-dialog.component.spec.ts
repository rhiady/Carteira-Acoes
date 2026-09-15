import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Usuario } from '../../core/models/domain.models';
import { FeedbackService } from '../../core/services/feedback.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { UserEditDialogComponent } from './user-edit-dialog.component';

const user: Usuario = { id: 1, nome: 'Ana', email: 'ana@email.com', createdAt: '2026-01-01', updatedAt: '2026-01-01' };

describe('UserEditDialogComponent', () => {
  let fixture: ComponentFixture<UserEditDialogComponent>;
  let service: { update: ReturnType<typeof vi.fn> };
  let dialog: { close: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    service = { update: vi.fn() };
    dialog = { close: vi.fn() };
    await TestBed.configureTestingModule({
      imports: [UserEditDialogComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: user },
        { provide: MatDialogRef, useValue: dialog },
        { provide: UsuarioService, useValue: service },
        { provide: FeedbackService, useValue: { success: vi.fn() } },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(UserEditDialogComponent);
    fixture.detectChanges();
  });

  function setValue(id: string, value: string) {
    const input = fixture.nativeElement.querySelector(`#${id}`) as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  function submit() {
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  it('prefills name and email while keeping password empty', () => {
    expect((fixture.nativeElement.querySelector('#edit-user-name') as HTMLInputElement).value).toBe('Ana');
    expect((fixture.nativeElement.querySelector('#edit-user-email') as HTMLInputElement).value).toBe('ana@email.com');
    expect((fixture.nativeElement.querySelector('#edit-user-password') as HTMLInputElement).value).toBe('');
  });

  it('omits a blank password from the update payload', () => {
    service.update.mockReturnValue(of(user));
    setValue('edit-user-name', ' Ana Atualizada ');
    setValue('edit-user-email', ' ana.nova@email.com ');
    setValue('edit-user-password', '   ');
    submit();

    expect(service.update).toHaveBeenCalledWith(1, { nome: 'Ana Atualizada', email: 'ana.nova@email.com' });
    expect(dialog.close).toHaveBeenCalledWith(user);
  });

  it('includes a supplied password without exposing it in the response model', () => {
    service.update.mockReturnValue(of({ ...user, nome: 'Ana Atualizada' }));
    setValue('edit-user-password', 'novaSenha123');
    submit();

    expect(service.update).toHaveBeenCalledWith(1, { nome: 'Ana', email: 'ana@email.com', senha: 'novaSenha123' });
    expect(fixture.nativeElement.textContent).not.toContain('novaSenha123');
  });

  it('prevents submission with an invalid email', () => {
    setValue('edit-user-email', 'invalid');
    submit();

    expect(service.update).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.error')).not.toBeNull();
  });

  it('keeps the dialog open and displays duplicate-email errors', () => {
    service.update.mockReturnValue(throwError(() => ({ status: 409, message: 'E-mail já utilizado.' })));
    submit();

    expect(dialog.close).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain('E-mail já utilizado.');
  });

  it('displays not-found errors without reporting success', () => {
    service.update.mockReturnValue(throwError(() => ({ status: 404, message: 'not found' })));
    submit();

    expect(dialog.close).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain('Usuário não encontrado.');
  });
});
