import { SelectionModel } from '@angular/cdk/collections';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { TrackService } from '../../../core/services/track.service';
import { AuthService } from '../../../core/services/auth-service';
import { Track } from '../../../core/models/track';
import { AVATAR_BASE_URL, COVER_BASE_URL } from '../../../shared/utils/url';
import { LikedTrackService } from '../../../core/services/liked-track.service';
import { forkJoin } from 'rxjs';
import { CommentService } from '../../../core/services/comment.service';

@Component({
  selector: 'app-track-table',
  standalone: false,
  templateUrl: './track-table.component.html',
  styleUrl: './track-table.component.scss',
})
export class TrackTableComponent implements OnInit {
  displayedColumns: string[] = [
    'select',
    'thumbnail',
    'length',
    'date',
    'engagements',
    'plays',
    'actions',
  ];
  coverUrl = COVER_BASE_URL;
  dataSource: Track[] = [];
  selection = new SelectionModel<Track>(true, []);
  playId = '';
  isPlay = false;
  @Output() chosenTrackToEdit = new EventEmitter<Track>();
  @Output() chosenTrackToDelete = new EventEmitter<Track>();
  constructor(
    private trackService: TrackService,
    private likedTrackService: LikedTrackService,
    private authService: AuthService,
    private commentService: CommentService
  ) {}
  ngOnInit(): void {
    const userId = this.authService.getUserId() || '';

    this.trackService.getTracksByUserId(userId).subscribe((res) => {
      const tracks = res.data;

      const likeCountRequests = tracks.map((track: any) =>
        this.likedTrackService.getTrackLikeCount(track.id)
      );

      const commentCountRequests = tracks.map((track: any) =>
        this.commentService.getComments(track.id)
      );

      forkJoin([
        forkJoin(likeCountRequests),
        forkJoin(commentCountRequests),
      ]).subscribe(([likeCounts, commentResults]: [any[], any[]]) => {
        this.dataSource = tracks.map((track: any, index: number) => ({
          ...track,
          countLike: likeCounts[index]?.data ?? 0,
          countComment: commentResults[index]?.data?.length ?? 0,
        }));

        console.log(this.dataSource);
      });
    });

    this.trackService.updateDone$.subscribe((track) => {
      if (this.dataSource.length < 0) return;
      const editedIndex = this.dataSource.findIndex(
        (data) => data.id === track.id
      );
      console.log(editedIndex);
      if (editedIndex < 0) return;
      this.dataSource[editedIndex] = {
        ...track,
        displayName: this.dataSource[editedIndex].displayName,
      };
      this.dataSource = [...this.dataSource];
    });
    this.trackService.deleteDone$.subscribe((trackId) => {
      console.log(trackId);
      this.dataSource = this.dataSource.filter((data) => data.id !== trackId);
    });
    this.trackService.trackPlay$.subscribe((data) => {
      this.isPlay = data.isPlay;
      this.playId = data.track.id;
    });
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource);
  }

  onOpenEdit(track: Track) {
    this.chosenTrackToEdit.emit(track);
  }
  onClickDelete(track: Track) {
    this.chosenTrackToDelete.emit(track);
  }
  playMusic(track: Track) {
    if (this.playId === track.id) {
      this.isPlay = !this.isPlay;
    } else {
      this.isPlay = true;
    }
    this.playId = track.id;
    this.emitPlayTrack(track);
  }
  emitPlayTrack(track: Track) {
    this.trackService.emitTrackPlay({
      isPlay: this.isPlay,
      track: track,
    });
  }
}
