import {
  Component,
  HostListener,
  OnInit,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { TrackList } from '../../../../core/models/tracklist';
import { PlaylistService } from '../../../../core/services/playlist_service';
import { SearchService } from '../../../../core/services/search.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-playlists',
  standalone: false,
  templateUrl: './playlists.component.html',
  styleUrls: ['./playlists.component.scss'],
})
export class PlaylistsComponent implements OnInit {
  playlists: TrackList[] = [];
  ids!: string[];
  visibleIds!: string[];
  currentId: number = 0;
  itemsPerLoad: number = 10;
  isLoading = false;
  sub!: Subscription;

  constructor(
    private playlistService: PlaylistService,
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
    this.searchService.searchPlaylistIds(query).subscribe((ids) => {
      this.ids = ids;
      console.log(ids);
      this.visibleIds = [];
      this.loadMore();
    });
  }

  loadMore(): void {
    if (this.isLoading || this.currentId >= this.ids.length) return;
    this.isLoading = true;
    console.log('Load more playlists...');
    const nextItems = this.ids.slice(
      this.currentId,
      this.currentId + this.itemsPerLoad
    );
    this.visibleIds.push(...nextItems);
    this.currentId += this.itemsPerLoad;
    if (nextItems.length > 0) {
      this.playlistService
        .getPlaylistsByIds(nextItems)
        .subscribe((playlists) => {
          this.playlists.push(...playlists.data);
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
