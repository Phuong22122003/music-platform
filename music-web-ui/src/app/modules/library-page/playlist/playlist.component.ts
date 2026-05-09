import { Component, OnInit } from '@angular/core';
import { RelatedTrack } from '../../../core/models/playlist';
import { PlaylistService } from '../../../core/services/playlist_service';
import { AuthService } from '../../../core/services/auth-service';
import { TrackList } from '../../../core/models/tracklist';
import { LikedPlaylistService } from '../../../core/services/liked_playlist_service';
import { combineLatest, debounceTime, distinctUntilChanged, tap } from 'rxjs';
import { createPlaceholders } from '../../../shared/utils/helper';

@Component({
  selector: 'app-playlist',
  standalone: false,
  templateUrl: './playlist.component.html',
  styleUrl: './playlist.component.scss',
})
export class PlaylistComponent implements OnInit {
  options = ['All', 'Created', 'Liked'];
  selectedOption: string = 'All';
  searchText: string = '';
  playlists: TrackList[] = [];
  allPlaylists: TrackList[] = [];
  userId = '';

  constructor(
    private playlistService: PlaylistService,
    private likedPlaylistService: LikedPlaylistService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.likedPlaylistService.getAllLikedPlaylists().subscribe((res) => {
      console.log(res);
    });
    this.userId = this.authService.getUserId() || '';
    if (!this.userId) return;
    this.loadPlaylists();
  }

  loadPlaylists() {
    if (this.selectedOption === 'All') {
      this.playlistService.getAllYourPlaylists().subscribe((res) => {
        this.allPlaylists = res.data;
        this.filterPlaylists();
      });
    } else if (this.selectedOption === 'Created') {
      this.playlistService.getCreatedPlaylists().subscribe((res) => {
        this.allPlaylists = res.data;
        this.filterPlaylists();
      });
    } else if (this.selectedOption === 'Liked') {
      this.playlistService.getLikedPlaylists().subscribe((res) => {
        this.allPlaylists = res.data;
        this.filterPlaylists();
      });
    }
  }

  onOptionChange() {
    this.loadPlaylists();
  }

  filterPlaylists() {
    const lowerSearch = this.searchText.toLowerCase();
    this.playlists = this.allPlaylists.filter(
      (playlist) =>
        playlist.listname.toLowerCase().includes(lowerSearch) ||
        playlist.displayName?.toLocaleLowerCase().includes(lowerSearch)
    );
  }

  unlike(id: string) {
    this.playlists = this.playlists.filter(
      (playlist) => playlist.listId !== id
    );
  }
  get placeholders(): any[] {
    return createPlaceholders(this.playlists);
  }
}
