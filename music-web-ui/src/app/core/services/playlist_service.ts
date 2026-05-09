import { environment } from '../../../environments/environment';
import { HttpClient, JsonpClientBackend } from '@angular/common/http';
import { ApiResponse } from '../models/api_response';
import { forkJoin, map, Observable, switchMap } from 'rxjs';
import {
  PlaylistResponse,
  PlaylistTypeResponse,
} from '../models/playlist/playlist.model';
import { Injectable } from '@angular/core';
import {
  AddPlaylistRequest,
  AddPlaylistTrackRequest,
  UpdatePlaylistInfoRequest,
} from '../models/playlist/playlist_request.model';
import { PlaylistTrackResponse } from '../models/playlist/playlist_track';
import { TrackList } from '../models/tracklist';
import { ProfileService } from './profile.service';
import { TagResponse } from '../models/tag/tag_response.model';
@Injectable({
  providedIn: 'root',
})
export class PlaylistService {
  private baseUrl = `${environment.apiBaseUrl}/user-library/playlist`;
  private trackUrl = `${environment.apiBaseUrl}/user-library/playlist/tracks`;

  constructor(
    private http: HttpClient,
    private profileService: ProfileService
  ) {}

  private getProfileForTrackList(userId: string): Observable<any> {
    return this.profileService.getProfileById(userId);
  }

  getAllPlaylists(userId: string): Observable<ApiResponse<TrackList[]>> {
    return this.http
      .get<ApiResponse<any[]>>(`${this.baseUrl}/users/${userId}`)
      .pipe(
        switchMap((response) => {
          const playlistItems = response.data;
          console.log(response);
          const requests = playlistItems.map((item) =>
            this.getProfileForTrackList(item.userId).pipe(
              map((profileRes) => {
                const trackList: TrackList = {
                  ...this.mapToTrackList(item),
                  type: 'playlist',
                  displayName: profileRes.displayName,
                };
                return JSON.parse(JSON.stringify(trackList));
              })
            )
          );

          return forkJoin(requests).pipe(
            map((trackLists: TrackList[]) => ({
              ...response,
              data: trackLists,
            }))
          );
        })
      );
  }
  getPlaylistsByIds(ids: string[]): Observable<ApiResponse<TrackList[]>> {
    // Truyền tham số playlist_ids qua URL
    return this.http
      .get<ApiResponse<any[]>>(`${this.baseUrl}/bulk`, {
        params: { playlist_ids: ids.join(',') }, // nối danh sách ids thành một chuỗi, ví dụ "1,2,3"
      })
      .pipe(
        switchMap((response) => {
          const playlistItems = response.data;

          // Tạo một request cho mỗi playlist item để lấy thêm thông tin user profile
          const requests = playlistItems.map((item) =>
            this.getProfileForTrackList(item.userId).pipe(
              map((profileRes) => {
                const trackList: TrackList = {
                  ...this.mapToTrackList(item),
                  displayName: profileRes.data.displayName,
                };
                return trackList;
              })
            )
          );

          // Sử dụng forkJoin để đợi tất cả các request hoàn thành và trả về kết quả
          return forkJoin(requests).pipe(
            map((trackLists: TrackList[]) => ({
              ...response,
              data: trackLists,
            }))
          );
        })
      );
  }

  getPlaylistById(id: string): Observable<ApiResponse<TrackList>> {
    return this.http
      .get<ApiResponse<PlaylistResponse>>(`${this.baseUrl}/${id}`)
      .pipe(
        switchMap((response) => {
          const item = response.data;
          return this.getProfileForTrackList(item.userId).pipe(
            map((profileRes) => {
              const trackList: TrackList = {
                ...this.mapToTrackList(item),
                displayName: profileRes.data.displayName,
              };

              return {
                ...response,
                data: trackList,
              };
            })
          );
        })
      );
  }

  addPlaylist(
    request: AddPlaylistRequest
  ): Observable<ApiResponse<PlaylistResponse>> {
    return this.http.post<ApiResponse<PlaylistResponse>>(
      `${this.baseUrl}`,
      request
    );
  }

