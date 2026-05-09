import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { PinturaEditorOptions } from '@pqina/pintura';
import { openEditor } from '../../../../shared/utils/image-edit';
import { Validators } from '@angular/forms';
import { TrackService } from '../../../../core/services/track.service';
import { Track } from '../../../../core/models/track';
import { MusicService } from '../../../../core/services/music.service';
import { TrackRequest } from '../../../../core/models/track/track_request.model';
import { AuthService } from '../../../../core/services/auth-service';
import { ToastrService } from 'ngx-toastr';
import { TagService } from '../../../../core/services/tag_service';
import { GenreService } from '../../../../core/services/genre_service';
import { TagResponse } from '../../../../core/models/tag/tag_response.model';
import { GenreResponse } from '../../../../core/models/genre/genre_response.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-upload-track',
  standalone: false,
  templateUrl: './upload-track.component.html',
  styleUrl: './upload-track.component.scss',
})
export class UploadTrackComponent implements OnInit {
  @Input('trackFile') trackFile!: File;
  Validators = Validators;
  selectedImageFile!: File;
  metaData: any;
  genres: GenreResponse[] = [];
  tags: TagResponse[] = [];
  loggedUserId: string = '';
  isLoading = false;
  constructor(
    private musicService: MusicService,
    private authService: AuthService,
    private toast: ToastrService,
    private tagService: TagService,
    private genreService: GenreService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loggedUserId = this.authService.getUserId() || '';
    this.tagService.getAllTags().subscribe((res) => {
      this.tags = res.data;
    });
    this.genreService.getAllGenres().subscribe((res) => {
      this.genres = res.data;
    });
  }

  onEditImage(image: File) {
    this.selectedImageFile = image;
  }

  onSubmitMetadata(metaData: any) {
    if (!this.loggedUserId) {
      return;
    }

    const trackRequest: TrackRequest = {
      countPlay: 0,
      privacy: metaData.privacy,
      title: metaData.title,
      genreId: metaData.genre || '',
      tagIds: metaData.tags || [],
      description: metaData.description,
      userId: this.loggedUserId,
    };
    this.isLoading = true;
    this.musicService
      .uploadTrack(this.selectedImageFile, this.trackFile, trackRequest)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          this.toast.success('Upload thành công');
          this.router.navigate(['/']);
        },
        error: (error) => {
          this.toast.error('Upload thất bại: ' + error.message);
          this.isLoading = false;
        },
      });
  }
}
