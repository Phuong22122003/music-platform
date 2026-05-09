import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DynamicFormComponent } from '../../dynamic-form/dynamic-form.component';
import { Validators } from '@angular/forms';
import { TrackList } from '../../../../core/models/tracklist';
import { PlaylistService } from '../../../../core/services/playlist_service';
import { UpdatePlaylistInfoRequest } from '../../../../core/models/playlist/playlist_request.model';
import { ToastrService } from 'ngx-toastr';
import { isThisISOWeek } from 'date-fns';
import { AlbumService } from '../../../../core/services/album.service';
import {
  AddTrackAlbumRequest,
  AlbumRequest,
} from '../../../../core/models/album/album_request';
import { FileService } from '../../../../core/services/file.service';
import { ProfileService } from '../../../../core/services/profile.service';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-edit-playlist',
  standalone: false,
  templateUrl: './edit-playlist.component.html',
  styleUrl: './edit-playlist.component.scss',
})
export class EditPlaylistComponent implements OnInit {
  updateData!: any;
  coverUpdate!: File;

  @Output() cancelTracklist = new EventEmitter<void>();
  @Output() updateInfo = new EventEmitter<void>();
  @Input() formFields: {
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
  @Input() tracklist!: TrackList;
  listDisplayNameTrack: string[] = [];
  newTracklist!: TrackList;
  activeTab: 'info' | 'tracks' = 'info';
  coverUrl = environment.fileApi + '/images/covers';
  constructor(
    private playlistService: PlaylistService,
    private albumService: AlbumService,
    private toast: ToastrService,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.newTracklist = JSON.parse(JSON.stringify(this.tracklist));
    this.updateData = this.buildUpdateDataFromTracklist(this.tracklist);
    this.tracklist.tracks.forEach((track) => {
      this.profileService.getProfileById(track.userId).subscribe((res) => {
        this.listDisplayNameTrack.push(res.data.displayName);
      });
    });
  }

  removeTrack(id: string) {
    this.newTracklist.tracks = this.newTracklist.tracks.filter(
      (track) => track.id !== id
    );
  }

  onSubmitInfo(formData: any) {
    const isPlaylist = this.tracklist.type === 'playlist';
    if (isPlaylist) {
      this.updateData = {
        id: this.tracklist.listId,
        title: formData['title'],
        description: formData['description'],
        privacy: formData['privacy'].toLowerCase(),
        releaseDate: formData['releaseDate'],
        tagIds: formData['tagIds'],
        trackIds: this.newTracklist?.tracks?.map((track) => track.id) ?? [],
        ...(formData['genreId'] ? { genreId: formData['genreId'] } : {}),
      };
    } else {
      this.updateData = {
        albumTitle: formData['albumTitle'],
        albumLink: formData['albumLink'],
        albumType: formData['albumType'],
        description: formData['description'],
        ...(formData['genreId'] ? { genreId: formData['genreId'] } : {}),
        mainArtists: formData['mainArtists'],
        privacy: formData['privacy'].toLowerCase(),
        tagsId: formData['tagIds'],
      };
    }

    console.log(this.updateData);
    this.submitUpdate();
  }
  onImageSelected(image: any) {
    this.coverUpdate = image;
  }
  trackById(index: number, track: any): any {
    return track.id;
  }
  onCancelTracklist() {
    this.cancelTracklist.emit();
  }
  onSubmitTracklist() {
    this.updateData = this.buildUpdateDataFromTracklist(this.newTracklist);
    this.submitUpdate();
  }

  private buildUpdateDataFromTracklist(tracklist: TrackList): any {
    const isPlaylist = tracklist.type === 'playlist';

    if (isPlaylist) {
      return {
        id: tracklist.listId,
        description: tracklist.description,
        privacy: tracklist.privacy.toLowerCase(),
        releaseDate: tracklist.releaseDate,
        tagIds: tracklist.tags,
        title: tracklist.listname,
        trackIds: tracklist.tracks?.map((track) => track.id) ?? [],

        ...(tracklist.genre ? { genreId: tracklist.genre } : {}),
      };
    } else {
      const albumRequest: AlbumRequest = {
        albumTitle: tracklist.listname,
        albumLink: tracklist.link || tracklist.listId,
        albumType: tracklist.albumType || 'album',
        description: tracklist.description,
        ...(tracklist.genre ? { genreId: tracklist.genre.id } : {}),
        mainArtists: tracklist.displayName || '',
        privacy: tracklist.privacy.toLowerCase(),
        tagsId: tracklist.tags.map((tag) => tag.id),
        trackIds: tracklist.tracks?.map((track) => track.id) ?? [],
      };

      return albumRequest;
    }
  }

  private submitUpdate() {
    const isPlaylist = this.tracklist.type === 'playlist';
    console.log(isPlaylist);
    console.log(this.updateData);
    const updateObservable = isPlaylist
      ? this.playlistService.updatePlaylistInfo(
          this.coverUpdate,
          this.updateData
        )
      : this.albumService.updateAlbum(
          this.tracklist.listId,
          this.updateData,
          this.coverUpdate
        );

    updateObservable.subscribe({
      next: () => {
        const successMsg = isPlaylist
          ? 'Update playlist successfully'
          : 'Update album successfully';
        this.toast.success(successMsg);
        this.updateInfo.emit();
      },
      error: (err) => {
        console.error(err);
        const errorMsg = isPlaylist
          ? 'Fail to update playlist'
          : 'Fail to update album';
        this.toast.error(errorMsg);
      },
    });
  }
}
