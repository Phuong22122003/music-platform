import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { Track } from '../../../../../core/models/track';
import { NextPlayListService } from '../../../../../core/services/next-play-list.service';
import { COVER_BASE_URL } from '../../../../utils/url';
import { LikedTrackService } from '../../../../../core/services/liked-track.service';
import { AuthService } from '../../../../../core/services/auth-service';
import { UiNotificationService } from '../../../../../core/services/ui-notification.service';
@Component({
  selector: 'app-next-up-item',
  standalone: false,
  templateUrl: './next-up-item.component.html',
  styleUrl: './next-up-item.component.scss',
})
export class NextUpItemComponent implements OnInit {
  @ViewChild('container', { static: true }) container!: ElementRef;
  @Input('track') track!: Track;
  @Input('trackIndex') trackIndex!: number;
  @Input('isPlaying') isPlaying: boolean = false;
  @Output() deleteEvent = new EventEmitter<number>();
  coverUrl = COVER_BASE_URL;
  constructor(
    private nextPlayListService: NextPlayListService,
    private renderer: Renderer2,
    private likedTrackService: LikedTrackService,
    private authService: AuthService,
    private uiNotification: UiNotificationService
  ) {}
  ngOnInit(): void {
    this.nextPlayListService.playPauseTrackInNextPLayListSubject.subscribe(
      (res) => {
        if (this.trackIndex !== res.index) return;
        if (res.type === 'PLAY') {
          this.isPlaying = true;
        } else {
          this.isPlaying = false;
        }
      }
    );
  }
  onPlay() {
    if (!this.isPlaying) {
      this.isPlaying = true;
      this.nextPlayListService.playPauseTrackInNextPLayListSubject.next({
        index: this.trackIndex,
        type: 'PLAY',
      });
    } else {
      this.isPlaying = false;
      this.nextPlayListService.playPauseTrackInNextPLayListSubject.next({
        index: this.trackIndex,
        type: 'STOP',
      });
    }
  }
  onDelete(event: any) {
    event.stopPropagation();

    // this.renderer.addClass(this.container.nativeElement, 'translate-x-full');
    // this.renderer.addClass(this.container.nativeElement, 'opacity-0');
    // setTimeout(() => {
    //   this.nextPlayListService.deleteTrackByIndex.next(this.trackIndex);
    // }, 300);
    this.deleteEvent.emit(this.trackIndex);
  }
  toggleLike(event: any) {
    event.stopPropagation();
    const loggedId = this.authService.getUserId() || '';
    if (loggedId === this.track.userId) return;
    const action = this.track.isLiked
      ? this.likedTrackService.unLikeTrack(this.track.id)
      : this.likedTrackService.likeTrack(this.track.id);

    action.subscribe((res) => {
      this.track.isLiked = !this.track.isLiked;
    });
  }
  showComingSoon(event: any) {
    event.stopPropagation();
    this.uiNotification.showComingSoon();
  }
}
