import { Component, HostListener, OnInit } from '@angular/core';
import { TrackList } from '../../../../core/models/tracklist';
import { AlbumService } from '../../../../core/services/album.service';
import { SearchService } from '../../../../core/services/search.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-albums',
  standalone: false,
  templateUrl: './albums.component.html',
  styleUrls: ['./albums.component.scss'],
})
export class AlbumsComponent implements OnInit {
  albums: TrackList[] = [];
  ids!: string[];
  visibleIds!: string[];
  currentId: number = 0;
  itemsPerLoad: number = 10;
  isLoading = false;
  sub!: Subscription;

  constructor(
    private albumService: AlbumService,
    private searchService: SearchService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Lắng nghe sự thay đổi của query trong search service
    this.sub = this.route.queryParams.subscribe((params) => {
      const query = params['q'];
      this.searchService.emitSearchChange(query);
      this.search(query);
    });
  }

  search(query: string): void {
    this.searchService.searchAlbumIds(query).subscribe((ids) => {
      this.ids = ids;
      this.visibleIds = [];
      this.loadMore();
    });
  }

  loadMore(): void {
    if (this.isLoading || this.currentId >= this.ids.length) return;
    this.isLoading = true;
    console.log('Load more albums...');
    const nextItems = this.ids.slice(
      this.currentId,
      this.currentId + this.itemsPerLoad
    );
    this.visibleIds.push(...nextItems);
    this.currentId += this.itemsPerLoad;
    if (nextItems.length > 0) {
      this.albumService.getAlbumsIds(nextItems).subscribe((albums) => {
        this.albums.push(...albums.data);
        this.isLoading = false;
      });
    } else {
      this.isLoading = false;
    }
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const threshold = 300;
    const position = window.innerHeight + window.scrollY;
    const height = document.body.offsetHeight;

    if (position > height - threshold) {
      this.loadMore();
    }
  }

  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
  }
}
