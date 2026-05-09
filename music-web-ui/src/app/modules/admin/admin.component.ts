import { Component, ViewEncapsulation, OnDestroy } from '@angular/core';
import { AuthService } from '../../core/services/auth-service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { Router } from '@angular/router';
import { AudioPlayerService } from '../../core/services/audio-player.service';
import { NextPlayListService } from '../../core/services/next-play-list.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-admin',
  standalone: false,
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class AdminComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private dialog: MatDialog,
    private router: Router,
    private audioPlayerService: AudioPlayerService,
    private nextPlayListService: NextPlayListService
  ) {
    // Subscribe to audio player changes
    this.audioPlayerService.currentTrack$
      .pipe(takeUntil(this.destroy$))
      .subscribe();

    this.audioPlayerService.isPlaying$
      .pipe(takeUntil(this.destroy$))
      .subscribe();
  }

  // async navigateToHome() {
  //   try {
  //     // Stop any playing audio
  //     this.audioPlayerService.pause();

  //     // Clear next playlist and wait for it to complete
  //     this.nextPlayListService.deleteAll.next();

  //     // Unsubscribe from all subscriptions
  //     this.destroy$.next();

  //     // Small delay to ensure cleanup is complete
  //     await new Promise((resolve) => setTimeout(resolve, 100));

  //     // Navigate to home
  //     await this.router.navigate(['/home']);
  //   } catch (error) {
  //     console.error('Error during navigation:', error);
  //   }
  // }

  onLogout() {
    const dialogData = {
      title: 'Logout',
      description: 'Are you sure you want to logout?',
      confirmText: 'Yes',
      cancelText: 'No',
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: dialogData,
    });

    dialogRef
      .afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe((result) => {
        if (result) {
          this.audioPlayerService.pause();
          this.nextPlayListService.deleteAll.next();
          this.authService.logout();
          this.router.navigate(['/home']);
        }
      });
  }

  ngOnDestroy() {
    // Complete and unsubscribe from all subscriptions
    this.destroy$.next();
    this.destroy$.complete();

    // Cleanup services
    this.audioPlayerService.pause();
    this.nextPlayListService.deleteAll.next();
  }
}
