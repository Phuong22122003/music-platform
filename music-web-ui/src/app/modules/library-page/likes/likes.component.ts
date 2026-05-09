import { Component, OnInit } from '@angular/core';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { Track } from '../../../core/models/track';
import { AuthService } from '../../../core/services/auth-service';
import { FollowService } from '../../../core/services/follow.service';
import { createPlaceholders } from '../../../shared/utils/helper';

@Component({
  selector: 'app-likes',
  standalone: false,
  templateUrl: './likes.component.html',
  styleUrls: ['./likes.component.scss'],
})
export class LikesComponent implements OnInit {
  tracks: Track[] = [];
  loggedUserId: string = '';
  isFollows: boolean[] = [];
  searchQuery: string = '';

  constructor(
    private likedTrackService: LikedTrackService,
    private authService: AuthService,
    private followService: FollowService
  ) {}

  ngOnInit(): void {
    this.loggedUserId = this.authService.getUserId() || '';
    this.loadLikedTracks();
  }

  loadLikedTracks() {
    this.likedTrackService.getAllLikedTracks().subscribe({
      next: (res) => {
        this.tracks = res.data;
      },
      error: (err) => {
        console.error(err);
      },
    });
  }

  onTrackStateChange(
    event: { trackId: string; isLiked: boolean },
    track: Track
  ) {
    if (!event.isLiked) {
      this.tracks = this.tracks.filter((t) => t.id !== event.trackId);
    }
  }

  onFollowStatusChange(isFollowed: boolean, track: Track) {
    const trackIndex = this.tracks.findIndex((t) => t.id === track.id);
    if (trackIndex !== -1) {
      this.isFollows[trackIndex] = isFollowed;
    }
  }

  get filteredTracks(): Track[] {
    const query = this.searchQuery.toLowerCase();
    return this.tracks.filter(
      (track) =>
        track.title?.toLowerCase().includes(query) ||
        track.displayName?.toLowerCase().includes(query)
    );
  }

  get placeholders(): any[] {
    return createPlaceholders(this.filteredTracks);
  }
}
