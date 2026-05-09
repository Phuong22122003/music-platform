import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { Observable } from 'rxjs';
import { FollowService } from '../../../core/services/follow.service';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { TrackAction } from '../../../core/models/track_action';
import copyLink from '../../utils/copylink';
import { PlaylistModalComponent } from '../playlist-modal/playlist-modal.component';
import { Track } from '../../../core/models/track';
import { AuthService } from '../../../core/services/auth-service';
import { AudioPlayerService } from '../../../core/services/audio-player.service';

@Component({
  selector: 'app-card',
  standalone: false,
  templateUrl: './card.component.html',
  styleUrl: './card.component.scss',
})
export class CardComponent implements OnInit {
  isShow = false;
  @Input() isLiked = false;
  @Input() isFollowed = false;
  @Input() goTo!: () => void;
  @Input() onCopyLink!: () => void;
  @Input() playTrack!: () => void;
  @Input() pauseTrack!: () => void;
  @Input() addToNextUp!: () => void;
  @Input() isPlay = false;
  @Input() toggleLike!: () => Observable<any>;
  @Input() toggleFollow!: () => Observable<any>;
  @Input() trackIdsToAddPlayList!: string[];
  @Input() idToEmitDelete!: string;
  @Input() isOwner: boolean = false;
  @Output() unlikeEvent = new EventEmitter<string>();
  @ViewChild('playlistModal', { static: true })
  playlistModal!: PlaylistModalComponent;
  menuVisible = false;
  trackActions: TrackAction[] = [
    {
      label: 'Add to Next Up',
      icon: 'fas fa-forward',
      action: () => this.addToNextUp(),
    },
    {
      label: 'Add to Playlist',
      icon: 'fas fa-plus',
      action: () => this.addToPlaylist(),
    },
    {
      label: 'Copy',
      icon: 'fa-solid fa-copy',
      action: () => this.onCopyLink(),
    },
  ];

  constructor(
    private followService: FollowService,
    private likedTrackService: LikedTrackService,
    private audioPlayerService: AudioPlayerService
  ) {}

  ngOnInit(): void {}
  addToPlaylist(): void {
    this.playlistModal.show();
  }

  playTrackInnerCard(event: any) {
    event.stopPropagation();
    if (this.isPlay) {
      this.pauseTrack();
    } else {
      this.playTrack();
    }
    this.isPlay = !this.isPlay;
  }

  toggleLikeInnerCard(event: any) {
    event.stopPropagation();

    this.toggleLike?.().subscribe((res) => {
      this.isLiked = !this.isLiked;
      this.unlikeEvent.emit(this.idToEmitDelete);
    });
  }

  toggleFollowInnerCard(event: any) {
    event.stopPropagation();
    this.toggleFollow?.().subscribe(() => {
      this.isFollowed = !this.isFollowed;
    });
  }

  prevent(event: any) {
    event.stopPropagation();
  }
  showPlayButton() {
    this.isShow = true;
  }
  hiddenPlayButton() {
    this.isShow = false;
  }
}
