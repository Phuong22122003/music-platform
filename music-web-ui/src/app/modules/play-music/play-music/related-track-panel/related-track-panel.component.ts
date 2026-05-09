import { Component, Input, OnInit } from '@angular/core';
import { TrackService } from '../../../../core/services/track.service';
import { Track } from '../../../../core/models/track';
import { ProfileService } from '../../../../core/services/profile.service';
import { UserProfile } from '../../../../core/models/user_profile';
import { TrackStatisticsService } from '../../../../core/services/track-statistic.service';
import { forkJoin } from 'rxjs';
import { LikedTrackService } from '../../../../core/services/liked-track.service';
import { environment } from '../../../../../environments/environment';
import { UiNotificationService } from '../../../../core/services/ui-notification.service';
@Component({
  selector: 'app-related-track-panel',
  standalone: false,
  templateUrl: './related-track-panel.component.html',
  styleUrl: './related-track-panel.component.scss',
})
export class RelatedTrackPanelComponent implements OnInit {
  tracks: any[] = [];
  likedUsers: UserProfile[] = [];
  avatarBaseUrl = environment.fileApi + '/images/avatars';
  @Input({ required: true, alias: 'track' }) currentTrack!: Track;

  constructor(
    private trackService: TrackService,
    private profileService: ProfileService,
    private trackStatisticsService: TrackStatisticsService,
    private likedTrackService: LikedTrackService,
    private uiNotification: UiNotificationService
  ) {}

  ngOnInit(): void {
    console.log(this.currentTrack, '====');
    this.trackService.getRandomTracks(3).subscribe((res) => {
      this.tracks = res.data;

      const observables = this.tracks.map((track) =>
        forkJoin({
          profile: this.profileService.getProfileById(track.userId),
          playCount: this.trackStatisticsService.getPlayCount(track.id),
          commentCount: this.trackStatisticsService.getCommentCountForTrack(
            track.id
          ),
          likeCount: this.trackStatisticsService.getTrackLikeCount(track.id),
        })
      );

      forkJoin(observables).subscribe((results) => {
        results.forEach((result, index) => {
          const track = this.tracks[index];
          track.displayName = result.profile.data.displayName;
          track.playCount = result.playCount.data;
          track.commentCount = result.commentCount.data;
          track.likeCount = result.likeCount.data;
        });
      });
    });

    // Lấy danh sách userIds đã like track và hiển thị tối đa 5 user
    this.likedTrackService
      .getUserIdsLikedTrack(this.currentTrack.id)
      .subscribe((res) => {
        const userIds = res.data.slice(0, 5); // Lấy tối đa 5 userId
        if (userIds.length > 0) {
          this.profileService
            .getProfileByIds(userIds)
            .subscribe((profileRes) => {
              this.likedUsers = profileRes.data;
            });
        }
      });
  }
  showComingSoon() {
    this.uiNotification.showComingSoon();
  }
}
