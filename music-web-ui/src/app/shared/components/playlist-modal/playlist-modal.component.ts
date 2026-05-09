import { Component, Input, OnInit } from '@angular/core';
import {
  trigger,
  style,
  animate,
  transition,
  state,
} from '@angular/animations';
import { Track } from '../../../core/models/track';
import { PlaylistService } from '../../../core/services/playlist_service';
import {
  AddPlaylistRequest,
  AddPlaylistTrackRequest,
  UpdatePlaylistInfoRequest,
} from '../../../core/models/playlist/playlist_request.model';
import {
  PlaylistResponse,
  PlaylistTypeResponse,
} from '../../../core/models/playlist/playlist.model';
import { ToastrService } from 'ngx-toastr';
import { TrackList } from '../../../core/models/tracklist';
import { AuthService } from '../../../core/services/auth-service';
import { environment } from '../../../../environments/environment';
import { COVER_BASE_URL } from '../../utils/url';

@Component({
  selector: 'app-playlist-modal',
  standalone: false,
  templateUrl: './playlist-modal.component.html',
  styleUrl: './playlist-modal.component.scss',
  animations: [
    trigger('slideModal', [
      state('void', style({ transform: 'translateY(-100%)', opacity: 0 })),
      state('visible', style({ transform: 'translateY(0)', opacity: 1 })),
      transition('void => visible', [animate('300ms ease-out')]),
      transition('visible => void', [animate('300ms ease-in')]),
    ]),
  ],
})
export class PlaylistModalComponent {
  @Input({ required: true }) trackIds!: string[];
  activeTab: 'add' | 'create' = 'add';
  isVisible = false; // điều khiển *ngIf
  animationState: 'visible' | 'void' = 'void'; // điều khiển animation
  privacy: string = 'PUBLIC'; // hoặc 'PRIVATE' nếu mặc định là private
  title: string = '';
  playlists: TrackList[] = [];
  isLikes: boolean[] = [];
  coverUrl = COVER_BASE_URL;
  constructor(
    private playlistService: PlaylistService,
    private toast: ToastrService,
    private authService: AuthService
  ) {}

  show() {
    this.isVisible = true;
    this.animationState = 'visible';
    this.fetchPlaylist();
  }

  hide() {
    this.animationState = 'void'; // chạy hiệu ứng
  }

  onAnimationDone(event: any) {
    if (event.toState === 'void') {
      this.isVisible = false;
    }
  }

  switchTab(tab: 'add' | 'create', event: Event) {
    event.stopPropagation();
    this.activeTab = tab;
  }

  onCreateNewPlaylist() {
    const request: AddPlaylistRequest = {
      privacy: this.privacy,
      title: this.title,
      trackIds: this.trackIds,
    };

    this.playlistService.addPlaylist(request).subscribe({
      next: (res) => {
        this.toast.success('Playlist created successfully');
        this.fetchPlaylist(); // reload danh sách nếu cần
        this.title = ''; // reset title
        this.activeTab = 'add'; // chuyển về tab add
      },
      error: (err) => {
        this.toast.error('Failed to create playlist');
        console.error(err);
      },
    });
  }

  fetchPlaylist() {
    if (!this.authService.getUserId()) return;
    this.playlistService
      .getAllPlaylists(this.authService.getUserId() as string)
      .subscribe((res) => {
        console.log(res);
        this.playlists = [...res.data];
      });
  }

  addTrackToPlayList(playlistId: string, index: number) {
    console.log(this.coverUrl, '====');
    const currentPlaylist = this.playlists[index];
    const request: UpdatePlaylistInfoRequest = {
      id: playlistId,
      title: currentPlaylist.listname,
      privacy: currentPlaylist.privacy,
      trackIds: Array.from(
        new Set([
          ...currentPlaylist.tracks.map((track) => track.id),
          ...this.trackIds,
        ])
      ),
      releaseDate: currentPlaylist.releaseDate,
      description: currentPlaylist.description,
      genreId: currentPlaylist.genre?.id || undefined,
      tagIds: currentPlaylist.tags.map((tag) => tag.id) || [],
    };

    this.playlistService.updatePlaylistInfo(null, request).subscribe({
      next: (res) => {
        this.fetchPlaylist();
        this.toast.success('Track added to playlist');
      },
      error: (err) => {
        this.toast.error('Failed to add track to playlist');
        console.error(err);
      },
    });
  }
}
