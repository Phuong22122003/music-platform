import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { ProfileService } from './profile.service';
import { catchError, forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { Track } from '../models/track';
import { ApiResponse } from '../models/api_response';
import { UserProfile } from '../models/user_profile';
import { TrackEnricherService } from './track-enricher.service';

@Injectable({
  providedIn: 'root',
})
export class RecommendedService {
  private readonly baseUrl =
    environment.apiBaseUrl + '/user-library/recommended';

  constructor(
    private http: HttpClient,
    private profileService: ProfileService,
    private trackEnricher: TrackEnricherService
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

  // 1. Mixed for user
  getMixedForUser(): Observable<ApiResponse<Track[]>> {
    return this.http
      .get<ApiResponse<Track[]>>(`${this.baseUrl}/mixed-for`)
      .pipe(
        switchMap((res) => {
          console.log(res);
          return this.trackEnricher
            .enrichTracksWithDisplayNames(res.data)
            .pipe(map((enrichedTracks) => ({ ...res, data: enrichedTracks })));
        })
      );
  }

  // 2. Related Track (TrackResponse[])
  getRelatedTrack(trackId: string): Observable<ApiResponse<Track[]>> {
    return this.http
      .get<ApiResponse<Track[]>>(`${this.baseUrl}/related-track/${trackId}`)
      .pipe(
        switchMap((res) =>
          this.enrichTracksWithDisplayNames(res.data).pipe(
            map((tracksWithDisplayName) => ({
              ...res,
              data: tracksWithDisplayName,
            }))
          )
        )
      );
  }

  // 3. Grouped by Genre (Map<String, List<TrackResponse>>)
  // getGroupedTracksByGenre(): Observable<
  //   ApiResponse<{ [genre: string]: Track[] }>
  // > {
  //   return this.http
  //     .get<ApiResponse<{ [genre: string]: Track[] }>>(
  //       `${this.baseUrl}/group-by-genre`
  //     )
  //     .pipe(
  //       switchMap((res) => {
  //         const allTracks: Track[] = Object.values(res.data).flat();
  //         return this.enrichTracksWithDisplayNames(allTracks).pipe(
  //           map((tracksWithDisplayName) => {
  //             // rebuild map after enriching
  //             const genreMap: { [genre: string]: Track[] } = {};
  //             for (const genre of Object.keys(res.data)) {
  //               genreMap[genre] = tracksWithDisplayName.filter(
  //                 (track) => track.genre === genre
  //               );
  //             }
  //             return {
  //               ...res,
  //               data: genreMap,
  //             };
  //           })
  //         );
  //       }),
  //       catchError(this.errorHandlerService.handleError)
  //     );
  // }

  // 4. More of what you like (any response)
  getMoreOfWhatYouLike(): Observable<ApiResponse<Track[]>> {
    return this.http
      .get<ApiResponse<Track[]>>(`${this.baseUrl}/more-of-you-like`)
      .pipe(
        switchMap((res) =>
          this.enrichTracksWithDisplayNames(res.data).pipe(
            map((tracksWithDisplayName) => ({
              ...res,
              data: tracksWithDisplayName,
            }))
          )
        )
      );
  }
  getArtistYouShouldKnow(): Observable<ApiResponse<UserProfile[]>> {
    return this.http.get<ApiResponse<UserProfile[]>>(
      `${this.baseUrl}/artist-you-should-know`
    );
  }
}
