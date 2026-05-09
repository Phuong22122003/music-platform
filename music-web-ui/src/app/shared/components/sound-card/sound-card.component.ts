import { Component, Input, OnInit } from '@angular/core';
import { COVER_BASE_URL } from '../../utils/url';
import { AudioPlayerService } from '../../../core/services/audio-player.service';
import { Subscription } from 'rxjs';
import { Track } from '../../../core/models/track';
import { LikedTrackService } from '../../../core/services/liked-track.service';

@Component({
  selector: 'app-sound-card',
  standalone: false,
  templateUrl: './sound-card.component.html',
  styleUrl: './sound-card.component.scss',
})
export class SoundCardComponent implements OnInit {
  @Input() avatar: string = 'assets/ava.png';
  @Input() author: string = 'Unknown';
  @Input() songTitle: string = 'Unknown Song';
  @Input() playCount: number = 0;
  @Input() likeCount: number = 0;
  @Input() commentCount: number = 0;
  @Input() track!: Track;
  coverUrl = COVER_BASE_URL;
  isShow = false;
  isPlaying = false;
  playingSub?: Subscription;
  isLiked = false;
  constructor(
    private audioPlayerService: AudioPlayerService,
    private likeTrackService: LikedTrackService
  ) {}
  ngOnInit(): void {
    if (!this.track) return;
    this.audioPlayerService.currentTrack$.subscribe((currentTrack) => {
      const isCurrent = currentTrack?.id === this.track.id;
      if (isCurrent) {
        this.subscribeToPlaying();
      } else {
        this.unsubscribePlaying();
        this.isShow = false;
        this.isPlaying = false;
      }
    });
    this.likeTrackService.isTrackLiked(this.track.id).subscribe((isLiked) => {
      this.isLiked = isLiked.data;
      console.log(this.isLiked);
    });
  }
  showPlayButton() {
    this.isShow = true;
  }
  hiddenPlayButton() {
    if (this.isPlaying) return;
    this.isShow = false;
  }
  playTrack() {
    if (this.isPlaying) {
      this.audioPlayerService.pause();
    } else {
      this.audioPlayerService.playTrack(this.track);
    }
  }
  toggleLike() {
    const action = this.isLiked
      ? this.likeTrackService.unLikeTrack(this.track.id)
      : this.likeTrackService.likeTrack(this.track.id);
    action.subscribe((res) => {
      this.isLiked = !this.isLiked;
    });
  }
  private subscribeToPlaying() {
    if (this.playingSub) return; // tránh subscribe lại

    this.playingSub = this.audioPlayerService.isPlaying$.subscribe(
      (isPlaying) => {
        this.isPlaying = isPlaying;
        if (isPlaying) {
          this.isShow = true;
        }
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
