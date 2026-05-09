import {
  Component,
  ElementRef,
  input,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
  AfterViewInit,
  ChangeDetectorRef,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { TrackCreation } from '../../../../core/models/TrackCreation';
import { formatDuration } from '../../../../shared/utils/track';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { AlbumService } from '../../../../core/services/album.service';
import { ApiResponse } from '../../../../core/models/api_response';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../../core/services/auth-service';
import { GenreResponse } from '../../../../core/models/genre/genre_response.model';
import { TagResponse } from '../../../../core/models/tag/tag_response.model';
import { TagService } from '../../../../core/services/tag_service';
import { GenreService } from '../../../../core/services/genre_service';
import { Router } from '@angular/router';

interface TrackFile {
  file: File;
  tempId: string;
}

interface TrackCreationWithId extends TrackCreation {
  tempId: string;
}

@Component({
  selector: 'app-upload-album',
  standalone: false,
  templateUrl: './upload-album.component.html',
  styleUrl: './upload-album.component.scss',
})
export class UploadAlbumComponent implements OnInit, OnDestroy {
  Validators = Validators;
  @Input('trackFiles') inputTrackFiles!: File[];
  @ViewChild('uploadedTracksSection') uploadedTracksSection!: ElementRef;
  @ViewChild('audio') audio!: ElementRef;

  selectedImageFile!: File;
  isOpenEditPanel = false;
  formatDuration = formatDuration;
  trackCreations: TrackCreationWithId[] = [];
  currentIndex: number | null = null;
  infoTrackList: {
    duration: number;
    fileName: string;
    srcAudio: string;
    isPlay: boolean;
  }[] = [];
  selectedIndexToEdit: number = -1;
  metaData: any;
  editedImageUrl: string = '';
  userId: string = '';
  genres: GenreResponse[] = [];
  tags: TagResponse[] = [];
  albumTypes = ['EP', 'ALBUM'];
  isScroll = false;
  trackFiles: TrackFile[] = [];
  isSubmitting = false;
  showTrackSection = false;

  get hasInvalidTracks(): boolean {
    return this.trackCreations.some(
      (track) => !track.title || track.title.trim().length === 0
    );
  }

  ngOnInit(): void {
    this.getTrackDetails();
    const authUser = this.authService.getUserId();
    if (!authUser) return;
    this.userId = authUser;
    this.tagService.getAllTags().subscribe((res) => {
      this.tags = res.data;
    });
    this.genreService.getAllGenres().subscribe((res) => {
      this.genres = res.data;
    });
  }

  constructor(
    private albumService: AlbumService,
    private authService: AuthService,
    private toastr: ToastrService,
    private tagService: TagService,
    private genreService: GenreService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  getTrackDetails() {
    this.inputTrackFiles.forEach((file) => {
      this.extractInfoFromTrackFile(file);
    });
  }
  onSubmitMetadata(metaData: any) {
    this.metaData = metaData;
    this.showTrackSection = true;

    // Sử dụng setTimeout để đảm bảo DOM đã được cập nhật
    setTimeout(() => {
      this.scrollToSection();
    });
  }
  onSubmit() {
    console.log('=vaoday=');
    let isInvalid = this.trackCreations.some(
      (trackCreation) => trackCreation.title.length <= 0
    );
    if (isInvalid) {
      this.toastr.error('Please fill in all track titles');
      return;
    }

    this.isSubmitting = true;

    const formData = new FormData();
    // 1. Meta data (AlbumRequest)
    formData.append(
      'meta-data',
      new Blob([JSON.stringify(this.metaData)], { type: 'application/json' })
    );

    // 2. Cover album image
    if (this.selectedImageFile) {
      formData.append('cover-album', this.selectedImageFile);
    }

    // 3. Track requests
    formData.append(
      'track-request',
      new Blob([JSON.stringify(this.trackCreations)], {
        type: 'application/json',
      })
    );

    // 4. Track files (MP3,...) - Add files in the same order as trackCreations
    this.trackCreations.forEach((track) => {
      const matchingFileObj = this.trackFiles.find(
        (tf) => tf.tempId === track.tempId
      );
      if (matchingFileObj) {
        formData.append('track-files', matchingFileObj.file);
      }
    });
    console.log(formData.getAll('track-files'));
    console.log(formData.getAll('track-request'));
    console.log(formData.getAll('meta-data'));
    // 5. Gửi request
    this.albumService.uploadAlbum(formData).subscribe({
      next: (res: ApiResponse<any>) => {
        this.showSuccess('Upload thành công');
        this.router.navigate(['/']);
      },
      error: (err: ApiResponse<any>) => {
        this.showFail('Upload thất bại:' + err.message);
        this.isSubmitting = false;
        this.showTrackSection = false;
        this.metaData = null; // Reset metadata to force user to resubmit form
      },
    });
  }
  scrollToSection() {
    if (!this.uploadedTracksSection) return;

    this.uploadedTracksSection.nativeElement.scrollIntoView({
      behavior: 'smooth',
    });
  }

  onDeleteTrack(index: number) {
    URL.revokeObjectURL(this.infoTrackList[index].srcAudio);
    const deletedTrack = this.trackCreations[index];
    this.trackCreations.splice(index, 1);
    this.infoTrackList.splice(index, 1);
    // Remove from trackFiles using tempId
    const fileIndex = this.trackFiles.findIndex(
      (tf) => tf.tempId === deletedTrack.tempId
    );
    if (fileIndex !== -1) {
      this.trackFiles.splice(fileIndex, 1);
    }
  }
  onEditTrack(index: number) {
    this.selectedIndexToEdit = index;
  }
  onSubmitEdit(metaData: TrackCreation) {
    if (this.selectedIndexToEdit < 0) return;

    const existingTempId = this.trackCreations[this.selectedIndexToEdit].tempId;
    this.trackCreations[this.selectedIndexToEdit] = {
      ...metaData,
      tempId: existingTempId,
      privacy: 'public',
    };
    console.log(this.trackCreations[this.selectedIndexToEdit]);
    this.selectedIndexToEdit = -1;
  }
  onEditImage(image: File) {
    this.selectedImageFile = image;
  }
  ngOnDestroy() {
    this.infoTrackList.forEach((track) => URL.revokeObjectURL(track.srcAudio));
  }
  playAudio(index: number) {
    const audio = this.audio.nativeElement;
    const track = this.infoTrackList[index];

    // Nếu đang phát và nhấn vào cùng bài -> Pause
    if (this.currentIndex === index && !audio.paused) {
      audio.pause();
      this.infoTrackList[index].isPlay = false;
      this.currentIndex = null;
      return;
    }

    // Nếu có bài đang phát khác -> Dừng nó
    if (this.currentIndex !== null) {
      this.infoTrackList[this.currentIndex].isPlay = false;
    }

    // Phát bài mới
    audio.src = track.srcAudio;
    audio.play();
    this.currentIndex = index;
    this.infoTrackList[index].isPlay = true;

    // Khi phát xong, reset trạng thái
    audio.onended = () => {
      this.infoTrackList[index].isPlay = false;
      this.currentIndex = null;
    };
  }
  onSelectedNewTracks(event: Event) {
    const inputFile = event.target as HTMLInputElement;
    if (inputFile.files && inputFile.files.length > 0) {
      for (const file of inputFile.files) {
        this.extractInfoFromTrackFile(file);
      }
    }
  }
  extractInfoFromTrackFile(trackFile: File) {
    const audio = new Audio();
    const objectURL = URL.createObjectURL(trackFile);
    const tempId = Math.random().toString(36).substr(2, 9);

    audio.src = objectURL;
    audio.addEventListener('loadedmetadata', () => {
      this.infoTrackList.push({
        duration: audio.duration,
        fileName: trackFile.name,
        srcAudio: audio.src,
        isPlay: false,
      });

      const newTrack: TrackCreationWithId = {
        title: trackFile.name.substring(0, trackFile.name.lastIndexOf('.')),
        userId: this.userId,
        privacy: 'public',
        mainArtists: 'user1',
        genreId: '',
        tagIds: [],
        tempId: tempId,
      };
      this.trackCreations.push(newTrack);

      // Store file with its tempId
      this.trackFiles.push({ file: trackFile, tempId: tempId });
    });
  }
  drop(event: CdkDragDrop<any>) {
    moveItemInArray(
      this.trackCreations,
      event.previousIndex,
      event.currentIndex
    );
    moveItemInArray(this.trackFiles, event.previousIndex, event.currentIndex);
    moveItemInArray(
      this.infoTrackList,
      event.previousIndex,
      event.currentIndex
    );
  }
  trackByTempId(index: number, item: any): number {
    return item.tempId;
  }

  showSuccess(msg: string) {
    this.toastr.success(msg, 'Success');
  }

  showFail(msg: string) {
    this.toastr.error(msg, 'Fail');
  }

  closeEditTrack() {
    this.selectedIndexToEdit = -1;
  }
}
