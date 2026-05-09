import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Validators } from '@angular/forms';
import { Track } from '../../core/models/track';
import { FileService } from '../../core/services/file.service';
import { firstValueFrom, pipe } from 'rxjs';
import { getMatFormFieldMissingControlError } from '@angular/material/form-field';
import { TrackService } from '../../core/services/track.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { openConfirmDialog } from '../../shared/utils/dialog';
import { AuthService } from '../../core/services/auth-service';
import { Router } from '@angular/router';
import { UserProfile } from '../../core/models/user_profile';
import { ProfileService } from '../../core/services/profile.service';
import { AVATAR_BASE_URL } from '../../shared/utils/url';
import { TagService } from '../../core/services/tag_service';
import { GenreService } from '../../core/services/genre_service';

@Component({
  selector: 'app-track-management',
  standalone: false,
  templateUrl: './track-management.component.html',
  styleUrl: './track-management.component.scss',
})
export class TrackManagementComponent implements OnInit, OnDestroy {
  Validators = Validators;
  isOpenEditTrack = false;
  trackToEdit!: Track;
  trackToDelete!: Track;
  formField: any = [];
  isUpdateFormDone = false;
  coverImage!: File;
  userId = '';
  profile!: UserProfile;
  avatarUrl = AVATAR_BASE_URL;
  constructor(
    private fileService: FileService,
    private trackService: TrackService,
    private toast: ToastrService,
    private dialog: MatDialog,
    private authService: AuthService,
    private router: Router,
    private profileService: ProfileService,
    private tagService: TagService,
    private genreService: GenreService
  ) {}
  ngOnInit(): void {
    this.userId = this.authService.getUserId() || '';
    if (!this.userId) return;
    this.profileService.getProfileById(this.userId).subscribe((res) => {
      this.profile = res.data;
    });
  }
  toggleEditTracK() {
    this.isOpenEditTrack = !this.isOpenEditTrack;
  }
  chosenTrackToEdit(track: Track) {
    this.isOpenEditTrack = true;
    this.trackToEdit = track;
    this.updateFormField();
  }

  onEditTrackClose() {
    this.isOpenEditTrack = false;
  }

  async updateFormField() {
    console.log('updateFormField', this.trackToEdit);
    this.isUpdateFormDone = false;
    let coverImageValue: File | undefined;
    if (this.trackToEdit.coverImageName) {
      coverImageValue = await firstValueFrom(
        this.fileService.getCoverImage(this.trackToEdit.coverImageName)
      );
      this.coverImage = coverImageValue;
    }
    const genre = (await firstValueFrom(this.genreService.getAllGenres())).data;
    const tags = (await firstValueFrom(this.tagService.getAllTags())).data;
    this.formField = [
      {
        name: 'image',
        label: 'Upload Image',
        type: 'image',
        options: {
          widthImage: 300,
          heighImage: 300,
          defaultValue: coverImageValue,
          imageOrientation: 'vertical',
        },
      },
      {
        columnSpan: 2,
        name: 'title',
        label: 'Track Title',
        type: 'text',
        placeholder: 'Enter track title',
        validators: [Validators.required],
        options: {
          defaultValue: this.trackToEdit.title,
        },
      },
      {
        columnSpan: 2,
        name: 'genreId',
        label: 'Genre',
        type: 'select',
        placeholder: 'Select a genre',
        options: {
          defaultValue: this.trackToEdit.genre?.id,
        },
        dataSelect: genre,
      },
      {
        columnSpan: 2,
        name: 'tagIds',
        label: 'Tags',
        type: 'multi-select',
        placeholder: 'Select tags',
        options: {
          defaultValue: this.trackToEdit.tags?.map((tag) => tag.id),
        },
        dataSelect: tags,
      },
      {
        columnSpan: 2,
        name: 'description',
        label: 'Description',
        type: 'textarea',
        options: { defaultValue: this.trackToEdit.description },
        placeholder: 'Enter description',
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
          defaultValue: this.trackToEdit.privacy,
        },
        validators: [Validators.required],
      },
    ];
    this.isUpdateFormDone = true;
  }

  onUpdateTrack(updataData: any) {
    console.log(updataData);
    if (!this.trackToEdit) return;
    this.trackService
      .updateTrack(this.trackToEdit.id, updataData, this.coverImage)
      .subscribe((res) => {
        this.trackService.emitUpdateDone(res.data);
        this.toast.success('Update successfully');
        this.onEditTrackClose();
      });
  }
  onFileSelected(file: File) {
    this.coverImage = file;
  }
  onDeleteTrack(track: Track) {
    openConfirmDialog(
      this.dialog,
      {
        title: 'Permanently delete this track?',
        cancelText: 'Cancel',
        confirmText: 'Delete forever',
        description:
          'Removing this track is irreversible. You will lose all the plays, likes, and comments for this track with no way to get them back.',
      },
      () => {
        this.trackService.deleteTrack(track.id).subscribe((res) => {
          this.toast.success('Delete Successfully');
          this.trackService.emitDeleteDone(track.id);
        });
      }
    );
  }
  onLogout() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }
  onNavigateProfile() {
    this.router.navigate(['/profile', this.userId]);
  }
  ngOnDestroy(): void {
    this.isOpenEditTrack = false;
  }
}
