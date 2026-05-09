import { Component, OnInit } from '@angular/core';
import { FollowService } from '../../../core/services/follow.service';
import { UserProfile } from '../../../core/models/user_profile';
import { AuthService } from '../../../core/services/auth-service';
import { createPlaceholders } from '../../../shared/utils/helper';

@Component({
  selector: 'app-following',
  standalone: false,
  templateUrl: './following.component.html',
  styleUrl: './following.component.scss',
})
export class FollowingComponent implements OnInit {
  profiles: UserProfile[] = [];
  searchQuery: string = '';
  constructor(
    private followService: FollowService,
    private authService: AuthService
  ) {}
  ngOnInit(): void {
    let loggedUserId = this.authService.getUserId();
    if (!loggedUserId) return;
    this.followService.getFollowings(loggedUserId).subscribe((res) => {
      this.profiles = res.data.content;
    });
  }
  get profileFilter(): UserProfile[] {
    return this.profiles.filter((profile) =>
      profile.displayName.toLowerCase().includes(this.searchQuery)
    );
  }
  get placeholders(): any[] {
    return createPlaceholders(this.profileFilter);
  }
}
