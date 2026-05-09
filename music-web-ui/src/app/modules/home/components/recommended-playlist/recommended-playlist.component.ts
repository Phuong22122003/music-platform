import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { PlayList, RelatedTrack } from '../../../../core/models/playlist';
import { Track } from '../../../../core/models/track';
import { RecommendedService } from '../../../../core/services/recommend_service';
import { createPlaceholders } from '../../../../shared/utils/helper';

@Component({
  selector: 'app-recommended-playlist',
  standalone: false,
  templateUrl: './recommended-playlist.component.html',
  styleUrl: './recommended-playlist.component.scss',
})
export class RecommendedPlaylistComponent implements OnInit {
  playlists: Track[] = [];
  @ViewChild('playlistsCard', { static: true }) playlistsCard!: ElementRef;

  constructor(private recommendService: RecommendedService) {}

  ngOnInit(): void {
    this.loadRecommendedTracks();
  }

  loadRecommendedTracks() {
    this.recommendService.getMoreOfWhatYouLike().subscribe({
      next: (res) => {
        this.playlists = res.data;
      },
      error: (err) => {
        console.error('Error loading recommended tracks:', err);
      },
    });
  }

  onTrackStateChange(event: { trackId: string; isLiked: boolean }) {
    // When a track is unliked, remove it from the list
    if (!event.isLiked) {
      this.playlists = this.playlists.filter(
        (track) => track.id !== event.trackId
      );
    }
    // When a track is liked, refresh the recommendations
    else {
      this.loadRecommendedTracks();
    }
  }

  scollLeft() {
    this.playlistsCard.nativeElement.scrollBy({
      left: -200 * 5,
      behavior: 'smooth',
    });
  }

  scollRight() {
    this.playlistsCard.nativeElement.scrollBy({
      left: 200 * 5,
      behavior: 'smooth',
    });
  }

  mockData() {
    const mock_data: RelatedTrack[] = [];
    // for (let i = 0; i < 9; i++) {
    //   mock_data.push({
    //     playlistId: `${i}`,
    //     trackName: `Track ${i}`,
    //     trackPath: `/assets/images/image.png`,
    //     authors: 'LynkNguyen, DenVau, abc,',
    //   });
    // }
    return mock_data;
  }

  get placeholders(): any[] {
    return createPlaceholders(this.playlists);
  }
}
