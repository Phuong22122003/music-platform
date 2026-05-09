import { Component, OnInit } from '@angular/core';
import { Track } from '../../../core/models/track';
import { HistoryService } from '../../../core/services/history.service';
import { TrackService } from '../../../core/services/track.service';
import { ProfileService } from '../../../core/services/profile.service';
import { TrackStatisticsService } from '../../../core/services/track-statistic.service';
import { forkJoin } from 'rxjs';
import { RecommendedPlaylistComponent } from '../../../modules/home/components/recommended-playlist/recommended-playlist.component';
import { RecommendedService } from '../../../core/services/recommend_service';
import { UserProfile } from '../../../core/models/user_profile';
import { AVATAR_BASE_URL } from '../../utils/url';

@Component({
  selector: 'app-side-bar',
  standalone: false,
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.scss',
})
export class SideBarComponent implements OnInit {
  soundCards: any[] = [];
  profiles: UserProfile[] = [];
  avatarUrl = AVATAR_BASE_URL;
  constructor(
    private historyService: HistoryService,
    private trackService: TrackService,
    private profileService: ProfileService,
    private trackStatisticsService: TrackStatisticsService,
    private recommendService: RecommendedService
  ) {}
  ngOnInit(): void {
    this.historyService.getAllHistory().subscribe((res) => {
      this.soundCards = res.data.slice(0, 3);
      const observables = this.soundCards.map((track) =>
        forkJoin({
          playCount: this.trackStatisticsService.getPlayCount(track.id),
          commentCount: this.trackStatisticsService.getCommentCountForTrack(
            track.id
          ),
          likeCount: this.trackStatisticsService.getTrackLikeCount(track.id),
        })
      );

      forkJoin(observables).subscribe((results) => {
        results.forEach((result, index) => {
          const track = this.soundCards[index];
          track.playCount = result.playCount.data;
          track.commentCount = result.commentCount.data;
          track.likeCount = result.likeCount.data;
        });
      });
    });
    this.recommendService.getArtistYouShouldKnow().subscribe((res) => {
      this.profiles = res.data;
    });
  }
}
