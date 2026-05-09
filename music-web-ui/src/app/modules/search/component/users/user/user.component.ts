import { Component, Input, OnInit } from '@angular/core';
import { User } from '../../../../../core/models/seach/user';
import { UserProfile } from '../../../../../core/models/user_profile';
import { AVATAR_BASE_URL } from '../../../../../shared/utils/url';
import { FollowService } from '../../../../../core/services/follow.service';
import { AuthService } from '../../../../../core/services/auth-service';
import { ProfileService } from '../../../../../core/services/profile.service';

@Component({
  selector: 'app-user',
  standalone: false,
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss',
})
export class UserComponent implements OnInit {
  isFollowing = false;
  avatarUrl = AVATAR_BASE_URL;
  loggedUserId = '';
  isMySelf = false;
  countFollow = 0;
  @Input() user!: UserProfile;
  constructor(
    private followService: FollowService,
    private authService: AuthService
  ) {}
  ngOnInit(): void {
    this.loggedUserId = this.authService.getUserId() || '';
    this.isMySelf = this.loggedUserId === this.user.userId;
    this.countFollow = this.user.followerCount || 0;
  }

  toggleFollow() {
    if (!this.loggedUserId) return;

    const action = this.isFollowing
      ? this.followService.unfollowUser(this.user.userId)
      : this.followService.followUser({ followingId: this.user.userId });

    action.subscribe((res) => {
      if (this.isFollowing) {
        this.countFollow--;
      } else {
        this.countFollow++;
      }
      this.user.followerCount = this.countFollow;
      this.isFollowing = !this.isFollowing;
    });
  }
}
