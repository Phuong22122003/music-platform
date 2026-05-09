import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Track } from '../models/track';
import { ProfileService } from './profile.service';

@Injectable({
  providedIn: 'root',
})
export class TrackEnricherService {
  constructor(private profileService: ProfileService) {}

  enrichTracksWithDisplayNames(tracks: Track[]): Observable<Track[]> {
    if (!tracks || tracks.length === 0) return of([]);

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

  enrichTrackWithDisplayName(track: Track): Observable<Track> {
    if (!track)
      return of({ id: '', userId: '', displayName: 'Unknown' } as Track);

    return this.profileService.getProfileById(track.userId).pipe(
      map((res) => ({
        ...track,
        displayName: res.data.displayName,
      })),
      catchError(() =>
        of({
          ...track,
          displayName: 'Unknown User',
        })
      )
    );
  }
}
