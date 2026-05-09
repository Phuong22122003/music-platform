import {
  Component,
  EventEmitter,
  HostListener,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { Track } from '../../../../core/models/track';
import { TrackAndWave } from '../../../../core/models/track_wave';
import { SearchService } from '../../../../core/services/search.service';
import { ActivatedRoute } from '@angular/router';
import { TrackService } from '../../../../core/services/track.service';
import { environment } from '../../../../../environments/environment';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tracks',
  standalone: false,
  templateUrl: './tracks.component.html',
  styleUrl: './tracks.component.scss',
})
export class TracksComponent implements OnInit, OnDestroy {
  tracks: Track[] = [];
  ids!: string[];
  visibleIds!: string[];
  currentId: number = 0;
  itemsPerLoad: number = 10;
  baseUrl = environment.apiBaseUrl;
  isLoading = false;
  sub!: Subscription;
  constructor(
    private searchService: SearchService,
    private trackService: TrackService,
    private route: ActivatedRoute
  ) {}
  search(query: string) {
    this.searchService.searchTrackIds(query).subscribe((ids) => {
      this.ids = ids;
      console.log(ids);
      this.visibleIds = [];
      this.loadMore();
    });
  }
  loadMore() {
    if (this.isLoading || this.currentId >= this.ids.length) return;
    this.isLoading = true;
    console.log('load more....');
    const nextItems = this.ids.slice(
      this.currentId,
      this.currentId + this.itemsPerLoad
    );

    this.visibleIds.push(...nextItems); // nối thêm phần tử
    this.currentId += this.itemsPerLoad;
    if (nextItems.length > 0) {
      this.trackService.getTracksByIds(nextItems).subscribe((tracks) => {
        for (let track of tracks.data) {
          this.tracks.push(track);
        }
        this.isLoading = false;
      });
    } else {
      this.isLoading = false;
    }
  }
  ngOnInit(): void {
    this.sub = this.route.queryParams.subscribe((params) => {
      const query = params['q'];
      this.searchService.emitSearchChange(query);
      this.search(query);
    });
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
