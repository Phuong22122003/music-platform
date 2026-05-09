import { Component, Input, OnInit } from '@angular/core';
import { LikedTrackService } from '../../../../core/services/liked-track.service';
import { ProfileService } from '../../../../core/services/profile.service';
import { UserProfile } from '../../../../core/models/user_profile';
import { BehaviorSubject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-all-like',
  standalone: false,
  templateUrl: './all-like.component.html',
  styleUrl: './all-like.component.scss',
})
export class AllLikeComponent {
  trackId!: string;
  users: UserProfile[] = [];
  page = 0;
  size = 12;
  loading = false;
  hasMore = true;
  searchQuery = '';
  private userSubject = new BehaviorSubject<UserProfile[]>([]);
  users$ = this.userSubject.asObservable();
  userIds: string[] = [];
  constructor(
    private likedTrackService: LikedTrackService,
    private profileService: ProfileService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.trackId = this.route.parent?.snapshot.paramMap.get('trackId')!;
    this.likedTrackService.getUserIdsLikedTrack(this.trackId).subscribe({
      next: (res) => {
        this.userIds = res.data;
        this.hasMore = this.userIds.length > 0;
        this.loadMore(); // chỉ bắt đầu gọi getProfile sau khi đã có userIds
      },
      error: (err) => {
        console.error('Error loading user IDs:', err);
      },
    });
  }

  loadMore(): void {
    if (this.loading || !this.hasMore) return;

    const startIdx = this.page * this.size;
    const endIdx = startIdx + this.size;
    const idsToLoad = this.userIds.slice(startIdx, endIdx);

    if (idsToLoad.length === 0) {
      this.hasMore = false;
      return;
    }

    this.loading = true;
    this.profileService.getProfileByIds(idsToLoad).subscribe({
      next: (res) => {
        this.users = [...this.users, ...res.data];
        this.userSubject.next(this.users);
        this.page++;
        this.loading = false;

        if (this.page * this.size >= this.userIds.length) {
          this.hasMore = false;
        }
      },
      error: (err) => {
        console.error('Error loading profiles:', err);
        this.loading = false;
      },
    });
  }

  onScroll(): void {
    this.loadMore();
  }

  get filteredUsers(): UserProfile[] {
    return this.users.filter((user) =>
      user.displayName.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }
}
