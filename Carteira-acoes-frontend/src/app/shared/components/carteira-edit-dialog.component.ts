import { Component, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Carteira, CarteiraUpdateRequest } from '../../core/models/domain.models';
import { CarteiraService } from '../../core/services/carteira.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { maintenanceError } from '../../core/services/maintenance-error';

@Component({
  selector: 'app-carteira-edit-dialog',
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule],
  template: `<h2 mat-dialog-title>Editar carteira</h2><form mat-dialog-content (ngSubmit)="submit()" novalidate><p>Altere somente o nome de <strong>{{ data.nome }}</strong>.</p><label for="edit-wallet-name">Nome da carteira</label><input id="edit-wallet-name" [formControl]="form" autocomplete="off" aria-describedby="edit-wallet-name-error">@if(form.touched && form.invalid){<p id="edit-wallet-name-error" class="error" role="alert">Informe um nome válido.</p>}@if(error()){<p class="error" role="alert">{{ error() }}</p>}<div mat-dialog-actions align="end"><button mat-button type="button" [disabled]="loading()" (click)="ref.close()">Cancelar</button><button mat-flat-button type="submit" [disabled]="loading()">{{ loading() ? 'Salvando…' : 'Salvar alterações' }}</button></div></form>`,
})
export class CarteiraEditDialogComponent {
  protected readonly data = inject<Carteira>(MAT_DIALOG_DATA);
  protected readonly ref = inject(MatDialogRef<CarteiraEditDialogComponent>);
  private readonly service = inject(CarteiraService);
  private readonly feedback = inject(FeedbackService);
  protected readonly loading = signal(false);
  protected readonly error = signal('');
  protected readonly form = new FormControl(this.data.nome, { nonNullable: true, validators: [Validators.required, Validators.maxLength(120)] });

  protected submit() {
    if (this.loading()) return;
    const nome = this.form.value.trim();
    if (!nome) { this.form.markAsTouched(); return; }
    this.loading.set(true); this.error.set('');
    const request: CarteiraUpdateRequest = { nome };
    this.service.update(this.data.id, request).subscribe({
      next: (updated) => { this.feedback.success('Carteira atualizada com sucesso.'); this.ref.close(updated); },
      error: (error: unknown) => { this.error.set(maintenanceError(error, 'Não foi possível atualizar a carteira.', 'Esta carteira não está mais disponível.')); this.loading.set(false); },
    });
  }
}
