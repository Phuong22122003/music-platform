import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { NextPlayListService } from '../../../core/services/next-play-list.service';
import { Track } from '../../../core/models/track';
import { forkJoin, Subscription } from 'rxjs';
import { AudioPlayerService } from '../../../core/services/audio-player.service';
import { TrackService } from '../../../core/services/track.service';
import { AuthService } from '../../../core/services/auth-service';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { FollowService } from '../../../core/services/follow.service';
import { COVER_BASE_URL } from '../../utils/url';
import { ProfileService } from '../../../core/services/profile.service';

const STORAGE_KEY = 'next_playlist_track_ids';

@Component({
  selector: 'app-next-playlist',
  standalone: false,
  templateUrl: './next-playlist.component.html',
  styleUrl: './next-playlist.component.scss',
})
export class NextPlaylistComponent implements OnInit, OnDestroy, AfterViewInit {
  // @ViewChild('audioPlayer', { static: true }) audioPlayer!: ElementRef;
  @ViewChild('processBar', { static: true }) processBar!: ElementRef;

  // Track all subscriptions
  private subscriptions: Subscription[] = [];
  addTrackSubs!: Subscription;
  addTrackListSubs!: Subscription;
  playPauseTrackSubs!: Subscription;
  deleteByIndexSubs!: Subscription;
  deleteAllSubs!: Subscription;
  currentTime: number = 0;
  progress: number = 0;
  duration: number = 0;
  isPlay: boolean = false;
  isOpenNextUpList = false;
  audioUrl: string = '';
  username: string = '';
  trackName: string = '';
  currentIndex: number = 0;
  tracks: Track[] = [];
  originalTracks: any[] = [];
  repeatModes = ['off', 'one', 'all'];
  repeatModeIndex = 0;
  coverUrl = COVER_BASE_URL;
  isShuffle: boolean = false;
  currentVolume: number = 50;
  private isTriggeringFromInternal = false;
  isFirstTime: boolean = true;
  playingFollowUserId = true;
  get repeatMode() {
    return this.repeatModes[this.repeatModeIndex];
  }
  constructor(
    private renderer: Renderer2,
    private nextPlayListService: NextPlayListService,
    private audioPlayerService: AudioPlayerService,
    private trackService: TrackService,
    private authService: AuthService,
    private likedTrackService: LikedTrackService,
    private followService: FollowService,
    private profileService: ProfileService
  ) {}
  ngOnInit(): void {
    this.loadTracksFromStorage();
    this.currentVolume = 50;

    // Store subscription references
    this.addTrackSubs = this.nextPlayListService.addTrackSubject.subscribe(
      (track: Track) => {
        if (track && track.id !== '0') {
          const profileSub = this.profileService
            .getProfileById(track.userId)
            .subscribe((res) => {
              track.displayName = res.data.displayName;
              this.tracks.push(track);
              this.saveTracksToStorage();
            });
          this.subscriptions.push(profileSub);
        }
      }
    );

    this.addTrackListSubs =
      this.nextPlayListService.addTrackListSubject.subscribe(
        (tracks: Track[]) => {
          if (tracks && tracks.length > 0) {
            const profileRequests = tracks.map((track) =>
              this.profileService.getProfileById(track.userId)
            );
            const forkJoinSub = forkJoin(profileRequests).subscribe(
              (responses) => {
                responses.forEach((res, index) => {
                  tracks[index].displayName = res.data.displayName;
                });
                this.tracks = tracks;
                this.username = this.tracks[0].username;
                this.audioUrl = this.tracks[0].fileName;
                this.trackName = this.tracks[0].name;
                this.saveTracksToStorage();
              }
            );
            this.subscriptions.push(forkJoinSub);
          }
        }
      );

    this.playPauseTrackSubs =
      this.nextPlayListService.playPauseTrackInNextPLayListSubject.subscribe(
        (res) => {
          const selectedTrack = this.tracks[res.index];
          if (!selectedTrack) return;

          this.isTriggeringFromInternal = true; // Bắt đầu chặn vòng lặp

          try {
            if (res.type === 'PLAY') {
              if (this.currentIndex !== res.index) {
                this.username = selectedTrack.username;
                this.trackName = selectedTrack.name;

                this.audioPlayerService.playTrack(selectedTrack);
              } else {
                this.audioPlayerService.resume();
              }
            } else {
              this.audioPlayerService.pause();
            }

            this.currentIndex = res.index;
            this.isFirstTime = false;
          } finally {
            this.isTriggeringFromInternal = false; // Mở lại cho các update tiếp theo
          }
        }
      );
    // this.trackService
    //   .getTracksByUserId(this.authService.getUserId() || '')
    //   .subscribe((res) => {
    //     this.nextPlayListService.addTrackListSubject.next(res.data);
    //   });

    this.deleteByIndexSubs =
      this.nextPlayListService.deleteTrackByIndex.subscribe((trackIndex) => {
        this.tracks.splice(trackIndex, 1);
        console.log(trackIndex);
        this.saveTracksToStorage();
      });
    this.deleteAllSubs = this.nextPlayListService.deleteAll.subscribe(() => {
      let currentTrack = this.tracks[this.currentIndex];
      this.tracks = [];
      this.tracks.push(currentTrack);
      this.saveTracksToStorage();
    });

    // Add AudioPlayerService subscriptions to tracking
    this.subscriptions.push(
      this.audioPlayerService.isPlaying$.subscribe((isPlaying) => {
        this.isPlay = isPlaying;
        if (!this.isTriggeringFromInternal) {
          const followSub = this.checkFollow();
          if (followSub) {
            this.subscriptions.push(followSub);
          }
          this.nextPlayListService.playPauseTrackInNextPLayListSubject.next({
            index: this.currentIndex,
            type: isPlaying ? 'PLAY' : 'STOP',
          });
        }
      })
    );

    this.subscriptions.push(
      this.audioPlayerService.duration$.subscribe((duration) => {
        this.duration = duration;
      })
    );

    this.subscriptions.push(
      this.audioPlayerService.currentTime$.subscribe((currentTime) => {
        this.currentTime = currentTime;
        this.updateProgress();
      })
    );

    this.subscriptions.push(
      this.audioPlayerService.audioEnded$.subscribe(() => {
        this.onAudioEnded();
      })
    );

    this.subscriptions.push(
      this.audioPlayerService.currentTrack$.subscribe((track) => {
        if (!track) return;
        const isExisted = this.tracks.some((t) => t.id === track.id);
        console.log(isExisted);
        if (!isExisted) {
          // this.tracks = JSON.parse(JSON.stringify(this.tracks));
          this.tracks = [];
          this.tracks.unshift(track);
        }
        if (track.belongToTrackListId) {
          this.audioPlayerService.setCurrentTrackList(
            track.belongToTrackListId
          );
        }
        this.currentIndex = this.tracks.findIndex((t) => t.id === track.id);
        const currentTrack = this.tracks[this.currentIndex];
        this.trackName = currentTrack.title;
        this.username = currentTrack.displayName || 'Unknown User';
        this.saveTracksToStorage();
      })
    );
  }
  checkFollow() {
    if (!this.tracks || this.tracks.length == 0) return;
    const loggedUserId = this.authService.getUserId() || '';
    const playingUserId = this.tracks[this.currentIndex].userId;
    const sub = this.followService
      .isFollowing(loggedUserId, playingUserId)
      .subscribe((res) => {
        this.playingFollowUserId = res.data;
      });
    return sub;
  }
  ngAfterViewInit(): void {}
  updateProgress() {
    this.progress = (this.currentTime / this.duration) * 100;
    this.renderer.setStyle(
      this.processBar.nativeElement,
      'width',
      `${Math.round(this.progress)}%`
    );
  }
  // setDuration() {
  //   console.log(this.audioPlayer.nativeElement.duration);
  //   this.duration = this.audioPlayer.nativeElement.duration;
  // }
  formatDuration(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const sec = Math.floor(seconds % 60);
    return `${minutes}:${sec < 10 ? '0' : ''}${sec}`;
  }
  togglePlay() {
    // const audio = this.audioPlayer.nativeElement;
    if (!this.isPlay) {
      if (this.isFirstTime) {
        this.audioPlayerService.playTrack(this.tracks[this.currentIndex]);
        this.isFirstTime = false;
      } else {
        this.audioPlayerService.resume();
      }
      this.emitStatusTrack(this.currentIndex, 'PLAY');
    } else {
      // audio.pause();
      this.audioPlayerService.pause();
      this.emitStatusTrack(this.currentIndex, 'STOP');
    }
  }
  seekAudio(event: any) {
    let currentTime = (event.target.value / 100) * this.duration;
    this.audioPlayerService.seekTo(currentTime);
  }
  changeVolume(event: any) {
    // const audio = this.audioPlayer.nativeElement;
    this.currentVolume = event.target.value;
    this.audioPlayerService.setVolume(this.currentVolume / 100);
  }
  toggleNextUpList() {
    this.isOpenNextUpList = !this.isOpenNextUpList;
  }
  onCloseNextUpList() {
    this.isOpenNextUpList = false;
  }
  toggleRepeat() {
    this.repeatModeIndex = (this.repeatModeIndex + 1) % this.repeatModes.length;
  }

