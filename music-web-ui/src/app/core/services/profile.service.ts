import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ApiResponse } from '../models/api_response';
import { Observable, of } from 'rxjs';
import { UserProfile } from '../models/user_profile';
import { environment } from '../../../environments/environment';
export interface ProfileUpdateRequest {
  firstName: string;
  lastName: string;
  dob: string; // ISO string, ví dụ: '2000-10-31'
  gender: boolean;
  displayName: string;
}

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private apiUrl = environment.apiBaseUrl + '/profile/users';
  private apiAuthUrl = environment.apiBaseUrl + '/profile/auth/users';
  constructor(private http: HttpClient) {}
  getProfileById(userId: string): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.apiUrl}/` + userId);
  }

  uploadAvatar(avatar: FormData): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(
      `${this.apiAuthUrl}/upload-avatar`,
      avatar
    );
  }

  uploadCover(cover: FormData): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(
      `${this.apiAuthUrl}/upload-cover`,
      cover
    );
  }

  updateProfile(
    profile: ProfileUpdateRequest
  ): Observable<ApiResponse<UserProfile>> {
    return this.http.put<ApiResponse<UserProfile>>(
      this.apiAuthUrl + '/update-my-info',
      profile
    );
  }

  getProfileByIds(ids: string[]): Observable<ApiResponse<UserProfile[]>> {
    if (ids.length === 0) {
      return of({
        code: 200,
        message: 'Success',
        data: [],
      });
    }
    let params = new HttpParams();
    ids.forEach((id) => {
      params = params.append('userIds', id);
    });

    return this.http.get<ApiResponse<UserProfile[]>>(`${this.apiUrl}/bulk`, {
      params,
    });
  }
}
