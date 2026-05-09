import {
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { Playlist } from '../../../../../core/models/seach/playlist';
import { Track } from '../../../../../core/models/track';
import { PlaylistComponent } from '../../playlists/playlist/playlist.component';
import { TrackInfoAndWaveComponent } from '../../../../../shared/components/track-info-and-wave/track-info-and-wave.component';
import { Album } from '../../../../../core/models/seach/album';
import { TrackAndWave } from '../../../../../core/models/track_wave';

@Component({
  selector: 'app-album',
  standalone: false,
  templateUrl: './album.component.html',
  styleUrl: './album.component.scss',
})
export class AlbumComponent {
  @Input() album!: Album;
  @Output() play = new EventEmitter<AlbumComponent>();
  currentAlbum!: TrackAndWave;
  isPlaying = false;

  @ViewChild('trackComponent') trackInfoAndWave!: TrackInfoAndWaveComponent;
  ngOnInit(): void {
    this.currentAlbum = this.album.tracks[0];
  }
  togglePlay(isPlaying: boolean) {
    this.isPlaying = isPlaying;
    this.play.emit(this);
  }
  playNext(track: TrackAndWave) {
    // this.togglePlay(true);
    // this.trackInfoAndWave.changeUrl(track.file_path);
    // this.currentAlbum = track;
  }
  stopPlay() {
    // this.trackInfoAndWave.pause();
    this.isPlaying = false;
  }
}