  onPlayTrack(index: number) {
    let track: Track = this.tracks[index];
    if (!track) return;

    this.username = track.username;
    this.trackName = track.name;
    this.audioPlayerService.playTrack(track);
    this.currentIndex = index;
    this.emitStatusTrack(this.currentIndex, 'PLAY');
  }
  onAudioEnded() {
    if (
      this.repeatMode === 'off' &&
      this.currentIndex < this.tracks.length - 1
    ) {
      this.onPlayTrack(++this.currentIndex);
    }
    if (this.repeatMode === 'one') {
      this.onPlayTrack(this.currentIndex);
    }
    if (this.repeatMode === 'all') {
      this.currentIndex = (this.currentIndex + 1) % this.tracks.length;
      this.onPlayTrack(this.currentIndex);
    }
  }
  toggleShuffle() {
    this.isShuffle = !this.isShuffle;
    if (this.isShuffle) {
      this.originalTracks = [...this.tracks];
      this.tracks = this.shuffleArray([...this.tracks], this.currentIndex);
    } else {
      this.tracks = [...this.originalTracks];
    }
  }
  shuffleArray(array: any[], currentIndex: number): any[] {
    if (array.length <= 1) return array; // Nếu danh sách quá nhỏ, không cần shuffle

    // Lấy bài hát hiện tại ra khỏi danh sách
    const currentTrack = array[currentIndex];
    const remainingTracks = array.filter((_, index) => index !== currentIndex);

    // Shuffle các bài còn lại bằng thuật toán Fisher-Yates
    for (let i = remainingTracks.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [remainingTracks[i], remainingTracks[j]] = [
        remainingTracks[j],
        remainingTracks[i],
      ];
    }

    // Chèn lại bài hát hiện tại vào vị trí cũ
    remainingTracks.splice(currentIndex, 0, currentTrack);
    return remainingTracks;
  }

