import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { FollowService } from '../../../../core/services/follow.service';
import { UserProfile } from '../../../../core/models/user_profile';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../../core/services/auth-service';
import { TrackStatisticsService } from '../../../../core/services/track-statistic.service';
import { TrackService } from '../../../../core/services/track.service';
import { Track } from '../../../../core/models/track';
import getAvatarUrl from '../../../../shared/utils/get-avatar-url';
import { UiNotificationService } from '../../../../core/services/ui-notification.service';
@Component({
  selector: 'app-stat-bar',
  standalone: false,
  templateUrl: './stat-bar.component.html',
  styleUrl: './stat-bar.component.scss',
})
export class StatBarComponent implements OnInit, OnChanges {
  @Input() userId: string = '';
  userProfile?: UserProfile;
  followings: UserProfile[] = [];
  followers: UserProfile[] = [];
  tracks: Track[] = [];
  avatarUrl = '';
  isOwner = false;
  constructor(
    private followService: FollowService,
    private toast: ToastrService,
    private authService: AuthService,
    private trackService: TrackService,
    private uiNotification: UiNotificationService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (this.userId) {
      this.loadFollowData();
    }

    this.route.params.subscribe((params) => {
      this.isOwner = this.authService.getUserId() === params['userId'];
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userId'] && !changes['userId'].firstChange) {
      this.loadFollowData();
    }
  }

  loadFollowData(): void {
    forkJoin({
      followings: this.followService.getFollowings(this.userId, 0, 5),
      followers: this.followService.getFollowers(this.userId, 0, 5),
      tracks: this.trackService.getTracksByUserId(this.userId),
    }).subscribe({
      next: ({ followings, followers, tracks }) => {
        this.followings = followings.data.content;
        this.followers = followers.data.content;
        this.tracks = tracks.data;
      },
      error: (err) => {
        this.toast.error('Fail to load follow data: ' + err.message, 'Error');
      },
    });
  }

  unfollow(userId: string): void {
    this.followService.unfollowUser(userId).subscribe(() => {
      this.followings = this.followings.filter(
        (user) => user.userId !== userId
      );
    });
  }

  trackByUserId(index: number, user: UserProfile): string {
    return user.userId; // dùng userId thay vì email nếu có thể
  }

  getAvatarUrl(avatar: string | null) {
    return getAvatarUrl(avatar as string, null);
  }
  showComingSoon() {
    this.uiNotification.showComingSoon();
  }
}
