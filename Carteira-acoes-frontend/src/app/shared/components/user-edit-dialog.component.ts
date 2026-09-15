import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Usuario, UsuarioUpdateRequest } from '../../core/models/domain.models';
import { UsuarioService } from '../../core/services/usuario.service';
import { FeedbackService } from '../../core/services/feedback.service';

@Component({
  selector: 'app-user-edit-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatButtonModule],
  template: `<h2 mat-dialog-title>Editar usuário</h2><form mat-dialog-content [formGroup]="form" (ngSubmit)="submit()" novalidate><p class="dialog-entity">Atualize os dados de {{ data.nome }}.</p><label for="edit-user-name">Nome</label><input id="edit-user-name" formControlName="nome" autocomplete="name">@if(form.controls.nome.invalid&&form.controls.nome.touched){<p class="error" id="edit-user-name-error">Informe o nome.</p>}<label for="edit-user-email">E-mail</label><input id="edit-user-email" type="email" formControlName="email" autocomplete="email">@if(form.controls.email.invalid&&form.controls.email.touched){<p class="error" id="edit-user-email-error">Informe um e-mail válido.</p>}<label for="edit-user-password">Nova senha <span>(opcional)</span></label><input id="edit-user-password" type="password" formControlName="senha" autocomplete="new-password"><p class="helper-text">Deixe a senha em branco para manter a senha atual.</p>@if(error()){<p class="error" role="alert">{{ error() }}</p>}<div mat-dialog-actions align="end"><button mat-button type="button" [disabled]="loading()" (click)="ref.close()">Cancelar</button><button mat-flat-button type="submit" [disabled]="loading()">{{ loading() ? 'Salvando…' : 'Salvar alterações' }}</button></div></form>`
})
export class UserEditDialogComponent {
  protected readonly ref = inject(MatDialogRef<UserEditDialogComponent>);
  protected readonly data = inject<Usuario>(MAT_DIALOG_DATA);
  private readonly service = inject(UsuarioService);
  private readonly feedback = inject(FeedbackService);
  protected readonly loading = signal(false);
  protected readonly error = signal('');
  protected readonly form = new FormGroup({ nome: new FormControl(this.data.nome, { nonNullable: true, validators: Validators.required }), email: new FormControl(this.data.email, { nonNullable: true, validators: [Validators.required, Validators.email] }), senha: new FormControl('', { nonNullable: true }) });

  protected submit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true); this.error.set('');
    const value = this.form.getRawValue();
    const request: UsuarioUpdateRequest = { nome: value.nome.trim(), email: value.email.trim() };
    const password = value.senha.trim();
    if (password) request.senha = password;
    this.service.update(this.data.id, request).subscribe({ next: (user) => { this.feedback.success('Usuário atualizado.'); this.ref.close(user); }, error: (error: { status?: number; message?: string }) => { this.error.set(error.status === 409 ? (error.message ?? 'Já existe um usuário com este e-mail.') : error.status === 404 ? 'Usuário não encontrado.' : error.message ?? 'Não foi possível concluir a operação.'); this.loading.set(false); } });
  }
}
