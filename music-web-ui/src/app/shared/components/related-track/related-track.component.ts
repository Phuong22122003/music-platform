import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { RelatedTrack } from '../../../core/models/playlist';
import { Router } from '@angular/router';
import { Observable, of, Subscription } from 'rxjs';
import { TrackList } from '../../../core/models/tracklist';
import { COVER_BASE_URL } from '../../utils/url';
import { Track } from '../../../core/models/track';
import { AlbumService } from '../../../core/services/album.service';
import { PlaylistService } from '../../../core/services/playlist_service';
import { ProfileService } from '../../../core/services/profile.service';
import { FollowService } from '../../../core/services/follow.service';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { AuthService } from '../../../core/services/auth-service';
import { LikedPlaylistService } from '../../../core/services/liked_playlist_service';
import { AudioPlayerService } from '../../../core/services/audio-player.service';
import { NextPlayListService } from '../../../core/services/next-play-list.service';

@Component({
  selector: 'app-related-track',
  standalone: false,
  templateUrl: './related-track.component.html',
  styleUrl: './related-track.component.scss',
})
export class RelatedTrackComponent implements OnDestroy, OnInit {
  @Input() relatedTrackInput?: Track | TrackList;
  @Input() isLiked = false;
  @Input() isFollowed = false;
  @Output() unlikeEvent = new EventEmitter<string>();
  loggedUserId = '';
  coverUrl = COVER_BASE_URL;
  isShow = false;
  relatedTrack!: RelatedTrack;
  id: string = '';
  trackIdsToAddPlayList: string[] = [];
  playTrack!: any;
  pauseTrack = () => {
    this.audioPlayerService.pause();
  };
  playingSub?: Subscription;
  isPlaying = false;
  playingTrackListSub?: Subscription;
  addToNextUp: any;
  constructor(
    private router: Router,
    private albumService: AlbumService,
    private playlistService: PlaylistService,
    private likedPlaylistService: LikedPlaylistService,
    private followService: FollowService,
    private likedTrackService: LikedTrackService,
    private authService: AuthService,
    private audioPlayerService: AudioPlayerService,
    private nextPlayListService: NextPlayListService
  ) {}

  ngOnInit(): void {
    if (!this.relatedTrackInput) return;
    this.loggedUserId = this.authService.getUserId() || '';
    // Chuyển đổi relatedTrackInput thành RelatedTrack
    if (this.isTrack(this.relatedTrackInput)) {
      this.relatedTrack = {
        playlistId: this.relatedTrackInput.id,
        trackName: this.relatedTrackInput.title || 'Unknown',
        authors: this.relatedTrackInput.displayName || 'Unknown',
        trackPath: this.relatedTrackInput.coverImageName || '',
        userId: this.relatedTrackInput.userId,
      };
      this.playTrack = () => {
        this.audioPlayerService.playTrack(this.relatedTrackInput as Track);
      };
      this.addToNextUp = () => {
        this.nextPlayListService.addTrackSubject.next(
          this.relatedTrackInput as Track
        );
      };
      this.audioPlayerService.currentTrack$.subscribe((currentTrack) => {
        const isCurrent =
          currentTrack?.id === (this.relatedTrackInput as Track).id;
        if (isCurrent) {
          this.subscribeToPlaying();
        } else {
          this.unsubscribePlaying();
          this.isPlaying = false;
        }
      });
      this.trackIdsToAddPlayList = [this.relatedTrackInput.id];
    } else {
      this.relatedTrack = {
        playlistId: this.relatedTrackInput.listId,
        trackName: this.relatedTrackInput.listname,
        authors: this.relatedTrackInput.displayName || 'Unknown',
        trackPath: this.relatedTrackInput.imagePath,
        userId: this.relatedTrackInput.userId,
      };
      const tracks = (this.relatedTrackInput as TrackList).tracks;
      this.playTrack = () => {
        this.audioPlayerService.setCurrentTrackList(
          this.relatedTrack.playlistId
        );

        this.nextPlayListService.addTrackListSubject.next(tracks);
        console.log(this.relatedTrackInput);
        this.audioPlayerService.playTrack(tracks[0]);
      };
      this.trackIdsToAddPlayList = this.relatedTrackInput.tracks.map(
        (track) => track.id
      );
      this.addToNextUp = () => {
        this.nextPlayListService.addTrackListSubject.next(tracks);
      };
      this.audioPlayerService.currentTrackList$.subscribe((trackListId) => {
        const isCurrent = this.relatedTrack.playlistId === trackListId;
        if (isCurrent) {
          this.subscribeToTrackInListPlay();
        } else {
          console.log(trackListId);
          this.unsubscribeTrackInListPlay();
          this.isPlaying = false;
        }
      });
    }

    // Kiểm tra follow và like status cho Track và TrackList (Album, Playlist)
    this.checkFollowStatus();
    this.checkLikeStatus();
  }

