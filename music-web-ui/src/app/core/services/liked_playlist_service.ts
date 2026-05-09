import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, forkJoin, map, switchMap } from 'rxjs';
import { ErrorHandlerService } from './error-handler-service';
import { ApiResponse } from '../models/api_response';
import { LikedPlaylistResponse } from '../models/playlist/liked_playlist_response';
import { environment } from '../../../environments/environment';
import { ProfileService } from './profile.service';
import { Track } from '../models/track';
import { PlaylistResponse } from '../models/playlist/playlist.model';
import { TrackList } from '../models/tracklist';

@Injectable({
  providedIn: 'root',
})
export class LikedPlaylistService {
  private apiUrl = `${environment.apiBaseUrl}/user-library/playlist/liked`; // hoặc URL tương ứng với backend

  constructor(
    private http: HttpClient,
    private profileService: ProfileService
  ) {}

  getAllLikedPlaylists(): Observable<ApiResponse<TrackList[]>> {
    return this.http
      .get<ApiResponse<LikedPlaylistResponse[]>>(`${this.apiUrl}/all`)
      .pipe(
        switchMap((response) => {
          console.log(response);
          const likedPlaylistItems = response.data;

          const requests = likedPlaylistItems.map((likedPlaylist) => {
            const playlist = likedPlaylist.playlist;
            return this.getProfileForTrackList(playlist.userId).pipe(
              map((profileRes) => {
                const trackList: TrackList = {
                  ...this.mapToTrackListFromLiked(playlist),
                  displayName: profileRes.data.displayName,
                };
                return trackList;
              })
            );
          });

          return forkJoin(requests).pipe(
            map((trackLists: TrackList[]) => ({
              ...response,
              data: trackLists,
            }))
          );
        })
      );
  }

  isLiked(playlistId: string): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(
      `${this.apiUrl}/is_liked/${playlistId}`
    );
  }

  likePlaylist(playlistId: string): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(
      `${this.apiUrl}/like/${playlistId}`,
      {}
    );
  }

  unLikePlaylist(playlistId: string): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(
      `${this.apiUrl}/unlike/${playlistId}`,
      {}
    );
  }
  countLike(playlistId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.apiUrl}/count/${playlistId}`
    );
  }
  private mapToTrackListFromLiked(playlist: any): TrackList {
    return {
      listname: playlist.title,
      listId: playlist.id,
      releaseDate: playlist.releaseDate,
      description: playlist.description,
      privacy: playlist.privacy,
      userId: playlist.userId,
      genre: playlist.genre?.name ?? '',
      displayName: '', // gán sau khi enrich profile
      tracks: playlist.playlistTracks,
      likeCount: 0,
      imagePath: playlist.imagePath,
      isLiked: playlist.isLiked ?? false,
      type: 'playlist',
      tags: playlist.playlistTags?.map((tag: any) => tag.name) || [],
    };
  }
  private getProfileForTrackList(userId: string): Observable<any> {
    return this.profileService.getProfileById(userId);
  }
}
