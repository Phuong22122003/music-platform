import { MatDialog } from '@angular/material/dialog';
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from '../components/confirm-dialog/confirm-dialog.component';

export function openConfirmDialog(
  dialog: MatDialog,
  data: ConfirmDialogData,
  onConfirm: () => void
): void {
  const dialogRef = dialog.open(ConfirmDialogComponent, {
    data: {
      title: data.title,
      description: data.description,
      confirmText: data.confirmText || 'Accept',
      cancelText: data.cancelText || 'Cancel',
    },
  });

  dialogRef.afterClosed().subscribe((result) => {
    if (result) {
      onConfirm();
    }
  });
}
