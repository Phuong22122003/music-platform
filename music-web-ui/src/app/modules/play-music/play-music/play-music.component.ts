import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TrackService } from '../../../core/services/track.service';
import { Track } from '../../../core/models/track';
import { ToastrService } from 'ngx-toastr';
import { ProfileService } from '../../../core/services/profile.service';
import { UserProfile } from '../../../core/models/user_profile';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CommentService } from '../../../core/services/comment.service';
import { TrackList } from '../../../core/models/tracklist';
import { AlbumService } from '../../../core/services/album.service';
import { PlaylistService } from '../../../core/services/playlist_service';
import { COVER_BASE_URL } from '../../../shared/utils/url';
import { AuthService } from '../../../core/services/auth-service';
import { FollowService } from '../../../core/services/follow.service';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { LikedPlaylistService } from '../../../core/services/liked_playlist_service';
import { AudioPlayerService } from '../../../core/services/audio-player.service';

@Component({
  selector: 'app-play-music',
  standalone: false,
  templateUrl: './play-music.component.html',
  styleUrl: './play-music.component.scss',
})
export class PlayMusicComponent implements OnInit {
  trackId!: string;
  tracklistId!: string;
  track!: Track;
  trackList!: TrackList;
  profile!: UserProfile;
  type: 'track' | 'playlist' | 'album' = 'track';
  currentTrack!: Track;
  currentIndex = 0;
  coverUrl = COVER_BASE_URL;
  displayNames: string[] = [];
  isOwner: boolean = false;
  likeCount = 0;

  constructor(
    private route: ActivatedRoute,
    private trackService: TrackService,
    private playlistService: PlaylistService,
    private albumService: AlbumService,
    private profileService: ProfileService,
    private toast: ToastrService,
    private router: Router,
    private authService: AuthService,
    private likedTrackService: LikedTrackService,
    private likedPlaylistService: LikedPlaylistService,
    private audioPlayerService: AudioPlayerService
  ) {}

  ngOnInit(): void {
    const url = this.router.url;

    if (url.startsWith('/song')) {
      this.type = 'track';
    } else if (url.startsWith('/playlists')) {
      this.type = 'playlist';
    } else if (url.startsWith('/albums')) {
      this.type = 'album';
    }

    this.route.paramMap
      .pipe(
        switchMap((params) => {
          if (this.type === 'track') {
            this.trackId = params.get('trackId')!;
            return this.trackService.getTrackById(this.trackId);
          } else {
            this.tracklistId = params.get('tracklistId')!;
            if (this.type === 'playlist') {
              return this.playlistService.getPlaylistById(this.tracklistId);
            } else {
              return this.albumService.getAlbumById(this.tracklistId);
            }
          }
        }),
        switchMap((res: any) => {
          const likeCount$ =
            this.type === 'track'
              ? this.likedTrackService.getTrackLikeCount(res.data.id)
              : this.type === 'playlist'
              ? this.likedPlaylistService.countLike(res.data.listId)
              : this.albumService.countLike(res.data.listId);

          if (this.type === 'track') {
            this.track = res.data;
            this.currentTrack = this.track;
            this.isOwner = this.authService.getUserId() === this.track.userId;

            const profile$ = this.profileService.getProfileById(
              this.track.userId
            );

            return forkJoin([profile$, likeCount$]).pipe(
              map(([profileRes, likeRes]) => {
                this.profile = profileRes.data;
                this.displayNames = [profileRes.data.displayName];
                this.likeCount = likeRes.data;

                return null;
              })
            );
          } else {
            this.trackList = res.data;
            this.currentTrack = this.trackList.tracks[0];
            this.isOwner =
              this.authService.getUserId() === this.trackList.userId;

            const ownerProfile$ = this.profileService.getProfileById(
              this.trackList.userId
            );
            const trackProfiles$ = forkJoin(
              this.trackList.tracks.map((t) =>
                this.profileService.getProfileById(t.userId).pipe(
                  map((profile) => profile.data.displayName),
                  catchError(() => of('Unknown User'))
                )
              )
            );

            return forkJoin([ownerProfile$, trackProfiles$, likeCount$]).pipe(
              map(([ownerRes, names, likeRes]) => {
                this.profile = ownerRes.data;
                this.displayNames = names;
                this.trackList.likeCount = likeRes.data;
                this.likeCount = likeRes.data;
                console.log(likeRes);
                return null;
              })
            );
          }
        })
      )
      .subscribe({
        next: () => {},
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      });
  }

  onChangeTrack(index: number) {
    this.currentTrack = this.trackList.tracks[index];
    this.currentIndex = index;
    this.audioPlayerService.playTrack(this.currentTrack);
  }
}
