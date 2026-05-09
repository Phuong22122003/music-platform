import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { TrackAction } from '../../../core/models/track_action';
import { firstValueFrom, forkJoin, Observable } from 'rxjs';
import { AuthService } from '../../../core/services/auth-service';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { TrackList } from '../../../core/models/tracklist';
import { ApiResponse } from '../../../core/models/api_response';
import { LikedPlaylistService } from '../../../core/services/liked_playlist_service';
import { openConfirmDialog } from '../../utils/dialog';
import { MatDialog } from '@angular/material/dialog';
import { PlaylistService } from '../../../core/services/playlist_service';
import { ToastrService } from 'ngx-toastr';
import copyLink from '../../utils/copylink';
import { DynamicFormComponent } from '../dynamic-form/dynamic-form.component';
import { Validators } from '@angular/forms';
import { UpdatePlaylistInfoRequest } from '../../../core/models/playlist/playlist_request.model';
import { EditPlaylistComponent } from './edit-playlist/edit-playlist.component';
import { AlbumService } from '../../../core/services/album.service';
import { FileService } from '../../../core/services/file.service';
import { TagService } from '../../../core/services/tag_service';
import { GenreService } from '../../../core/services/genre_service';
import { TagResponse } from '../../../core/models/tag/tag_response.model';
import { GenreResponse } from '../../../core/models/genre/genre_response.model';
import { NextPlayListService } from '../../../core/services/next-play-list.service';

