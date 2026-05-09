import { Component, OnInit } from '@angular/core';
import { TrackAndWave } from '../../../core/models/track_wave';
import { Track } from '../../../core/models/track';
import { HistoryService } from '../../../core/services/history.service';
import { createPlaceholders } from '../../../shared/utils/helper';

@Component({
  selector: 'app-history',
  standalone: false,
  templateUrl: './history.component.html',
  styleUrl: './history.component.scss',
})
export class HistoryComponent implements OnInit {
  tracks: Track[] = [];
  searchQuery = '';
  constructor(private historyService: HistoryService) {}
  ngOnInit(): void {
    this.historyService.getAllHistory().subscribe((res) => {
      this.tracks = res.data;
      console.log(res);
    });
  }
  get filteredTracks(): Track[] {
    const query = this.searchQuery.toLowerCase();

    return this.tracks.filter(
      (track) =>
        track.title?.toLowerCase().includes(query) ||
        track.displayName?.toLowerCase().includes(query)
    );
  }
  get placeholders(): any[] {
    return createPlaceholders(this.tracks);
  }
}
