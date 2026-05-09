import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { TrackList } from '../../../core/models/tracklist';
import { TrackAndWave } from '../../../core/models/track_wave';
import { TrackInfoAndWaveComponent } from '../track-info-and-wave/track-info-and-wave.component';
import { Track } from '../../../core/models/track';
import { environment } from '../../../../environments/environment';
import { WavesurferComponent } from '../wavesurfer/wavesurfer.component';
import { TrackAction } from '../../../core/models/track_action';
import { openConfirmDialog } from '../../utils/dialog';
import { MatDialog } from '@angular/material/dialog';
import { PlaylistService } from '../../../core/services/playlist_service';
import { ToastrService } from 'ngx-toastr';
import copyLink from '../../utils/copylink';
import { LikedPlaylistService } from '../../../core/services/liked_playlist_service';
import { AlbumService } from '../../../core/services/album.service';
import { AuthService } from '../../../core/services/auth-service';
import { AudioPlayerService } from '../../../core/services/audio-player.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-track-list',
  standalone: false,
  templateUrl: './track-list.component.html',
  styleUrl: './track-list.component.scss',
})
export class TrackListComponent implements OnInit, OnDestroy {
  @Input() tracklist!: TrackList;
  type: string = '';
  @Output() delete = new EventEmitter<string>();

  @ViewChild('trackWave', { static: true })
  trackWave!: WavesurferComponent;
  isPlaying = false;
  currentTrack!: Track;
  currentIndex = 0;
  coverUrl = environment.fileApi + '/images/covers';
  trackUrl = environment.fileApi + '/audios';
  playingSub?: Subscription;

  constructor(
    private playlistService: PlaylistService,
    private albumService: AlbumService,
    private audioPlayerService: AudioPlayerService
  ) {}
  ngOnInit(): void {
    this.currentTrack = this.tracklist.tracks[0];
    this.audioPlayerService.currentTrack$.subscribe((track) => {
      const index = this.tracklist.tracks.findIndex(
        (trackInList) => trackInList.id === track?.id
      );

      if (index >= 0) {
        this.currentIndex = index;
        this.currentTrack = this.tracklist.tracks[index];
        this.subscribeToPlaying();
      } else {
        this.unsubscribePlaying();
        this.isPlaying = false;
      }
    });
  }

  addToNextUp() {}

  playNext(track: Track, index: number) {
    this.currentTrack = track;
    this.currentIndex = index;
    this.togglePlay();
  }
  updatePlay(isPlay: boolean) {
    this.isPlaying = isPlay;
  }
  togglePlay() {
    const currentTrack = this.audioPlayerService.getCurrentTrack();
    const isCurrent = currentTrack?.id === this.currentTrack.id;
    console.log(currentTrack, this.currentTrack);
    if (isCurrent && this.isPlaying) {
      this.audioPlayerService.pause();
    } else {
      this.audioPlayerService.playTrack(this.currentTrack);
      console.log(this.audioPlayerService.getCurrentTrack());
    }
  }

  onDelete(listId: any) {
    this.delete.emit(listId);
  }
  onUpdate() {
    const action =
      this.tracklist.type === 'album'
        ? this.albumService.getAlbumById(this.tracklist.listId)
        : this.playlistService.getPlaylistById(this.tracklist.listId);
    action.subscribe((res) => {
      console.log(res);
      this.tracklist = { ...res.data };
    });
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