@Component({
  selector: 'app-list-action',
  standalone: false,
  templateUrl: './list-action.component.html',
  styleUrl: './list-action.component.scss',
})
export class ListActionComponent implements OnChanges {
  @Output() delete = new EventEmitter<string>();
  @Input() tracklist!: TrackList;
  @Output() update = new EventEmitter<void>();
  formFields: any = [];
  loggedUserId = '';
  isOwner = false;
  constructor(
    private authService: AuthService,
    private likedPlaylistService: LikedPlaylistService,
    private dialog: MatDialog,
    private playlistService: PlaylistService,
    private toast: ToastrService,
    private albumService: AlbumService,
    private fileService: FileService,
    private tagService: TagService,
    private genreService: GenreService,
    private nextPlayListService: NextPlayListService
  ) {}
  async ngOnInit(): Promise<void> {
    this.loggedUserId = this.authService.getUserId() || '';
    this.isOwner = this.loggedUserId === this.tracklist.userId;
    await this.buildFormFields();
    if (this.tracklist.type === 'album') {
      this.albumService.countLike(this.tracklist.listId).subscribe((res) => {
        this.tracklist.likeCount = res.data;
      });
    } else {
      this.likedPlaylistService
        .countLike(this.tracklist.listId)
        .subscribe((res) => {
          this.tracklist.likeCount = res.data;
        });
    }
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tracklist']) {
      this.tracklist = changes['tracklist'].currentValue;
      this.buildFormFields();
    }
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
      label: 'Delete Playlist',
      icon: 'fas fa-plus',
      action: () => this.onDelete(),
    },
  ];

  onDelete() {
    openConfirmDialog(
      this.dialog,
      {
        title: 'Delete playlist',
        description: `Are you sure you want to delete ${this.tracklist.listname}? This action cannot be undone.`,
        confirmText: 'Delete',
        cancelText: 'Cancel',
      },
      () => {
        // Gọi API xóa ở đây
        const action =
          this.tracklist.type === 'playlist'
            ? this.playlistService.deletePlaylistById(this.tracklist.listId)
            : this.albumService.deleteAlbum(this.tracklist.listId);
        action.subscribe((res) => {
          this.toast.success(
            `Delete ${this.tracklist.displayName} successfully`
          );
          this.delete.emit(this.tracklist.listId);
        });
      }
    );
  }
  onCopyLink(): void {
    const trackUrl = `${window.location.origin}/sets/${this.tracklist.listId}`;
    copyLink(trackUrl);
    this.isCopy = true;
  }
  toggleLike() {
    if (
      this.tracklist.type !== 'album' &&
      this.tracklist.userId === this.loggedUserId
    ) {
      return;
    }
    const currentlyLiked = this.tracklist.isLiked;

    const onSuccess = () => {
      this.tracklist.isLiked = !currentlyLiked;
      this.tracklist.likeCount = this.tracklist.isLiked
        ? this.tracklist.likeCount + 1
        : this.tracklist.likeCount - 1;
      this.toast.success(
        !currentlyLiked ? 'Liked successfully' : 'Unliked successfully'
      );
    };

    const onError = (err: any) => {
      console.error(err);
      this.toast.error("Can't finish action, something went wrong");
    };

    if (this.tracklist.type === 'playlist') {
      const action = currentlyLiked
        ? this.likedPlaylistService.unLikePlaylist(this.tracklist.listId)
        : this.likedPlaylistService.likePlaylist(this.tracklist.listId);
      action.subscribe({ next: onSuccess, error: onError });
    } else {
      const action = currentlyLiked
        ? this.albumService.unlikeAlbum(this.tracklist.listId)
        : this.albumService.likeAlbum(this.tracklist.listId);
      action.subscribe({ next: onSuccess, error: onError });
    }
  }

  addToNextUp(): void {
    this.addToNextUp = () => {
      this.nextPlayListService.addTrackListSubject.next(this.tracklist.tracks);
    };
  }

  openEditPlaylist() {
    const dialogRef = this.dialog.open(EditPlaylistComponent, {
      width: '800px',
      maxWidth: '50vw',
      height: '70vh',
      maxHeight: '70vh',
    });

    dialogRef.componentInstance.tracklist = this.tracklist;
    dialogRef.componentInstance.formFields = this.formFields;
    dialogRef.componentInstance.cancelTracklist.subscribe(() =>
      dialogRef.close()
    );

    dialogRef.componentInstance.updateInfo.subscribe((formData) => {
      console.log('Form submitted!', formData);
      this.update.emit();
      dialogRef.close();
    });
  }

  private async buildFormFields() {
    const isAlbum = this.tracklist.type === 'album';
    let imageDefault = null;
    if (this.tracklist.imagePath) {
      try {
        imageDefault = await firstValueFrom(
          this.fileService.getCoverImage(this.tracklist.imagePath)
        );
      } catch (error) {
        console.error('Error fetching image', error);
      }
    }
    const tags: TagResponse[] = (
      await firstValueFrom(this.tagService.getAllTags())
    ).data;
    const genres: GenreResponse[] = (
      await firstValueFrom(this.genreService.getAllGenres())
    ).data;
    this.formFields = [
      {
        name: 'image',
        label: isAlbum ? 'Upload Album Cover' : 'Upload Playlist Cover',
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
        name: isAlbum ? 'albumTitle' : 'title',
        label: isAlbum ? 'Album Title' : 'Playlist Title',
        type: 'text',
        validators: [Validators.required],
        options: {
          defaultValue: this.tracklist.listname,
        },
      },
      ...(isAlbum
        ? [
            {
              columnSpan: 2,
              name: 'mainArtists',
              label: 'Main Artists',
              type: 'text',
              validators: [Validators.required],
              options: {
                defaultValue: this.tracklist.displayName,
              },
            },
          ]
        : [
            {
              columnSpan: 2,
              name: 'releaseDate',
              label: 'Release Date',
              type: 'datetime-local',
              validators: [Validators.required],
              options: {
                defaultValue: this.tracklist.releaseDate,
              },
            },
          ]),
      {
        columnSpan: 2,
        name: 'genreId',
        label: 'Genre',
        type: 'select',
        placeholder: 'Select a genre',
        options: {
          defaultValue: this.tracklist.genre?.id,
        },
        dataSelect: genres,
      },
      {
        columnSpan: 2,
        name: 'tagIds',
        label: 'Tags',
        type: 'multi-select',
        placeholder: 'Select tags',
        options: {
          defaultValue: this.tracklist.tags.map((tag) => tag.id),
        },
        dataSelect: tags,
      },
      {
        columnSpan: 2,
        name: 'description',
        label: 'Description',
        type: 'textarea',
        options: {
          defaultValue: this.tracklist.description,
        },
      },
      {
        columnSpan: 2,
        name: 'privacy',
        label: isAlbum ? 'Album Privacy' : 'Playlist Privacy',
        type: 'radio',
        dataSelect: [
          { id: 'public', name: 'Public' },
          { id: 'private', name: 'Private' },
        ],
        options: {
          defaultValue: this.tracklist.privacy,
        },
        validators: [Validators.required],
      },
      ...(isAlbum
        ? [
            {
              columnSpan: 2,
              name: 'albumType',
              label: 'Album Type',
              type: 'select',
              dataSelect: [
                { id: 'ep', name: 'EP' },
                { id: 'album', name: 'Album' },
              ],
              validators: [Validators.required],
              options: {
                defaultValue: this.tracklist.albumType,
              },
            },
            {
              columnSpan: 2,
              name: 'albumLink',
              label: 'Album Link',
              type: 'text',
              validators: [Validators.required],
              options: {
                defaultValue: this.tracklist.link ?? '',
              },
            },
          ]
        : []),
    ];
  }
}
