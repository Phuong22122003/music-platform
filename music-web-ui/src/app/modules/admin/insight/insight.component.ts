import { Component, OnInit } from '@angular/core';
import { TrackStatisticsService } from '../../../core/services/track-statistic.service';
import { Color, ScaleType } from '@swimlane/ngx-charts';
import { formatDate, getLast7Days } from '../../../shared/utils/helper';
import { UserProfile } from '../../../core/models/user_profile';
import { ProfileService } from '../../../core/services/profile.service';
import { AVATAR_BASE_URL, COVER_BASE_URL } from '../../../shared/utils/url';
import {
  TopTrack,
  UserStatistic,
} from '../../../core/models/statistic/statistic.model';
import { FollowService } from '../../../core/services/follow.service';

@Component({
  selector: 'app-insight',
  standalone: false,
  templateUrl: './insight.component.html',
  styleUrl: './insight.component.scss',
})
export class InsightComponent implements OnInit {
  constructor(
    private statisticService: TrackStatisticsService,
    private profileService: ProfileService,
    private statisticsService: TrackStatisticsService
  ) {}
  coverUrl = COVER_BASE_URL;
  data: any = [];
  staticBy: string = '';
  userProfiles: UserProfile[] = [];

  // Define different color schemes for each type of statistic
  playColorScheme: Color = {
    name: 'playScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1DB954', '#1ed760', '#1fdf64'], // Spotify-like green shades
  };

  likeColorScheme: Color = {
    name: 'likeScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#E0245E', '#F91880', '#FF69B4'], // Twitter-like red/pink shades
  };

  commentColorScheme: Color = {
    name: 'commentScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1DA1F2', '#0D8EDB', '#0B7BC0'], // Twitter-like blue shades
  };

  colorScheme: Color = this.playColorScheme; // Default color scheme

  rangeOptions = [
    { label: 'Today', value: 'today' },
    { label: 'Last 7 Days', value: 'last7' },
    { label: 'Last 30 Days', value: 'last30' },
    { label: 'Custom Range', value: 'custom' },
  ];
  avatarUrl = AVATAR_BASE_URL;
  selectedRange = 'last7';
  customFromDate: string = '';
  customToDate: string = '';
  selectedType: 'play' | 'like' | 'comment' = 'play';
  topTracks: TopTrack[] = [];
  topUser: UserStatistic[] = [];

  selectType(type: 'play' | 'like' | 'comment') {
    if (type === this.selectedType) return;
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
    this.selectedRange = 'last7';
    this.loadData(this.selectedRange, this.selectedType);
  }
  onRangeChange() {
    if (this.selectedRange !== 'custom') {
      this.loadData(this.selectedRange, this.selectedType);
    }
  }
  onCustomRangeApply() {
    if (!this.customFromDate || !this.customToDate) return;
    this.loadData('custom', this.selectedType);
  }
  loadData(range: string, type: 'play' | 'like' | 'comment') {
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

    switch (type) {
      case 'play':
        this.statisticService
          .getPlayResponse(fromDate, toDate)
          .subscribe((res) => {
            this.profileService
              .getProfileByIds(
                res.data.topListenerIds.map((topListener) => topListener.userId)
              )
              .subscribe((profilesRes) => {
                this.userProfiles = profilesRes.data;
                console.log(profilesRes);
              });
            this.topUser = res.data.topListenerIds;
            this.data = res.data.dailyPlays.map((dailyPlay) => {
              return {
                name: dailyPlay.date,
                value: dailyPlay.playCount,
              };
            });
          });
        break;
      case 'comment':
        this.statisticService
          .getCommentStatisticResponse(fromDate, toDate)
          .subscribe((res) => {
            this.profileService
              .getProfileByIds(
                res.data.whoCommented.map(
                  (userCommented) => userCommented.userId
                )
              )
              .subscribe((profilesRes) => {
                this.userProfiles = profilesRes.data;
              });
            this.topUser = res.data.whoCommented;
            this.data = res.data.dailyComments.map((dailyComment) => {
              return {
                name: dailyComment.date,
                value: dailyComment.commentCount,
              };
            });
          });
        break;
      case 'like':
        this.statisticService
          .getLikeResponse(fromDate, toDate)
          .subscribe((res) => {
            console.log(res);
            this.profileService
              .getProfileByIds(
                res.data.whoLiked.map((userLiked) => userLiked.userId)
              )
              .subscribe((profilesRes) => {
                this.userProfiles = profilesRes.data;
              });
            this.topUser = res.data.whoLiked;
            this.data = res.data.dailyLikes.map((dailyLikes) => {
              return {
                name: dailyLikes.date,
                value: dailyLikes.likedCount,
              };
            });
          });
        break;
    }
    this.getTopTracks(fromDate, toDate);
  }
  ngOnInit(): void {
    this.loadData(this.selectedRange, this.selectedType);
  }
  formatValueLabel(value: number): string {
    return value > 1 ? `${value} Plays` : `${value} Play`;
  }
  getDisplayText() {
    if (this.selectedRange === 'custom') {
      return `From ${this.customFromDate} to ${this.customToDate}`;
    } else {
      let label = '';
      switch (this.selectedRange) {
        case 'today':
          label = 'Today';
          break;
        case 'last7':
          label = 'Last 7 days';
          break;
        case 'last30':
          label = 'Last 30 days';
          break;
        default:
          label = '';
          break;
      }
      return label;
    }
  }

  getTopTracks(fromDate: string | null, toDate: string | null) {
    this.statisticsService
      .getAllTopTracks(fromDate, toDate)
      .subscribe((res) => {
        console.log(res);
        this.topTracks = res.data;
      });
  }
}
