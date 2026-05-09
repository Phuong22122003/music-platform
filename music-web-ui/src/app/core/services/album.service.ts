import { Injectable } from '@angular/core';
import { catchError, forkJoin, map, Observable, switchMap } from 'rxjs';
import { ApiResponse } from '../models/api_response';
import { HttpClient } from '@angular/common/http';
import { ErrorHandlerService } from './error-handler-service';
import { environment } from '../../../environments/environment';
import { TrackList } from '../models/tracklist';
import { ProfileService } from './profile.service';
import {
  AddTrackAlbumRequest,
  AlbumRequest,
} from '../models/album/album_request';
import { TagResponse } from '../models/tag/tag_response.model';
@Injectable({
  providedIn: 'root', // Đăng ký service ở root để dùng toàn app
})
export class AlbumService {
  private baseUrl = environment.apiBaseUrl + '/user-library';
  private authApiUrl = `${this.baseUrl}/auth/album`;
  private publicApiUrl = `${this.baseUrl}/album`;

  constructor(
    private http: HttpClient,
    private errorHandlerService: ErrorHandlerService,
    private profileService: ProfileService
  ) {}

  uploadAlbum(albumData: FormData): Observable<ApiResponse<TrackList>> {
    return this.http
      .post<ApiResponse<any>>(`${this.authApiUrl}/add-album`, albumData)
      .pipe(
        switchMap((res) =>
          this.getTrackListWithDisplayName(res.data).pipe(
            map((trackList) => ({ ...res, data: trackList }))
          )
        )
      );
  }

  getAlbumById(albumId: string): Observable<ApiResponse<TrackList>> {
    return this.http
      .get<ApiResponse<any>>(`${this.publicApiUrl}/find-by-album-id/${albumId}`)
      .pipe(
        switchMap((res) =>
          this.getTrackListWithDisplayName(res.data).pipe(
            map((trackList) => ({ ...res, data: trackList }))
          )
        )
      );
  }

  getAlbumByLink(albumLink: string): Observable<ApiResponse<TrackList>> {
    return this.http
      .get<ApiResponse<any>>(`${this.publicApiUrl}/${albumLink}`)
      .pipe(
        switchMap((res) =>
          this.getTrackListWithDisplayName(res.data).pipe(
            map((trackList) => ({ ...res, data: trackList }))
          )
        )
      );
  }

  getAlbumsByUserId(userId: string): Observable<ApiResponse<TrackList[]>> {
    return this.getAlbums(`${this.publicApiUrl}/find-by-user-id/${userId}`);
  }
  getAlbumsIds(ids: string[]): Observable<ApiResponse<TrackList[]>> {
    const queryString = ids.map((id) => `album_ids=${id}`).join(',');
    const url = `${this.publicApiUrl}/bulk?${queryString}`;
    return this.getAlbums(url);
  }

  getLikedAlbums(userId: string): Observable<ApiResponse<TrackList[]>> {
    return this.getAlbums(`${this.publicApiUrl}/liked-albums/${userId}`);
  }

  getCreatedAndLikedAlbums(
    userId: string
  ): Observable<ApiResponse<TrackList[]>> {
    return this.getAlbums(
      `${this.publicApiUrl}/get-liked-created-album/${userId}`
    );
  }

  deleteAlbum(albumId: string): Observable<ApiResponse<string>> {
    return this.http
      .delete<ApiResponse<string>>(`${this.authApiUrl}/${albumId}`)
      .pipe(catchError(this.errorHandlerService.handleError));
  }

  updateAlbum(
    albumId: string,
    albumRequest: AlbumRequest,
    coverAlbum?: File
  ): Observable<ApiResponse<TrackList>> {
    const formData = new FormData();
    formData.append(
      'meta-data',
      new Blob([JSON.stringify(albumRequest)], { type: 'application/json' })
    );
    if (coverAlbum) {
      formData.append('cover-album', coverAlbum);
    }

    return this.http
      .put<ApiResponse<any>>(`${this.authApiUrl}/${albumId}`, formData)
      .pipe(
        switchMap((res) =>
          this.getTrackListWithDisplayName(res.data).pipe(
            map((trackList) => ({ ...res, data: trackList }))
          )
        ),
        catchError(this.errorHandlerService.handleError)
      );
  }

  likeAlbum(albumId: string): Observable<ApiResponse<boolean>> {
    return this.http
      .post<ApiResponse<void>>(`${this.authApiUrl}/like/${albumId}`, null)
      .pipe(catchError(this.errorHandlerService.handleError));
  }

  unlikeAlbum(albumId: string): Observable<ApiResponse<boolean>> {
    return this.http
      .delete<ApiResponse<void>>(`${this.authApiUrl}/unlike/${albumId}`)
      .pipe(catchError(this.errorHandlerService.handleError));
  }

  addTrackToAlbum(request: AddTrackAlbumRequest): Observable<ApiResponse<any>> {
    return this.http
      .post<ApiResponse<any>>(`${this.authApiUrl}/add-track`, request)
      .pipe(catchError(this.errorHandlerService.handleError));
  }

  countLike(albumId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.publicApiUrl}/count/${albumId}`
    );
  }
  private getAlbums(url: string): Observable<ApiResponse<TrackList[]>> {
    return this.http.get<ApiResponse<any[]>>(url).pipe(
      switchMap((response) => {
        const requests = response.data.map((item) =>
          this.getTrackListWithDisplayName(item)
        );
        return forkJoin(requests).pipe(
          map((trackLists: TrackList[]) => ({ ...response, data: trackLists }))
        );
      })
    );
  }

  private getTrackListWithDisplayName(album: any): Observable<TrackList> {
    return this.profileService.getProfileById(album.userId).pipe(
      map((profileRes) => ({
        ...this.mapAlbumToTrackList(album),
        displayName: profileRes.data.displayName,
      }))
    );
  }

  private mapAlbumToTrackList(album: any): TrackList {
    const tracks = (album.tracks ?? []).map((track: any) => ({
      ...track,
      belongToTrackListId: album.id,
    }));

    return {
      listname: album.albumTitle,
      listId: album.id,
      releaseDate: album.createdAt,
      description: album.description,
      privacy: album.privacy,
      userId: album.userId,
      genre: album.genre,
      displayName: '',
      tracks, // Gán tracks đã xử lý
      likeCount: album.likeCount ?? 0,
      imagePath: album.imagePath,
      isLiked: album.isLiked ?? false,
      tags: album.tags,
      type: 'album',
      albumType: album.albumType,
      link: album.albumLink,
    };
  }
}
