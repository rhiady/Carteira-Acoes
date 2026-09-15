import { Service, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Service()
export class FeedbackService {
  private readonly snackBar = inject(MatSnackBar);

  success(message: string) {
    this.snackBar.open(message, 'Fechar', { duration: 5000, panelClass: 'feedback-success' });
  }

  error(message: string) {
    this.snackBar.open(message, 'Fechar', { panelClass: 'feedback-error', politeness: 'assertive' });
  }
}
