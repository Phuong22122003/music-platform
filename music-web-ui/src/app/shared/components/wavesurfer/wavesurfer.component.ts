import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  Output,
  EventEmitter,
  ViewChild,
  OnInit,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import WaveSurfer from 'wavesurfer.js';
import Hover from 'wavesurfer.js/dist/plugins/hover.esm.js';
import { AudioPlayerService } from '../../../core/services/audio-player.service';
import { TRACK_BASE_URL } from '../../utils/url';
import { Subscription } from 'rxjs';
import { TrackService } from '../../../core/services/track.service';
import { Track } from '../../../core/models/track';

@Component({
  selector: 'app-wavesurfer',
  standalone: false,
  templateUrl: './wavesurfer.component.html',
  styleUrls: ['./wavesurfer.component.scss'],
})
export class WavesurferComponent implements OnInit, AfterViewInit, OnChanges {
  @Input() track!: Track;
  @Input() height: number = 100;
  @Input() isMuted = false;
  @Input() isPlay = false;
  @Output() playStateChanged = new EventEmitter<boolean>();

  @ViewChild('waveform', { static: true }) waveformRef!: ElementRef;
  @ViewChild('totalTime', { static: false }) totalTimeRef!: ElementRef;

  private wavesurfer!: WaveSurfer;
  private subscriptions = new Subscription();
  private duration: number = 0;
  trackUrl = TRACK_BASE_URL;
  isSubscribedToState = false;
  isCurrent: any = false;
  constructor(
    private audioPlayerService: AudioPlayerService,
    private trackService: TrackService
  ) {}

  ngOnInit(): void {
    let wasCurrent = false; // theo dõi trạng thái cũ

    this.subscriptions.add(
      this.audioPlayerService.currentTrack$.subscribe((currentTrack) => {
        const isNowCurrent = currentTrack?.id === this.track.id;

        if (wasCurrent !== isNowCurrent) {
          this.isCurrent = isNowCurrent;

          if (this.isCurrent) {
            this.initWavesurfer(true); // dùng audio element
            this.playStateChanged.emit(true);
            this.events();
            // this.play();
          } else {
            this.initWavesurfer(false); // dùng WebAudio riêng
            this.playStateChanged.emit(false);
          }
        }

        wasCurrent = isNowCurrent;
      })
    );
  }

  ngAfterViewInit(): void {
    // this.initWavesurfer(this.isCurrent);
    // this.events();
  }
  play() {
    // if (this.isCurrent) {
    //   this.audioPlayerService.resume();
    //   console.log('track current');
    // } else {
    //   console.log('track moi');
    //   this.audioPlayerService.playTrack(this.track);
    // }
    this.audioPlayerService.playTrack(this.track);
    // this.playStateChanged.emit(true);
  }
  pause() {
    this.audioPlayerService.pause();
    // this.playStateChanged.emit(false);
  }
  // private subscribeToAudioState() {
  //   if (this.isSubscribedToState) return;
  //   this.isSubscribedToState = true;

  //   this.subscriptions.add(
  //     this.audioPlayerService.currentTime$.subscribe((time) => {
  //       if (this.wavesurfer && !this.wavesurfer.isPlaying()) {
  //         const progress = time / this.duration;
  //         this.wavesurfer.seekTo(progress);
  //       }
  //     })
  //   );

  //   this.subscriptions.add(
  //     this.audioPlayerService.isPlaying$.subscribe((isPlaying) => {
  //       if (!this.wavesurfer) return;

  //       this.playStateChanged.emit(isPlaying);
  //     })
  //   );
  // }
  // private unsubscribeAudioState() {
  //   this.subscriptions.unsubscribe();
  //   this.subscriptions = new Subscription();
  //   this.isSubscribedToState = false;
  // }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['track']) {
      const newTrack: Track = changes['track'].currentValue;
      console.log(newTrack);
      // Re-init WaveSurfer
      // this.initWavesurfer(true);

      // Load new track
      const newUrl = `${TRACK_BASE_URL}/${newTrack.fileName}`;
      this.changeUrl(newUrl);
    }
  }

  private changeUrl(url: string) {
    if (!this.wavesurfer) {
      return;
    }
    this.wavesurfer.load(url);
    this.wavesurfer.once('ready', () => {
      this.duration = this.wavesurfer.getDuration();
      if (this.totalTimeRef) {
        this.totalTimeRef.nativeElement.innerText = this.formatTime(
          this.duration
        );
      }
    });
  }

  private events() {
    this.wavesurfer.on('ready', () => {
      this.duration = this.wavesurfer.getDuration();
      if (this.totalTimeRef) {
        this.totalTimeRef.nativeElement.innerText = this.formatTime(
          this.duration
        );
      }
    });

    // this.wavesurfer.on('seeking', (progress: number) => {
    //   const time = this.duration * progress;
    //   this.audioPlayerService.seekTo(time);
    // });

    this.wavesurfer.on('finish', () => {
      this.playStateChanged.emit(false);
    });

    this.wavesurfer.setMuted(this.isMuted);
  }

  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
  }
  private initWavesurfer(useAudioElement: boolean) {
    if (this.wavesurfer) {
      this.wavesurfer.destroy();
    }

    const options: any = {
      container: this.waveformRef.nativeElement,
      waveColor: 'rgb(197, 197, 197)',
      progressColor: '#f8400d',
      cursorColor: '#f8400d',
      barWidth: 2,
      barHeight: 1,
      height: this.height,
      backend: useAudioElement ? 'MediaElement' : 'WebAudio',
      plugins: [
        Hover.create({
          lineColor: '#ff0000',
          lineWidth: 2,
          labelBackground: '#555',
          labelColor: '#fff',
          labelSize: '11px',
        }),
      ],
    };

    if (useAudioElement) {
      options.media = this.audioPlayerService.getAudio();
    }

    this.wavesurfer = WaveSurfer.create(options);

    if (!useAudioElement) {
      this.wavesurfer.load(`${TRACK_BASE_URL}/${this.track.fileName}`);
    }
  }

  ngOnDestroy(): void {
    // this.unsubscribeAudioState();
    if (this.wavesurfer) {
      this.wavesurfer.destroy();
    }
  }
}
