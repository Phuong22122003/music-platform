import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, switchMap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Track } from '../models/track';
import { ApiResponse } from '../models/api_response';
import { TrackEnricherService } from './track-enricher.service';

@Injectable({
  providedIn: 'root',
})
export class LikedTrackService {
  private apiUrl = `${environment.apiBaseUrl}/user-library/liked`;

  constructor(
    private http: HttpClient,
    private trackEnricher: TrackEnricherService
  ) {}

  getAllLikedTracks(): Observable<ApiResponse<Track[]>> {
    return this.http.get<ApiResponse<Track[]>>(`${this.apiUrl}/all`).pipe(
      switchMap((res) => {
        return this.trackEnricher
          .enrichTracksWithDisplayNames(res.data)
          .pipe(map((enrichedTracks) => ({ ...res, data: enrichedTracks })));
      })
    );
  }

  getTrackLikeCount(trackId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.apiUrl}/count/${trackId}`
    );
  }

  isTrackLiked(trackId: string): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(
      `${this.apiUrl}/is_liked/${trackId}`
    );
  }

  likeTrack(trackId: string): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(
      `${this.apiUrl}/like/${trackId}`,
      {}
    );
  }

  unLikeTrack(trackId: string): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(
      `${this.apiUrl}/unlike/${trackId}`,
      {}
    );
  }

  getUserIdsLikedTrack(trackId: string): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(
      `${this.apiUrl}/users/${trackId}`
    );
  }
}
