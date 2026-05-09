import { Component, ElementRef, Renderer2, ViewChild } from '@angular/core';
import { TrackService } from '../../../core/services/track.service';
import { COVER_BASE_URL, TRACK_BASE_URL } from '../../../shared/utils/url';
import { Track } from '../../../core/models/track';

@Component({
  selector: 'app-audio-player',
  standalone: false,
  templateUrl: './audio-player.component.html',
  styleUrl: './audio-player.component.scss',
})
export class AudioPlayerComponent {
  audioUrl = '';
  @ViewChild('audioPlayer', { static: true }) audioPlayer!: ElementRef;
  @ViewChild('processBar', { static: true }) processBar!: ElementRef;
  trackTitle = '';
  artist = 'Tranvietquang3110';
  isShow = false;
  isPlay = true;
  progress = 0;
  currentTime = 0;
  duration = 86;
  isSliderFocused = false;
  track!: Track;
  coverUrl = COVER_BASE_URL;
  togglePlay() {
    const audio = this.audioPlayer.nativeElement;
    if (audio.paused) {
      audio.play();
      this.isPlay = true;
    } else {
      audio.pause();
      this.isPlay = false;
    }
    this.trackService.emitTrackPlay({ isPlay: this.isPlay, track: this.track });
  }

  constructor(
    private trackService: TrackService,
    private renderer: Renderer2
  ) {}
  ngOnInit(): void {
    this.trackService.trackPlay$.subscribe((trackInfo) => {
      console.log('New track URL:', trackInfo);
      this.track = trackInfo.track;
      this.isShow = true;
      const audio = this.audioPlayer.nativeElement;
      this.isPlay = trackInfo.isPlay;
      const trackUrl = TRACK_BASE_URL + '/' + trackInfo.track.fileName;
      if (trackUrl !== this.audioUrl) {
        this.audioUrl = trackUrl;
        this.trackTitle = trackInfo.track.title || 'Unknown User';
        this.artist = trackInfo.track.displayName || 'Unknown User';
        audio.src = this.audioUrl;
      }
      if (trackInfo.isPlay) {
        audio.play();
      } else {
        audio.pause();
      }
    });
  }
  updateProgress() {
    const audio = this.audioPlayer.nativeElement;
    this.currentTime = audio.currentTime;
    this.progress = (audio.currentTime / audio.duration) * 100;

    this.renderer.setStyle(
      this.processBar.nativeElement,
      'width',
      `${Math.round(this.progress)}%`
    );
  }

  seekAudio(event: any) {
    const audio = this.audioPlayer.nativeElement;

    audio.currentTime = (event.target.value / 100) * audio.duration;
  }

  setDuration() {
    console.log(this.audioPlayer.nativeElement.duration);
    this.duration = this.audioPlayer.nativeElement.duration;
  }
  formatDuration(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const sec = Math.floor(seconds % 60);
    return `${minutes}:${sec < 10 ? '0' : ''}${sec}`;
  }
}
