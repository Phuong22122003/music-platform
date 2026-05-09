import { Component, Input, OnInit } from '@angular/core';
import { Track } from '../../../../../core/models/track';
import { ActivatedRoute } from '@angular/router';
import { TrackService } from '../../../../../core/services/track.service';
import { AuthService } from '../../../../../core/services/auth-service';
import { TrackStatisticsService } from '../../../../../core/services/track-statistic.service';

@Component({
  selector: 'app-track-tab',
  standalone: false,
  templateUrl: './track-tab.component.html',
  styleUrl: './track-tab.component.scss',
})
export class TrackTabComponent implements OnInit {
  routePath = '';
  isLoading = false;
  tracks: Track[] = [];
  constructor(
    private route: ActivatedRoute,
    private trackService: TrackService,
    private authService: AuthService,
    private trackStatisticService: TrackStatisticsService
  ) {}
  ngOnInit(): void {
    this.isLoading = true;
    const routePath = this.route.snapshot.url[0]?.path;
    this.route.parent?.params.subscribe((params) => {
      this.isLoading = true;
      if (params['userId']) {
        if (routePath === 'tracks') {
          this.trackService
            .getTracksByUserId(params['userId'])
            .subscribe((res) => {
              this.tracks = res.data;
              this.isLoading = false;
            });
        } else {
          this.trackStatisticService
            .getUserTopTracks(params['userId'], null, null)
            .subscribe((res) => {
              this.tracks = res.data
                .map((track) => track.track)
                .filter((track) => track !== null);
              this.isLoading = false;
            });
        }
      }
    });
  }
}