  // Kiểm tra là Track hay TrackList
  private isTrack(obj: any): obj is Track {
    return 'title' in obj;
  }

  goTo = () => {
    console.log('go to');
    if (this.isTrack(this.relatedTrack)) {
      const track = this.relatedTrackInput as Track;
      this.router.navigate(['/song', track.id]);
    } else {
      const trackList = this.relatedTrackInput as TrackList;
      if (trackList.type === 'album') {
        console.log('/album', trackList.listId);
        this.router.navigate(['/albums', trackList.listId]);
      } else {
        this.router.navigate(['/playlists', trackList.listId]);
      }
    }
  };

  toggleLike = (): Observable<any> => {
    let action;

    if (this.isTrack(this.relatedTrackInput)) {
      // Nếu là Track
      action = this.isLiked
        ? this.likedTrackService.unLikeTrack(this.relatedTrackInput.id)
        : this.likedTrackService.likeTrack(this.relatedTrackInput.id);
    } else if (this.relatedTrackInput?.type === 'album') {
      // Nếu là Album trong TrackList
      action = this.isLiked
        ? this.albumService.unlikeAlbum(this.relatedTrackInput.listId)
        : this.albumService.likeAlbum(this.relatedTrackInput.listId);
    } else {
      // Nếu là Playlist trong TrackList
      action = this.isLiked
        ? this.likedPlaylistService.unLikePlaylist(
            this.relatedTrackInput?.listId || ''
          )
        : this.likedPlaylistService.likePlaylist(
            this.relatedTrackInput?.listId || ''
          );
    }

    return action;
  };

  toggleFollow = (): Observable<any> => {
    if (this.isTrack(this.relatedTrackInput)) {
      // Nếu là Track
      const action = this.isFollowed
        ? this.followService.unfollowUser(this.relatedTrackInput.userId)
        : this.followService.followUser({
            followingId: this.relatedTrackInput.userId,
          });
      return action;
    } else {
      // Nếu là Album hoặc Playlist trong TrackList
      const action = this.isFollowed
        ? this.followService.unfollowUser(this.relatedTrackInput?.userId || '')
        : this.followService.followUser({
            followingId: this.relatedTrackInput?.userId || '',
          });
      return action;
    }
  };

  unlike(trackId: string) {
    this.unlikeEvent.emit(trackId);
  }

  checkFollowStatus(): void {
    const userId = this.authService.getUserId()!;

    if (this.isTrack(this.relatedTrackInput)) {
      // Nếu là Track
      this.followService
        .isFollowing(userId, this.relatedTrackInput.userId)
        .subscribe((res) => {
          this.isFollowed = res.data;
        });
    } else {
      // Nếu là TrackList (Album, Playlist)
      this.followService
        .isFollowing(userId, this.relatedTrackInput?.userId || '') // Assuming TrackList also has userId
        .subscribe((res) => {
          this.isFollowed = res.data;
        });
    }
  }

  // Kiểm tra like status cho cả Track và TrackList
  checkLikeStatus(): void {
    if (this.isTrack(this.relatedTrackInput)) {
      // Nếu là Track
      this.likedTrackService
        .isTrackLiked(this.relatedTrackInput.id)
        .subscribe((res) => {
          this.isLiked = res.data;
        });
    } else {
      // Nếu là TrackList (Album, Playlist)
      let trackList = this.relatedTrackInput as TrackList;
      this.isLiked = trackList.isLiked;
    }
  }

  private subscribeToPlaying() {
    if (this.playingSub) return; // tránh subscribe lại

    this.playingSub = this.audioPlayerService.isPlaying$.subscribe(
      (isPlaying) => {
        this.isPlaying = isPlaying;
      }
    );
  }

  private unsubscribePlaying() {
    if (this.playingSub) {
      this.playingSub.unsubscribe();
      this.playingSub = undefined;
    }
  }
  private subscribeToTrackInListPlay() {
    if (this.playingTrackListSub) return;
    this.playingTrackListSub =
      this.audioPlayerService.isTrackInListPlaying$.subscribe((isPlay) => {
        this.isPlaying = isPlay;
      });
  }
  private unsubscribeTrackInListPlay() {
    if (this.playingTrackListSub) {
      this.playingTrackListSub.unsubscribe();
      this.playingTrackListSub = undefined;
    }
  }
  ngOnDestroy(): void {
    this.unsubscribePlaying();
    this.unsubscribeTrackInListPlay();
  }
}
