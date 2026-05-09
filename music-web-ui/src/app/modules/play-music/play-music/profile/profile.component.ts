import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { UserProfile } from '../../../../core/models/user_profile';
import { AuthService } from '../../../../core/services/auth-service';
import { environment } from '../../../../../environments/environment';
import getAvatarUrl from '../../../../shared/utils/get-avatar-url';
import { FollowService } from '../../../../core/services/follow.service';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit, OnChanges {
  @Input('profile') profile!: UserProfile;
  avatarBaseUrl: string = environment.fileApi + '/images/avatars';
  avatarUrl!: string;
  loggedUserId!: string | null;
  isFollow: boolean = false;
  constructor(
    private authService: AuthService,
    private followService: FollowService
  ) {}

  ngOnInit(): void {
    this.loggedUserId = this.authService.getUserId();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['profile'] && this.profile) {
      this.avatarUrl = getAvatarUrl(
        this.profile.avatar as string,
        this.avatarBaseUrl
      );
      if (!this.loggedUserId) return;
      this.followService
        .isFollowing(this.loggedUserId, this.profile.userId)
        .subscribe((res) => {
          this.isFollow = res.data;
        });
    }
  }

  onFollow() {
    this.followService
      .followUser({ followingId: this.profile.userId })
      .subscribe((res) => {
        this.isFollow = true;
        if (typeof this.profile.followerCount === 'number') {
          this.profile.followerCount++;
        }
      });
  }

  onUnfollow() {
    this.followService.unfollowUser(this.profile.userId).subscribe((res) => {
      this.isFollow = false;
      if (typeof this.profile.followerCount === 'number') {
        this.profile.followerCount--;
      }
    });
  }
}