  updatePlaylistInfo(
    image: File | null,
    playlistInfo: UpdatePlaylistInfoRequest
  ): Observable<ApiResponse<TrackList>> {
    const formData = new FormData();
    if (image) {
      formData.append('image', image);
    }
    formData.append(
      'playlist',
      new Blob([JSON.stringify(playlistInfo)], { type: 'application/json' })
    );

    return this.http
      .put<ApiResponse<PlaylistResponse>>(`${this.baseUrl}/update`, formData)
      .pipe(
        switchMap((response) => {
          const item = response.data;
          return this.getProfileForTrackList(item.userId).pipe(
            map((profileRes) => {
              const trackList: TrackList = {
                ...this.mapToTrackList(item),
                displayName: profileRes.data.displayName,
              };
              return {
                ...response,
                data: trackList,
              };
            })
          );
        })
      );
  }

  deletePlaylistById(id: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(
      `${this.baseUrl}/delete/${id}`
    );
  }

  addTrackToPlaylist(
    request: AddPlaylistTrackRequest
  ): Observable<ApiResponse<PlaylistTrackResponse>> {
    return this.http.post<ApiResponse<PlaylistTrackResponse>>(
      `${this.trackUrl}/add`,
      request
    );
  }

  deleteTrackFromPlaylist(
    request: AddPlaylistTrackRequest
  ): Observable<ApiResponse<string>> {
    return this.http.request<ApiResponse<string>>(
      'delete',
      `${this.trackUrl}/delete`,
      {
        body: request,
      }
    );
  }
  getAllYourPlaylists(): Observable<ApiResponse<TrackList[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.baseUrl}/you/all`).pipe(
      switchMap((response) => {
        console.log(response);
        const playlistItems = response.data;
        const requests = playlistItems.map((item) =>
          this.getProfileForTrackList(item.userId).pipe(
            map((profileRes) => {
              const trackList: TrackList = {
                ...this.mapToTrackList(item),
                displayName: profileRes.data.displayName,
              };
              return trackList;
            })
          )
        );

        return forkJoin(requests).pipe(
          map((trackLists: TrackList[]) => ({
            ...response,
            data: trackLists,
          }))
        );
      })
    );
  }

  // Lấy playlist đã like
  getLikedPlaylists(): Observable<ApiResponse<TrackList[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.baseUrl}/you/liked`).pipe(
      switchMap((response) => {
        const playlistItems = response.data;
        const requests = playlistItems.map((item) =>
          this.getProfileForTrackList(item.userId).pipe(
            map((profileRes) => {
              const trackList: TrackList = {
                ...this.mapToTrackList(item),
                displayName: profileRes.data.displayName,
              };
              return trackList;
            })
          )
        );

        return forkJoin(requests).pipe(
          map((trackLists: TrackList[]) => ({
            ...response,
            data: trackLists,
          }))
        );
      })
    );
  }

  // Lấy playlist do user tạo
  getCreatedPlaylists(): Observable<ApiResponse<TrackList[]>> {
    return this.http
      .get<ApiResponse<any[]>>(`${this.baseUrl}/you/created`)
      .pipe(
        switchMap((response) => {
          const playlistItems = response.data;
          const requests = playlistItems.map((item) =>
            this.getProfileForTrackList(item.userId).pipe(
              map((profileRes) => {
                const trackList: TrackList = {
                  ...this.mapToTrackList(item),
                  displayName: profileRes.data.displayName,
                };
                return trackList;
              })
            )
          );

          return forkJoin(requests).pipe(
            map((trackLists: TrackList[]) => ({
              ...response,
              data: trackLists,
            }))
          );
        })
      );
  }
  private mapToTrackList(playlist: any): TrackList {
    console.log(playlist);
    const tracks = (playlist.playlistTracks ?? []).map((track: any) => ({
      ...track,
      belongToTrackListId: playlist.id,
    }));
    return {
      listname: playlist.title,
      listId: playlist.id,
      releaseDate: playlist.releaseDate,
      description: playlist.description,
      privacy: playlist.privacy,
      userId: playlist.userId,
      genre: playlist.genre,
      displayName: '', // gán sau khi gọi getProfileForTrackList
      tracks: tracks,
      likeCount: 0,
      imagePath: playlist.imagePath,
      isLiked: playlist.isLiked,
      type: 'playlist',
      tags: playlist.playlistTags,
    };
  }
}
