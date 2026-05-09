import { Component, input, Input, OnInit } from '@angular/core';
import { FollowService } from '../../../core/services/follow.service';
import { AuthService } from '../../../core/services/auth-service';

@Component({
  selector: 'app-user-card',
  standalone: false,
  templateUrl: './user-card.component.html',
  styleUrl: './user-card.component.scss',
})
export class UserCardComponent implements OnInit {
  @Input() avatar: string = '/assets/images/image.png';
  @Input() name!: string;
  @Input() followers!: number;
  @Input() tracks!: number;
  @Input({ required: true }) userId: string = '';
  loggedUserId = '';
  isFollow = false;
  constructor(
    private followService: FollowService,
    private authService: AuthService
  ) {
    // this.name='abc'
    // this.followers=100;
    // this.tracks=100;
  }
  ngOnInit(): void {
    this.loggedUserId = this.authService.getUserId() || '';

    // Tránh gọi khi userId chưa được gán
    if (!this.userId || this.userId === this.loggedUserId) return;
    this.followService
      .isFollowing(this.loggedUserId, this.userId)
      .subscribe((res) => {
        this.isFollow = res.data;
      });
  }
  toggleFollow(): void {
    if (!this.userId) return;

    const action = this.isFollow
      ? this.followService.unfollowUser(this.userId)
      : this.followService.followUser({ followingId: this.userId });

    action.subscribe({
      next: () => {
        this.isFollow = !this.isFollow;
        this.followers += this.isFollow ? 1 : -1;
      },
      error: (err) => {
        console.error('Follow action failed:', err);
      },
    });
  }
}
