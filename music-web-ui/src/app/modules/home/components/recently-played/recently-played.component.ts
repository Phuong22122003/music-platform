import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Track } from '../../../../core/models/track';
import { HistoryService } from '../../../../core/services/history.service';
import { createPlaceholders } from '../../../../shared/utils/helper';

@Component({
  selector: 'app-recently-played',
  standalone: false,
  templateUrl: './recently-played.component.html',
  styleUrl: './recently-played.component.scss',
})
export class RecentlyPlayedComponent implements OnInit {
  tracks: Track[] = [];
  @ViewChild('tracksCard', { static: true }) tracksCard!: ElementRef;
  constructor(private historyService: HistoryService) {}
  ngOnInit(): void {
    this.historyService.getAllHistory().subscribe((res) => {
      this.tracks = res.data;
    });
  }
  scollLeft() {
    this.tracksCard.nativeElement.scrollBy({
      left: -200 * 5,
      behavior: 'smooth',
    });
  }
  scollRight() {
    this.tracksCard.nativeElement.scrollBy({
      left: 200 * 5,
      behavior: 'smooth',
    });
  }
  mockData() {
    const mock_tracks: Track[] = [];
    // for (let i = 0; i < 10; i++) {
    //   const track: Track = {
    //     id: `${i}`,
    //     name: `Track${i}`,
    //     userId: String(i),
    //     username: 'user' + i,
    //     fileName: './assets/audio/music.mp3',
    //     coverImagePath: '/assets/images/background/bg-galaxy-1.jpg',
    //     duration: '0:12',
    //     createdAt: '01/01/2025',
    //   };
    //   mock_tracks.push(track);
    // }
    return mock_tracks;
  }
  get placeholders(): any[] {
    return createPlaceholders(this.tracks);
  }
}
