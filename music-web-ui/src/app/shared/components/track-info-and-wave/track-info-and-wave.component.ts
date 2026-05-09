import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { WavesurferComponent } from '../wavesurfer/wavesurfer.component';
import { Track } from '../../../core/models/track';
import { environment } from '../../../../environments/environment';
import { ProfileService } from '../../../core/services/profile.service';
import { UserProfile } from '../../../core/models/user_profile';
import { CommentService } from '../../../core/services/comment.service';
import { CommentRequest } from '../../../core/models/comment/comment-request.model';
import { AuthService } from '../../../core/services/auth-service';
import { TrackAndWave } from '../../../core/models/track_wave';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { forkJoin, Observable, Subscription } from 'rxjs';
import { ApiResponse } from '../../../core/models/api_response';
import { TrackStatisticsService } from '../../../core/services/track-statistic.service';
import { TrackAction } from '../../../core/models/track_action';
import { PlaylistModalComponent } from '../playlist-modal/playlist-modal.component';
import { TrackList } from '../../../core/models/tracklist';
import copyLink from '../../utils/copylink';
import { TrackService } from '../../../core/services/track.service';
import { AudioPlayerService } from '../../../core/services/audio-player.service';

@Component({
  selector: 'app-track-info-and-wave',
  standalone: false,
  templateUrl: './track-info-and-wave.component.html',
  styleUrl: './track-info-and-wave.component.scss',
})
export class TrackInfoAndWaveComponent implements OnInit, OnDestroy {
  isPlaying = false;
  isCommentVisible = false;
  trackUrl = environment.fileApi + '/audios';
  profile!: UserProfile;
  @Input() track!: Track;
  private subscriptions = new Subscription();
  @Input() useForPlaylistAndAlbum: boolean = false;
  @Output() clickPlayButton = new EventEmitter<boolean>();
  @Output() comment = new EventEmitter<String>();
  @Output() like = new EventEmitter<boolean>();
  @ViewChild('waveform', { static: true }) waveformRef!: WavesurferComponent;
  private playingSub?: Subscription;
  coverUrl = environment.fileApi + '/images/covers';
  newComment: string = '';
  loggedUserId!: string | null;
  commentCount: number = 0;
  isOwner = false;
  sub!: Subscription;
  isInternalEmit = false;
  currentTrackId: string | null = null;
  constructor(
    private profileService: ProfileService,
    private commentService: CommentService,
    private authService: AuthService,
    private trackStatisticService: TrackStatisticsService,
    private trackService: TrackService,
    private audioPlayerService: AudioPlayerService
  ) {}

  ngOnInit(): void {
    console.log(this.track);
    this.loggedUserId = this.authService.getUserId() || null;
    this.isOwner = this.authService.getUserId() === this.track.userId;
    this.fetchData();
    this.subscriptions.add(
      this.trackService.updateDone$.subscribe((track) => {
        if (this.track.id === track.id) {
          this.track = track;
        }
      })
    );
    this.subscriptions.add(
      this.audioPlayerService.currentTrack$.subscribe((track) => {
        const isCurrent = track?.id === this.track.id;

        this.currentTrackId = track ? track.id : null;

        if (isCurrent) {
          this.subscribeToPlaying();
        } else {
          this.unsubscribePlaying();
          this.isPlaying = false;
        }
      })
    );
  }
  fetchData() {
    forkJoin({
      profile: this.profileService.getProfileById(this.track.userId),
      commentCount: this.trackStatisticService.getCommentCountForTrack(
        this.track.id
      ),
    }).subscribe(({ profile, commentCount }) => {
      this.profile = profile.data;
      this.commentCount = commentCount.data;
    });
  }
  // changeUrl(url: string) {
  //   this.waveformRef.changeUrl(url);
  // }

  togglePlaying() {
    this.isCommentVisible = true;

    this.isInternalEmit = true;
    if (this.isPlaying) {
      this.audioPlayerService.pause();
      // this.audioPlayerService.pause();
    } else {
      // this.audioPlayerService.playTrack(this.track);
      this.audioPlayerService.playTrack(this.track);
    }
  }

  // pause() {
  //   this.isPlaying = false;
  //   console.log('track info');
  //   this.waveformRef.pause();
  // }
  // play() {
  //   this.isPlaying = true;
  //   this.audioPlayerService.playTrack(this.track);
  // }
  changeButtonIcon(isPlaying: boolean) {
    // console.log(isPlaying);
    // this.isCommentVisible = true;
    // this.isPlaying = isPlaying;
    // this.clickPlayButton.emit(this.isPlaying);
  }

  submitComment() {
    if (!this.newComment.trim() && !this.loggedUserId) return;
    const request: CommentRequest = {
      content: this.newComment,
      likeCount: 0,
      trackId: this.track.id,
      userId: this.loggedUserId as string,
    };
    this.commentService.addComment(request).subscribe((res) => {
      this.newComment = '';
    });
  }
  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
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
}
