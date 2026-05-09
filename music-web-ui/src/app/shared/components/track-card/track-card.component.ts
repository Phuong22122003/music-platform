import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  OnDestroy,
} from '@angular/core';
import { Track } from '../../../core/models/track';
import { Router } from '@angular/router';
import { Observable, of, Subscription } from 'rxjs';
import { COVER_BASE_URL } from '../../utils/url';
import { FollowService } from '../../../core/services/follow.service';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { AuthService } from '../../../core/services/auth-service';
import copyLink from '../../utils/copylink';
import { AudioPlayerService } from '../../../core/services/audio-player.service';
import { NextPlayListService } from '../../../core/services/next-play-list.service';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-track-card',
  standalone: false,
  templateUrl: './track-card.component.html',
  styleUrl: './track-card.component.scss',
})
export class TrackCardComponent implements OnInit, OnDestroy {
  @Input({ required: false }) track!: Track;
  isShow = false;
  @Input() isLiked = false;
  @Input() isFollowed = false;
  @Output() trackStateChange = new EventEmitter<{
    trackId: string;
    isLiked: boolean;
  }>();
  @Output() followStatusChange = new EventEmitter<boolean>();

  toggleLikeBound = () => this.toggleLike();
  toggleFollowBound = () => this.toggleFollow();
  copyLinkBound: any;
  playingSub?: Subscription;
  isPlaying = false;
  playTrack = () => {
    this.audioPlayerService.playTrack(this.track);
  };
  pauseTrack = () => {
    this.audioPlayerService.pause();
  };
  addToNextPlayList = () => {
    this.nextPlayListService.addTrackSubject.next(this.track);
  };
  constructor(
    private router: Router,
    private followService: FollowService,
    private likedTrackService: LikedTrackService,
    private authService: AuthService,
    private audioPlayerService: AudioPlayerService,
    private nextPlayListService: NextPlayListService
  ) {}
  ngOnInit(): void {
    if (!this.track) return;

    const userId = this.authService.getUserId();
    if (userId) {
      // Kiểm tra trạng thái like nếu chưa được truyền vào
      if (!this.isLiked) {
        this.likedTrackService.isTrackLiked(this.track.id).subscribe((res) => {
          this.isLiked = res.data;
          this.trackStateChange.emit({
            trackId: this.track.id,
            isLiked: this.isLiked,
          });
        });
      }

      // Kiểm tra trạng thái follow
      this.followService
        .isFollowing(userId, this.track.userId)
        .subscribe((res) => {
          this.isFollowed = res.data;
          this.followStatusChange.emit(this.isFollowed);
        });
    }

    this.copyLinkBound = () => this.onCopyLink();

    this.audioPlayerService.currentTrack$.subscribe((currentTrack) => {
      const isCurrent = currentTrack?.id === this.track.id;
      if (isCurrent) {
        this.subscribeToPlaying();
      } else {
        this.unsubscribePlaying();
        this.isPlaying = false;
      }
    });
  }
  coverUrl = COVER_BASE_URL;
  goToTrack() {
    if (!this.track) return;
    this.router.navigate(['/song', this.track.id]);
  }

  prevent() {}

  toggleLike(): Observable<any> {
    if (!this.track) return of(null);

    const action = this.isLiked
      ? this.likedTrackService.unLikeTrack(this.track.id)
      : this.likedTrackService.likeTrack(this.track.id);

    return action.pipe(
      tap(() => {
        this.isLiked = !this.isLiked;
        this.trackStateChange.emit({
          trackId: this.track.id,
          isLiked: this.isLiked,
        });
      })
    );
  }
  toggleFollow(): Observable<any> {
    if (!this.track) return of(null);

    const action = this.isFollowed
      ? this.followService.unfollowUser(this.track.userId)
      : this.followService.followUser({ followingId: this.track.userId });

    return action.pipe(
      tap(() => {
        this.isFollowed = !this.isFollowed;
        this.followStatusChange.emit(this.isFollowed);
      })
    );
  }
  onCopyLink(): void {
    if (!this.track) return;

    const trackUrl = `${window.location.origin}/song/${this.track.id}`;
    copyLink(trackUrl);
  }
  private subscribeToPlaying() {
    if (this.playingSub) return; // tránh subscribe lại

    this.playingSub = this.audioPlayerService.isPlaying$.subscribe(
      (isPlaying) => {
        this.isPlaying = isPlaying;
      }
    );
  }

  private unsubscribePlaying() {
    if (this.playingSub) {
      this.playingSub.unsubscribe();
      this.playingSub = undefined;
    }
  }
  ngOnDestroy(): void {
    this.unsubscribePlaying();
  }
}
