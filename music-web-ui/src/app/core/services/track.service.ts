import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  catchError,
  forkJoin,
  map,
  Observable,
  of,
  Subject,
  switchMap,
} from 'rxjs';
import { ApiResponse } from '../models/api_response';
import { Track } from '../models/track';
import { ProfileService } from './profile.service';
@Injectable({
  providedIn: 'root', // Đăng ký service ở root để dùng toàn app
})
export class TrackService {
  private updateDoneSubject = new Subject<Track>();
  private deleteDoneSubject = new Subject<string>();
  private trackPlaySubject = new Subject<{ isPlay: boolean; track: Track }>();
  updateDone$ = this.updateDoneSubject.asObservable();
  deleteDone$ = this.deleteDoneSubject.asObservable();
  trackPlay$ = this.trackPlaySubject.asObservable();
  private apiUrl = environment.apiBaseUrl + '/music-service/track';
  private apiUrlTracks = environment.apiBaseUrl + '/music-service/tracks';
  constructor(
    private http: HttpClient,
    private profileService: ProfileService
  ) {}
  emitUpdateDone(track: Track) {
    this.updateDoneSubject.next(track);
  }
  emitDeleteDone(trackId: string) {
    this.deleteDoneSubject.next(trackId);
  }
  emitTrackPlay(payload: { isPlay: boolean; track: Track }) {
    this.trackPlaySubject.next(payload);
  }
  private enrichTracksWithDisplayNames(tracks: Track[]): Observable<Track[]> {
    if (tracks.length === 0) {
      return of([]);
    }
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
  getTrackById(trackId: string): Observable<ApiResponse<Track>> {
    return this.http.get<ApiResponse<Track>>(this.apiUrl + '/' + trackId).pipe(
      switchMap((res) =>
        this.profileService.getProfileById(res.data.userId).pipe(
          map((profileRes) => ({
            ...res,
            data: {
              ...res.data,
              displayName: profileRes.data.displayName,
            },
          })),
          catchError(() =>
            of({
              ...res,
              data: { ...res.data, displayName: 'Unknown User' },
            })
          )
        )
      )
    );
  }
  updateTrack(
    trackId: string,
    updateData: any,
    imageFile?: File,
    trackFile?: File
  ): Observable<any> {
    const formData = new FormData();

    // 1. Gửi JSON metadata
    formData.append(
      'meta-data',
      new Blob([JSON.stringify(updateData)], { type: 'application/json' })
    );

    // 2. Gửi file ảnh nếu có
    if (imageFile) {
      formData.append('image', imageFile);
    }

    // 3. Gửi file nhạc nếu có
    if (trackFile) {
      formData.append('track', trackFile);
    }

    return this.http.put(`${this.apiUrl}/update/${trackId}`, formData);
  }

  getTracksByIds(trackIds: string[]): Observable<ApiResponse<Track[]>> {
    let params = new HttpParams();

    trackIds.forEach((id) => {
      params = params.append('ids', id);
    });

    return this.http
      .get<ApiResponse<Track[]>>(
        environment.apiBaseUrl + '/music-service/tracks/bulk',
        { params }
      )
      .pipe(
        switchMap((res) => {
          return this.enrichTracksWithDisplayNames(res.data).pipe(
            map((tracksWithDisplayName) => ({
              ...res,
              data: tracksWithDisplayName,
            }))
          );
        })
      );
  }
  getTracksByUserId(userId: string): Observable<ApiResponse<Track[]>> {
    return this.http
      .get<ApiResponse<Track[]>>(`${this.apiUrl}/users/${userId}`)
      .pipe(
        switchMap((res) => {
          return this.enrichTracksWithDisplayNames(res.data).pipe(
            map((tracks) => ({ ...res, data: tracks }))
          );
        })
      );
  }

  getRelatedTracks(
    trackIds: string[],
    limit: number
  ): Observable<ApiResponse<Track[]>> {
    const params = {
      'track-ids': trackIds,
      limit: limit.toString(),
    };

    return this.http
      .get<ApiResponse<Track[]>>(`${this.apiUrl}/list-ids-related`, { params })
      .pipe(
        switchMap((res) =>
          this.enrichTracksWithDisplayNames(res.data).pipe(
            map((tracks) => ({ ...res, data: tracks }))
          )
        )
      );
  }

  getTracksByGenre(
    genreId: string,
    limit: number
  ): Observable<ApiResponse<Track[]>> {
    return this.http
      .get<ApiResponse<Track[]>>(`${this.apiUrl}/list-by-genre`, {
        params: { genreId, limit: limit.toString() },
      })
      .pipe(
        switchMap((res) =>
          this.enrichTracksWithDisplayNames(res.data).pipe(
            map((tracks) => ({ ...res, data: tracks }))
          )
        )
      );
  }

  getRandomTracks(limit: number): Observable<ApiResponse<Track[]>> {
    return this.http
      .get<ApiResponse<Track[]>>(`${this.apiUrl}/random`, {
        params: { limit: limit.toString() },
      })
      .pipe(
        switchMap((res) =>
          this.enrichTracksWithDisplayNames(res.data).pipe(
            map((tracks) => ({ ...res, data: tracks }))
          )
        )
      );
  }

  deleteTrack(trackId: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(
      `${this.apiUrlTracks}/${trackId}`
    );
  }
}
