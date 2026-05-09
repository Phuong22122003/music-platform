import { BehaviorSubject, Subject } from 'rxjs';
import { Track } from '../models/track';
import { Injectable, OnDestroy } from '@angular/core';
import { TRACK_BASE_URL } from '../../shared/utils/url';
import { TrackList } from '../models/tracklist';
import { HistoryService } from './history.service';

@Injectable({ providedIn: 'root' })
export class AudioPlayerService implements OnDestroy {
  private audio = new Audio();
  private currentTrackSubject = new BehaviorSubject<Track | null>(null);
  private currentTrackListSubject = new BehaviorSubject<string | null>(null);
  private currentTimeSubject = new BehaviorSubject<number>(0);
  private trackUrl = TRACK_BASE_URL;
  private isPlayingSubject = new BehaviorSubject<boolean>(false);
  private isTrackInListPlayingSubject = new BehaviorSubject<boolean>(false);
  private durationSubject = new BehaviorSubject<number>(0);
  private audioEndedSubject = new BehaviorSubject<void>(undefined);
  public isLoadWavFileSubject = new BehaviorSubject<boolean>(false);
  // Observable streams
  currentTrack$ = this.currentTrackSubject.asObservable();
  currentTrackList$ = this.currentTrackListSubject.asObservable();
  currentTime$ = this.currentTimeSubject.asObservable();
  isPlaying$ = this.isPlayingSubject.asObservable();
  isTrackInListPlaying$ = this.isTrackInListPlayingSubject.asObservable();
  duration$ = this.durationSubject.asObservable();
  audioEnded$ = this.audioEndedSubject.asObservable();
  private onTimeUpdate = () => {
    this.currentTimeSubject.next(this.audio.currentTime);
  };

  private onEnded = () => {
    this.isPlayingSubject.next(false);
    this.audioEndedSubject.next();
  };

  private onLoadedMetadata = () => {
    this.durationSubject.next(this.audio.duration);
  };

  constructor(private historyService: HistoryService) {
    this.setupAudioListeners();
  }

  private setupAudioListeners() {
    this.audio.addEventListener('timeupdate', this.onTimeUpdate);
    this.audio.addEventListener('ended', this.onEnded);
    this.audio.addEventListener('loadedmetadata', this.onLoadedMetadata);
  }

  private removeAudioListeners() {
    this.audio.removeEventListener('timeupdate', this.onTimeUpdate);
    this.audio.removeEventListener('ended', this.onEnded);
    this.audio.removeEventListener('loadedmetadata', this.onLoadedMetadata);
  }

  getAudio(): HTMLAudioElement {
    return this.audio;
  }

  getCurrentVolume(): number {
    return this.audio.volume;
  }
  setVolume(value: number) {
    const volume = Math.max(0, Math.min(1, value));
    this.audio.volume = volume;
  }
  playTrack(track: Track, justUpdate: boolean = false) {
    if (this.currentTrackSubject.value?.id === track.id) {
      this.resume();
      return;
    }
    this.audio.src = `${this.trackUrl}/${track.fileName}`;
    this.audio.load();
    const onCanPlayThrough = () => {
      this.audio.removeEventListener('canplaythrough', onCanPlayThrough);
      this.audio
        .play()
        .then(() => {
          if (track.belongToTrackListId) {
            this.currentTrackListSubject.next(track.belongToTrackListId);
            this.isTrackInListPlayingSubject.next(true);
          }
          this.currentTrackSubject.next(track);
          this.isPlayingSubject.next(true);
          this.historyService.listenTrack(track.id);
        })
        .catch((err) => {
          console.error('Playback error:', err);
        });
    };

    this.audio.addEventListener('canplaythrough', onCanPlayThrough);
  }
  // playTrack(track: Track, justUpdate: boolean = false) {
  //   if (this.currentTrackSubject.value?.id === track.id) {
  //     this.resume();
  //     return;
  //   }
  //   this.audio.src = `${this.trackUrl}/${track.fileName}`;
  //   this.audio.load();
  //   this.isLoadWavFileSubject.subscribe((isLoadWavFile) => {
  //     if (isLoadWavFile) {
  //       const onCanPlayThrough = () => {
  //         this.audio.removeEventListener('canplaythrough', onCanPlayThrough);
  //         this.audio
  //           .play()
  //           .then(() => {
  //             if (track.belongToTrackListId) {
  //               this.currentTrackListSubject.next(track.belongToTrackListId);
  //               this.isTrackInListPlayingSubject.next(true);
  //             }
  //             this.currentTrackSubject.next(track);
  //             this.isPlayingSubject.next(true);
  //             this.historyService.listenTrack(track.id);
  //             this.isLoadWavFileSubject.unsubscribe();
  //           })
  //           .catch((err) => {
  //             console.error('Playback error:', err);
  //             this.isLoadWavFileSubject.unsubscribe();
  //           });
  //       };
  //       this.audio.addEventListener('canplaythrough', onCanPlayThrough);
  //     }
  //   });
  // }

  setCurrentTrackList(trackListId: string) {
    this.currentTrackListSubject.next(trackListId);
  }

  getCurrentTrackListId(): string | null {
    return this.currentTrackListSubject.getValue();
  }

  pause() {
    this.audio.pause();
    this.isPlayingSubject.next(false);
    this.isTrackInListPlayingSubject.next(false);
  }

  resume() {
    this.audio.play();
    this.isPlayingSubject.next(true);
    this.isTrackInListPlayingSubject.next(true);
  }

  getCurrentTrack(): Track | null {
    return this.currentTrackSubject.getValue();
  }

  seekTo(time: number) {
    console.log('seek to', time);
    this.audio.currentTime = time;
    this.currentTimeSubject.next(time);
  }

  togglePlay() {
    if (this.audio.paused) {
      this.resume();
    } else this.pause();
  }

  ngOnDestroy() {
    // Clean up audio element
    this.audio.pause();
    this.audio.src = '';
    this.removeAudioListeners();

    // Complete all subjects
    this.currentTrackSubject.complete();
    this.currentTrackListSubject.complete();
    this.currentTimeSubject.complete();
    this.isPlayingSubject.complete();
    this.isTrackInListPlayingSubject.complete();
    this.durationSubject.complete();
    this.audioEndedSubject.complete();
  }
}
