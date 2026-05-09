import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Track } from '../../../../core/models/track';
import { UserProfile } from '../../../../core/models/user_profile';
import { formatDistanceToNow } from 'date-fns';
import { COVER_BASE_URL, TRACK_BASE_URL } from '../../../../shared/utils/url';
import { AudioPlayerService } from '../../../../core/services/audio-player.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-track-play',
  standalone: false,
  templateUrl: './track-play.component.html',
  styleUrl: './track-play.component.scss',
})
export class TrackPlayComponent implements OnInit, OnDestroy {
  @Input() track!: Track;
  @Input() profile!: UserProfile;

  trackBaseUrl = TRACK_BASE_URL;
  coverBaseUrl = COVER_BASE_URL;
  muted: boolean = true;

  private subscriptions = new Subscription();
  isCurrent = false;
  isPlaying = false;

  constructor(
    private audioPlayerService: AudioPlayerService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.subscriptions.add(
      this.audioPlayerService.currentTrack$.subscribe((currentTrack) => {
        const wasCurrent = this.isCurrent;
        this.isCurrent = currentTrack?.id === this.track.id;

        if (this.isCurrent && !wasCurrent) {
          this.subscribeToPlayState();
        }
        // this.isCurrent = currentTrack?.id === this.track.id;
        // if (this.isCurrent) {
        //   this.subscribeToPlayState();
        // } else {
        //   this.unsubscribePlayState();
        //   this.muted = true;
        // }
      })
    );
    const play = this.route.snapshot.queryParamMap.get('play');
    if (play === 'true') {
      this.audioPlayerService.playTrack(this.track);
    }
  }

  onPlay() {
    if (this.isPlaying) {
      this.audioPlayerService.pause();
    } else {
      this.audioPlayerService.playTrack(this.track);
    }
  }

  changePlayState(isPause: boolean) {
    this.muted = !isPause;
  }

  timeAgo(date: string) {
    return formatDistanceToNow(new Date(date.split('.')[0]), {
      addSuffix: true,
    });
  }

  private subscribeToPlayState() {
    this.subscriptions.add(
      this.audioPlayerService.isPlaying$.subscribe((playing) => {
        this.isPlaying = playing;
        this.muted = !playing;
      })
    );
  }

  private unsubscribePlayState() {
    this.subscriptions.unsubscribe();
    this.subscriptions = new Subscription();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
