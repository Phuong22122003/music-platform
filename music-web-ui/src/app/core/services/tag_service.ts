import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TagResponse } from '../models/tag/tag_response.model';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api_response';
@Injectable({
  providedIn: 'root',
})
export class TagService {
  private baseAuthUrl = `${environment.apiBaseUrl}/music-service/auth/tags`;
  private baseUrl = `${environment.apiBaseUrl}/music-service/tags`;

  constructor(private http: HttpClient) {}

  getAllTags(): Observable<ApiResponse<TagResponse[]>> {
    return this.http.get<ApiResponse<TagResponse[]>>(`${this.baseUrl}`);
  }

  getTagsByIds(ids: string[]): Observable<ApiResponse<TagResponse[]>> {
    const params = ids.map((id) => `ids=${id}`).join('&');
    return this.http.get<ApiResponse<TagResponse[]>>(
      `${this.baseUrl}/?${params}`
    );
  }

  createTag(name: string): Observable<ApiResponse<TagResponse>> {
    return this.http.post<ApiResponse<TagResponse>>(`${this.baseAuthUrl}`, {
      name,
    });
  }

  deleteTag(tagId: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(
      `${this.baseAuthUrl}/${tagId}`
    );
  }
}
