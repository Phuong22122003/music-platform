import { Component, OnInit } from '@angular/core';
import { TrackList } from '../../../core/models/tracklist';
import { AlbumService } from '../../../core/services/album.service';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../core/services/auth-service';
import { createPlaceholders } from '../../../shared/utils/helper';

@Component({
  selector: 'app-albums',
  standalone: false,
  templateUrl: './albums.component.html',
  styleUrl: './albums.component.scss',
})
export class AlbumsComponent implements OnInit {
  options = ['All', 'Created', 'Liked'];
  selectedOption: string = 'All';
  albums: TrackList[] = [];
  searchKeyword: string = '';
  userId = '';

  constructor(
    private albumService: AlbumService,
    private authService: AuthService
  ) {
    this.userId = this.authService.getUserId() || '';
  }

  ngOnInit(): void {
    if (!this.userId) return;
    this.loadAlbums();
  }

  loadAlbums() {
    if (this.selectedOption === 'All') {
      // Nếu cần All = Created + Liked
      this.loadAllAlbums();
    } else if (this.selectedOption === 'Created') {
      let data: any = [];
      this.albumService.getAlbumsByUserId(this.userId).subscribe((res) => {
        data = res.data;
        this.albums = this.filterBySearch(data || []);
      });
      if (data.length === 0) this.albums = data;
    } else if (this.selectedOption === 'Liked') {
      this.albumService.getLikedAlbums(this.userId).subscribe((res) => {
        this.albums = this.filterBySearch(res.data || []);
      });
    }
  }

  loadAllAlbums() {
    this.albumService.getCreatedAndLikedAlbums(this.userId).subscribe((res) => {
      this.albums = this.filterBySearch(res.data || []);
    });
  }

  filterBySearch(data: TrackList[]): TrackList[] {
    if (!this.searchKeyword.trim()) return data;
    return data.filter(
      (album) =>
        album.listname
          .toLowerCase()
          .includes(this.searchKeyword.toLowerCase()) ||
        (album.displayName || '')
          .toLowerCase()
          .includes(this.searchKeyword.toLowerCase())
    );
  }

  onSearch(event: any) {
    this.searchKeyword = event.target.value;
    this.loadAlbums(); // Gọi lại API và search luôn
  }

  onOptionChange() {
    this.loadAlbums(); // Khi đổi selection, load API mới
  }
  unlike(id: string) {
    this.albums = this.albums.filter((album) => album.listId !== id);
  }
  get placeholders(): any[] {
    return createPlaceholders(this.albums);
  }
}
