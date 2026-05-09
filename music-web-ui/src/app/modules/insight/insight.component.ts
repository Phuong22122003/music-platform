import { Component, OnInit } from '@angular/core';
import { TrackStatisticsService } from '../../core/services/track-statistic.service';
import { AVATAR_BASE_URL, COVER_BASE_URL } from '../../shared/utils/url';
import { UserProfile } from '../../core/models/user_profile';
import { Color, ScaleType } from '@swimlane/ngx-charts';
import { formatDate } from '../../shared/utils/helper';
import { ProfileService } from '../../core/services/profile.service';
import { TopTrack } from '../../core/models/statistic/statistic.model';
import { AuthService } from '../../core/services/auth-service';

@Component({
  selector: 'app-insight',
  standalone: false,
  templateUrl: './insight.component.html',
  styleUrl: './insight.component.scss',
})
export class InsightComponent implements OnInit {
  selectedType: 'play' | 'like' | 'comment' = 'play';
  selectedRange = 'last7';
  customFromDate: string = '';
  customToDate: string = '';
  avatarUrl = AVATAR_BASE_URL;
  coverUrl = COVER_BASE_URL;
  userProfiles: UserProfile[] = [];
  topUser: { userId: string; count: number }[] = [];
  topTracks: TopTrack[] = [];
  data: any[] = [];

  rangeOptions = [
    { label: 'Today', value: 'today' },
    { label: 'Last 7 Days', value: 'last7' },
    { label: 'Last 30 Days', value: 'last30' },
    { label: 'Custom Range', value: 'custom' },
  ];

  // Color schemes for different types of statistics
  playColorScheme: Color = {
    name: 'play',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1DB954', '#1ed760', '#1fdf64', '#52BE80', '#82E0AA'], // Spotify greens
  };

  likeColorScheme: Color = {
    name: 'like',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#E0245E', '#F91880', '#FF69B4', '#FF1493', '#C71585'], // Pink shades
  };

  commentColorScheme: Color = {
    name: 'comment',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1DA1F2', '#0D8EDB', '#0B7BC0', '#2E86C1', '#3498DB'], // Twitter blues
  };

  colorScheme: Color = this.playColorScheme;

  constructor(
    private statisticsService: TrackStatisticsService,
    private profileService: ProfileService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadData(this.selectedRange);
  }

  selectType(type: 'play' | 'like' | 'comment') {
    this.selectedType = type;
    // Update color scheme based on selected type
    switch (type) {
      case 'play':
        this.colorScheme = this.playColorScheme;
        break;
      case 'like':
        this.colorScheme = this.likeColorScheme;
        break;
      case 'comment':
        this.colorScheme = this.commentColorScheme;
        break;
    }
    this.loadData(this.selectedRange);
  }

  onRangeChange() {
    if (this.selectedRange !== 'custom') {
      this.loadData(this.selectedRange);
    }
  }

  onCustomRangeApply() {
    if (!this.customFromDate || !this.customToDate) return;
    this.loadData('custom');
  }

  loadData(range: string): void {
    const today = new Date();
    let from: Date;
    let to: Date = today;

    switch (range) {
      case 'today':
        from = new Date(today);
        break;
      case 'last7':
        from = new Date();
        from.setDate(today.getDate() - 6);
        break;
      case 'last30':
        from = new Date();
        from.setDate(today.getDate() - 29);
        break;
      case 'custom':
        from = new Date(this.customFromDate);
        to = new Date(this.customToDate);
        break;
      default:
        return;
    }

    const fromDate = formatDate(from);
    const toDate = formatDate(to);

    const handleUserProfiles = (userIds: string[]) => {
      this.profileService.getProfileByIds(userIds).subscribe((profileRes) => {
        this.userProfiles = profileRes.data;
      });
    };

    switch (this.selectedType) {
      case 'play':
        this.statisticsService
          .getPlayResponseUser(fromDate, toDate)
          .subscribe((res) => {
            if (res.data) {
              this.data = res.data.dailyPlays.map((play) => ({
                name: play.date,
                value: play.playCount,
              }));
              this.topUser = res.data.topListenerIds;
              const userIds = res.data.topListenerIds.map((u) => u.userId);
              handleUserProfiles(userIds);
            }
          });
        break;

      case 'like':
        this.statisticsService
          .getLikeStatisticUserResponse(fromDate, toDate)
          .subscribe((res) => {
            if (res.data) {
              this.data = res.data.dailyLikes.map((like) => ({
                name: like.date,
                value: like.likedCount,
              }));
              this.topUser = res.data.whoLiked;
              const userIds = res.data.whoLiked.map((u) => u.userId);
              handleUserProfiles(userIds);
            }
          });
        break;

      case 'comment':
        this.statisticsService
          .getCommentStatisticUserResponse(fromDate, toDate)
          .subscribe((res) => {
            if (res.data) {
              this.data = res.data.dailyComments.map((comment) => ({
                name: comment.date,
                value: comment.commentCount,
              }));
              this.topUser = res.data.whoCommented;
              const userIds = res.data.whoCommented.map((u) => u.userId);
              handleUserProfiles(userIds);
            }
          });
        break;
    }
    this.getTopTracks(fromDate, toDate);
  }

  dataLabelFormatter = (value: number) => {
    return `${value} ${this.selectedType}`;
  };

  getDisplayText(): string {
    if (this.selectedRange === 'custom') {
      return `From ${this.customFromDate} to ${this.customToDate}`;
    }

    switch (this.selectedRange) {
      case 'today':
        return 'Today';
      case 'last7':
        return 'Last 7 days';
      case 'last30':
        return 'Last 30 days';
      default:
        return '';
    }
  }
  getTopTracks(fromDate: string | null, toDate: string | null) {
    const userId = this.authService.getUserId();
    if (!userId) return;
    this.statisticsService
      .getUserTopTracks(userId, fromDate, toDate)
      .subscribe((res) => {
        this.topTracks = res.data;
      });
  }
}
