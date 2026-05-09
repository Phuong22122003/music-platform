import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

import { FollowService } from '../../../../core/services/follow.service';
import { AuthService } from '../../../../core/services/auth-service';

@Component({
  selector: 'app-panel-tab',
  standalone: false,
  templateUrl: './panel-tab.component.html',
  styleUrl: './panel-tab.component.scss',
})
export class PanelTabComponent implements OnInit, OnChanges {
  @Output() openEditProfile = new EventEmitter<void>();
  @Input() userId!: string;
  loginUserId!: string;
  isFollowing: boolean = false;

  constructor(
    private authService: AuthService,
    private followService: FollowService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loginUserId = this.authService.getUserId() as string;
    this.checkFollowing();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userId'] && this.userId && this.loginUserId) {
      this.checkFollowing();
    }
  }

  checkFollowing() {
    this.followService.isFollowing(this.loginUserId, this.userId).subscribe({
      next: (res) => {
        console.log(res);
        this.isFollowing = res.data;
      },
      error: (err) => {
        console.error('Error checking follow status:', err);
      },
    });
  }

  onOpenEditProfile() {
    this.openEditProfile.emit();
  }
  onFollow() {
    this.followService
      .followUser({ followingId: this.userId })
      .subscribe(() => {
        this.isFollowing = true;
      });
  }

  onUnFollow() {
    this.followService.unfollowUser(this.userId).subscribe(() => {
      this.isFollowing = false;
    });
  }
}
