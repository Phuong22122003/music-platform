import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { ApiResponse } from '../../../core/models/api_response';
import { firstValueFrom, forkJoin, Observable } from 'rxjs';
import { TrackAction } from '../../../core/models/track_action';
import { AuthService } from '../../../core/services/auth-service';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { Track } from '../../../core/models/track';
import copyLink from '../../utils/copylink';
import { PlaylistModalComponent } from '../playlist-modal/playlist-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { EditTrackComponent } from './edit-track/edit-track.component';
import { Validators } from '@angular/forms';
import { TrackService } from '../../../core/services/track.service';
import { FileService } from '../../../core/services/file.service';
import { GenreService } from '../../../core/services/genre_service';
import { TagService } from '../../../core/services/tag_service';
import { GenreResponse } from '../../../core/models/genre/genre_response.model';
import { TagResponse } from '../../../core/models/tag/tag_response.model';
import { NextPlayListService } from '../../../core/services/next-play-list.service';

@Component({
  selector: 'app-track-action',
  standalone: false,
  templateUrl: './track-action.component.html',
  styleUrl: './track-action.component.scss',
})
export class TrackActionComponent implements OnInit {
  @Input() track!: Track;

  @ViewChild('playlistModal', { static: true })
  playlistModal!: PlaylistModalComponent;
  isOwner = false;
  loggedUserId = '';
  tags: TagResponse[] = [];
  genres: GenreResponse[] = [];
  formFields: {
    name: string;
    label: string;
    type: string;
    columnSpan?: number;
    placeholder?: string;
    validators?: any[];
    options?: {
      imageOrientation?: any;
      widthImage?: number;
      heighImage?: number;
      defaultValue?: any;
      includeDefault?: boolean;
    };
    dataSelect?: any;
  }[] = [];
  constructor(
    private authService: AuthService,
    private likedTrackService: LikedTrackService,
    private dialog: MatDialog,
    private trackService: TrackService,
    private fileService: FileService,
    private genreService: GenreService,
    private tagService: TagService,
    private nextPlayListService: NextPlayListService
  ) {}
  async ngOnInit(): Promise<void> {
    this.loggedUserId = this.authService.getUserId() || '';
    this.isOwner = this.loggedUserId === this.track.userId;
    await this.buildFormField();
    forkJoin({
      isLiked: this.likedTrackService.isTrackLiked(this.track.id),
      likeCount: this.likedTrackService.getTrackLikeCount(this.track.id),
    }).subscribe(({ isLiked, likeCount }) => {
      this.isLiked = isLiked.data;
      this.likeCount = likeCount.data;
    });
  }
  likeCount: number = 0;
  isLiked: boolean = false;
  isCopy: boolean = false;
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
  ];

  onToggleLike(): void {
    const action: Observable<ApiResponse<boolean>> = this.isLiked
      ? this.likedTrackService.unLikeTrack(this.track.id)
      : this.likedTrackService.likeTrack(this.track.id);

    action.subscribe({
      next: (res) => {
        if (res.data) {
          this.isLiked = !this.isLiked;
          if (this.isLiked) {
            this.likeCount = (this.likeCount || 0) + 1;
          } else {
            this.likeCount = Math.max((this.likeCount || 1) - 1, 0);
          }
        }
      },
      error: (err) => {
        console.error('Failed to toggle like status:', err);
      },
    });
  }

  onCopyLink(): void {
    const trackUrl = `${window.location.origin}/song/${this.track.id}`;
    copyLink(trackUrl);
    this.isCopy = true;
  }

  addToNextUp() {
    this.nextPlayListService.addTrackSubject.next(this.track);
  }

  addToPlaylist() {
    console.log('play list');
    this.playlistModal.show();
  }

  openEdit() {
    const dialogRef = this.dialog.open(EditTrackComponent, {
      width: '800px',
      maxWidth: '50vw',
    });
    dialogRef.componentInstance.formFields = this.formFields;
    dialogRef.componentInstance.track = this.track;
    dialogRef.componentInstance.updateInfo.subscribe((track) => {
      this.trackService.emitUpdateDone(track);
      this.track = track;
      dialogRef.close();
      this.buildFormField();
    });
  }

  async buildFormField() {
    let imageDefault = null;
    if (this.track.coverImageName) {
      try {
        imageDefault = await firstValueFrom(
          this.fileService.getCoverImage(this.track.coverImageName)
        );
      } catch (error) {
        console.error('Error fetching image', error);
      }
    }
    this.genres = (await firstValueFrom(this.genreService.getAllGenres())).data;
    this.tags = await (await firstValueFrom(this.tagService.getAllTags())).data;
    this.formFields = [
      {
        name: 'image',
        label: 'Upload Cover Image',
        type: 'image',
        options: {
          widthImage: 300,
          heighImage: 300,
          defaultValue: imageDefault,
          imageOrientation: 'horizontal',
        },
      },
      {
        columnSpan: 2,
        name: 'title',
        label: 'Track Title',
        type: 'text',
        validators: [Validators.required],
        options: {
          defaultValue: this.track.title || '',
        },
      },
      {
        columnSpan: 2,
        name: 'description',
        label: 'Description',
        type: 'textarea',
        options: {
          defaultValue: this.track.description || '',
        },
      },
      {
        columnSpan: 2,
        name: 'genreId',
        label: 'Genre',
        type: 'select',
        placeholder: 'Select a genre',
        options: {
          defaultValue: this.track.genre?.id || '',
        },
        dataSelect: this.genres || [], // Gán sau khi fetch genre list
      },
      {
        columnSpan: 2,
        name: 'tagIds',
        label: 'Tags',
        type: 'multi-select',
        placeholder: 'Select tags',
        options: {
          defaultValue: this.track.tags?.map((tag) => tag.id) || [],
        },
        dataSelect: this.tags || [], // Gán sau khi fetch tag list
      },
      {
        columnSpan: 2,
        name: 'privacy',
        label: 'Track Privacy',
        type: 'radio',
        dataSelect: [
          { id: 'public', name: 'Public' },
          { id: 'private', name: 'Private' },
        ],
        options: {
          defaultValue: this.track?.privacy || 'public',
        },
        validators: [Validators.required],
      },
    ];
  }
}
