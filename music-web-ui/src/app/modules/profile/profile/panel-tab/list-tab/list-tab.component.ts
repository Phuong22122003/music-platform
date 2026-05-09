import { Component, OnInit } from '@angular/core';
import { PlaylistService } from '../../../../../core/services/playlist_service';
import { TrackList } from '../../../../../core/models/tracklist';
import { MatDialog } from '@angular/material/dialog';
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from '../../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { openConfirmDialog } from '../../../../../shared/utils/dialog';
import { ActivatedRoute } from '@angular/router';
import { AlbumService } from '../../../../../core/services/album.service';

@Component({
  selector: 'app-list-tab',
  standalone: false,
  templateUrl: './list-tab.component.html',
  styleUrl: './list-tab.component.scss',
})
export class ListTabComponent implements OnInit {
  trackLists: TrackList[] = [];
  isLoading = false;
  constructor(
    private playlistService: PlaylistService,
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private albumService: AlbumService
  ) {}
  ngOnInit(): void {
    const routePath = this.route.snapshot.url[0]?.path;
    const userId = this.route.parent?.snapshot.paramMap.get('userId');
    if (!userId) return;
    if (routePath === 'playlists') {
      this.playlistService.getAllPlaylists(userId).subscribe((res) => {
        this.trackLists = [...res.data];
      });
    } else {
      console.log('albums');
      this.albumService.getAlbumsByUserId(userId).subscribe((res) => {
        this.trackLists = [...res.data];
      });
    }
  }
  deleteEvent(listId: any) {
    console.log('======', listId);
    this.trackLists = this.trackLists.filter(
      (trackList) => trackList.listId !== listId
    );
  }
}
