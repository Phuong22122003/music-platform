import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map, Observable, Subject } from 'rxjs';
import { ApiResponse } from '../models/api_response';
@Injectable({
  providedIn: 'root', // Đăng ký service ở root để dùng toàn app
})
export class SearchService {
  private apiUrl = environment.apiBaseUrl + '/search-service';
  private searchChange = new Subject<string>();
  search$ = this.searchChange.asObservable();
  emitSearchChange(query: string) {
    if (!query) return;
    this.searchChange.next(query);
  }
  constructor(private http: HttpClient) {}
  searchTrackIds(query: string): Observable<string[]> {
    return this.http
      .get<ApiResponse<string[]>>(this.apiUrl + `/tracks?q=${query}`)
      .pipe(map((response) => response.data));
  }

  // Tìm kiếm Users
  searchUserIds(query: string): Observable<string[]> {
    return this.http
      .get<ApiResponse<string[]>>(`${this.apiUrl}/users`, {
        params: { q: query }, // Thêm tham số query vào yêu cầu
      })
      .pipe(map((response) => response.data));
  }

  // Tìm kiếm Playlists
  searchPlaylistIds(query: string): Observable<string[]> {
    return this.http
      .get<ApiResponse<string[]>>(`${this.apiUrl}/playlists`, {
        params: { q: query }, // Thêm tham số query vào yêu cầu
      })
      .pipe(map((response) => response.data));
  }

  // Tìm kiếm Albums
  searchAlbumIds(query: string): Observable<string[]> {
    return this.http
      .get<ApiResponse<string[]>>(`${this.apiUrl}/albums`, {
        params: { q: query }, // Thêm tham số query vào yêu cầu
      })
      .pipe(map((response) => response.data));
  }
}