  onNextTrack() {
    this.currentIndex = (this.currentIndex + 1) % this.tracks.length;
    this.onPlayTrack(this.currentIndex);
  }
  onPrevTrack() {
    this.currentIndex =
      this.currentIndex - 1 < 0
        ? this.tracks.length - 1
        : this.currentIndex - 1;
    this.onPlayTrack(this.currentIndex);
  }
  emitStatusTrack(index: number, type: string) {
    this.isFirstTime = false;
    this.nextPlayListService.playPauseTrackInNextPLayListSubject.next({
      index: index,
      type: type,
    });
  }
  ngOnDestroy(): void {
    // Unsubscribe from all tracked subscriptions
    this.subscriptions.forEach((sub) => sub.unsubscribe());

    // Unsubscribe from main service subscriptions
    if (this.playPauseTrackSubs) {
      this.playPauseTrackSubs.unsubscribe();
    }
    if (this.addTrackListSubs) {
      this.addTrackListSubs.unsubscribe();
    }
    if (this.addTrackSubs) {
      this.addTrackSubs.unsubscribe();
    }
    if (this.deleteByIndexSubs) {
      this.deleteByIndexSubs.unsubscribe();
    }
    if (this.deleteAllSubs) {
      this.deleteAllSubs.unsubscribe();
    }
  }
  toggleMute() {
    this.currentVolume = this.currentVolume > 0 ? 0 : 50;
    this.audioPlayerService.setVolume(this.currentVolume / 100);
  }
  handleDropDrag(event: any) {
    this.tracks = event.tracks;
    this.currentIndex = event.currentIndex;
    this.saveTracksToStorage();
  }

  toggleLike() {
    const currentTrack = this.tracks[this.currentIndex];
    console.log(currentTrack.isLiked);
    const action = currentTrack.isLiked
      ? this.likedTrackService.unLikeTrack(currentTrack.id)
      : this.likedTrackService.likeTrack(currentTrack.id);

    action.subscribe((res) => {
      this.tracks[this.currentIndex].isLiked =
        !this.tracks[this.currentIndex].isLiked;
    });
  }

  toggleFollow() {
    const loggedUserId = this.authService.getUserId() || '';
    const playingUserId = this.tracks[this.currentIndex].userId;
    const action = this.playingFollowUserId
      ? this.followService.unfollowUser(playingUserId)
      : this.followService.followUser({ followingId: playingUserId });
    action.subscribe((res) => {
      this.playingFollowUserId = !this.playingFollowUserId;
    });
  }

  private loadTracksFromStorage() {
    const storedTrackIds = localStorage.getItem(STORAGE_KEY);
    if (storedTrackIds) {
      const trackIds = JSON.parse(storedTrackIds) as string[];
      if (trackIds && trackIds.length > 0) {
        // Load full track data for each ID
        this.trackService.getTracksByIds(trackIds).subscribe((response) => {
          if (response.data) {
            console.log(response.data);
            this.tracks = response.data;
            // Load display names for all tracks
            this.profileService
              .getProfileByIds(this.tracks.map((track) => track.userId))
              .subscribe((profileResponse) => {
                this.tracks.forEach((track) => {
                  const profile = profileResponse.data.find(
                    (p) => p.userId === track.userId
                  );
                  if (profile) {
                    track.displayName = profile.displayName;
                  }
                });

                // Set initial track info after all data is loaded
                const firstTrack = this.tracks[0];
                this.currentIndex = 0;
                this.username = firstTrack.displayName || 'Unknown User';
                this.trackName = firstTrack.title;
                this.audioUrl = firstTrack.fileName;
                this.isFirstTime = true;
              });
          }
        });
      }
    }
  }

  private saveTracksToStorage() {
    // Only save track IDs to storage
    const trackIds = this.tracks.map((track) => track.id);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(trackIds));
  }

  onDeleteByTrackIndex(index: number) {
    this.tracks.splice(index, 1);
    this.saveTracksToStorage();
  }
}
