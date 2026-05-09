import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { UserProfile } from '../../../core/models/user_profile';
import { ProfileService } from '../../../core/services/profile.service';
import { FollowService } from '../../../core/services/follow.service';
import { AuthService } from '../../../core/services/auth-service';
import { AVATAR_BASE_URL } from '../../utils/url';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-following-circle',
  standalone: false,
  templateUrl: './following-circle.component.html',
  styleUrl: './following-circle.component.scss',
})
export class FollowingCircleComponent implements OnInit, OnDestroy {
  @Input({ required: true }) profile!: UserProfile;
  avatarUrl = AVATAR_BASE_URL;
  isFollow = false;
  countFollow = 0;
  loggedUserId!: string | null;
  private followChangeSubscription?: Subscription;

  constructor(
    private profileService: ProfileService,
    private followService: FollowService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.countFollow = this.profile.followerCount || 0;
    this.loggedUserId = this.authService.getUserId();
    if (!this.loggedUserId) return;

    this.followService
      .isFollowing(this.loggedUserId, this.profile.userId)
      .subscribe((res) => {
        this.isFollow = res.data;
      });

    this.followChangeSubscription = this.followService.followChange$.subscribe(
      (change) => {
        if (
          change.userId === this.profile.userId &&
          change.initiatorId !== this.loggedUserId
        ) {
          if (change.action === 'follow') {
            this.countFollow++;
            this.isFollow = true;
          } else {
            this.countFollow--;
            this.isFollow = false;
          }
          this.profile.followerCount = Math.max(this.countFollow, 0);
        }
      }
    );
  }

  toggleFollow() {
    if (!this.loggedUserId) return;

    const action = this.isFollow
      ? this.followService.unfollowUser(this.profile.userId, this.loggedUserId)
      : this.followService.followUser({
          followingId: this.profile.userId,
          initiatorId: this.loggedUserId,
        });

    action.subscribe(() => {
      this.isFollow = !this.isFollow;
      this.countFollow = this.isFollow
        ? this.countFollow + 1
        : this.countFollow - 1;
      this.profile.followerCount = Math.max(this.countFollow, 0);
    });
  }

  ngOnDestroy() {
    if (this.followChangeSubscription) {
      this.followChangeSubscription.unsubscribe();
    }
  }
}
