import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export interface ConfirmDialogData { title: string; message: string; confirmLabel?: string; }

@Component({
  selector: 'app-confirm-dialog',
  imports: [MatButtonModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <p mat-dialog-content class="confirm-dialog__message">{{ data.message }}</p>
    <div mat-dialog-actions align="end" class="confirm-dialog confirm-dialog--destructive">
      <button mat-button type="button" (click)="dialogRef.close(false)">Cancelar</button>
      <button mat-flat-button type="button" (click)="dialogRef.close(true)">{{ data.confirmLabel ?? 'Confirmar' }}</button>
    </div>
  `,
})
export class ConfirmDialogComponent {
  protected readonly data = inject<ConfirmDialogData>(MAT_DIALOG_DATA);
  protected readonly dialogRef = inject(MatDialogRef<ConfirmDialogComponent>);
}
