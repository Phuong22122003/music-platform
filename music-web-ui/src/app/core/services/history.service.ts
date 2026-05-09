import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { ProfileService } from './profile.service';
import { ErrorHandlerService } from './error-handler-service';
import { catchError, forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { Track } from '../models/track';
import { ApiResponse } from '../models/api_response';
@Injectable({
  providedIn: 'root',
})
export class HistoryService {
  private apiUrl = `${environment.apiBaseUrl}/user-library/history`;

  constructor(
    private http: HttpClient,
    private profileService: ProfileService
  ) {}

  private enrichTracksWithDisplayNames(tracks: Track[]): Observable<Track[]> {
    const userIds = [...new Set(tracks.map((track) => track.userId))];

    const profileRequests = userIds.map((userId) =>
      this.profileService.getProfileById(userId).pipe(
        map((res) => ({ userId, displayName: res.data.displayName })),
        catchError(() => of({ userId, displayName: 'Unknown User' }))
      )
    );

    return forkJoin(profileRequests).pipe(
      map((profiles) => {
        const profileMap = new Map(
          profiles.map((p) => [p.userId, p.displayName])
        );
        return tracks.map((track) => ({
          ...track,
          displayName: profileMap.get(track.userId) || 'Unknown',
        }));
      })
    );
  }

  getAllHistory(): Observable<ApiResponse<Track[]>> {
    return this.http.get<ApiResponse<Track[]>>(`${this.apiUrl}/all`).pipe(
      switchMap((res) => {
        return this.enrichTracksWithDisplayNames(res.data).pipe(
          map((enriched) => ({ ...res, data: enriched }))
        );
      })
    );
  }

  listenTrack(trackId: string): void {
    this.http.post(`${this.apiUrl}?track_id=${trackId}`, {}).subscribe({
      next: () => {},
      error: (err) => {
        console.error('Gọi listenTrack thất bại:', err);
      },
    });
  }

  clearAllHistory(): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/all`);
  }

  deleteHistoryById(trackId: string): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(
      `${this.apiUrl}/delete/${trackId}`
    );
  }
}
