import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiResponse } from '../models/api_response';
import { environment } from '../../../environments/environment';
import { GenreResponse } from '../models/genre/genre_response.model';
@Injectable({ providedIn: 'root' })
export class GenreService {
  private baseAuthUrl = `${environment.apiBaseUrl}/music-service/genres`;
  private baseUrl = `${environment.apiBaseUrl}/music-service/genres`;

  constructor(private http: HttpClient) {}

  getAllGenres() {
    return this.http.get<ApiResponse<GenreResponse[]>>(`${this.baseUrl}`);
  }

  createGenre(name: string) {
    return this.http.post<ApiResponse<GenreResponse>>(`${this.baseAuthUrl}`, {
      name,
    });
  }

  deleteGenre(id: string) {
    return this.http.delete<ApiResponse<string>>(`${this.baseAuthUrl}/${id}`);
  }
}
