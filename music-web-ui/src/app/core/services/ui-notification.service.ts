import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class UiNotificationService {
  constructor(private snackBar: MatSnackBar) {}

  showComingSoon() {
    this.snackBar.open(
      'Coming Soon! 🚀 This feature is under development.',
      'Close',
      {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'bottom',
        panelClass: ['custom-snackbar'],
      }
    );
  }

  showInfo(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['info-snackbar'],
    });
  }
}
