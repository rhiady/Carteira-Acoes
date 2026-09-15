import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { vi } from 'vitest';
import { ConfirmDialogComponent } from './confirm-dialog.component';

describe('ConfirmDialogComponent', () => {
  let fixture: ComponentFixture<ConfirmDialogComponent>;
  const dialogRef = { close: vi.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmDialogComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: { title: 'Excluir carteira?', message: 'A carteira será removida.', confirmLabel: 'Excluir carteira' } },
        { provide: MatDialogRef, useValue: dialogRef },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmDialogComponent);
    fixture.detectChanges();
    dialogRef.close.mockClear();
  });

  it('uses explicit destructive and cancel labels', () => {
    const buttons = Array.from(fixture.nativeElement.querySelectorAll('button')) as HTMLButtonElement[];
    expect(buttons.map((button) => button.textContent?.trim())).toEqual(['Cancelar', 'Excluir carteira']);
    expect(buttons.every((button) => button.type === 'button')).toBe(true);
  });

  it('returns the selected explicit action', () => {
    const buttons = Array.from(fixture.nativeElement.querySelectorAll('button')) as HTMLButtonElement[];
    buttons[0].click();
    expect(dialogRef.close).toHaveBeenLastCalledWith(false);

    buttons[1].click();
    expect(dialogRef.close).toHaveBeenLastCalledWith(true);
  });
});
