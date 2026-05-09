import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable, Subject } from 'rxjs';
import { ApiResponse } from '../models/api_response';
import { Page } from '../models/page';
import { UserProfile } from '../models/user_profile';
import { HttpClient, HttpParams } from '@angular/common/http';
@Injectable({
  providedIn: 'root',
})
export class FollowService {
  private baseUrl = `${environment.apiBaseUrl}/profile/follows`;
  private baseAuthUrl = `${environment.apiBaseUrl}/profile/auth/follows`;
  private followChangeSubject = new Subject<{
    userId: string;
    action: 'follow' | 'unfollow';
    initiatorId: string;
  }>();
  followChange$ = this.followChangeSubject.asObservable();
  constructor(private http: HttpClient) {}

  // 🔄 Follow user
  followUser(followData: {
    followingId: string;
    initiatorId?: string;
  }): Observable<ApiResponse<any>> {
    return new Observable((observer) => {
      this.http.post<ApiResponse<any>>(this.baseAuthUrl, followData).subscribe({
        next: (res) => {
          this.followChangeSubject.next({
            userId: followData.followingId,
            action: 'follow',
            initiatorId: followData.initiatorId || '',
          });
          observer.next(res);
          observer.complete();
        },
        error: (err) => observer.error(err),
      });
    });
  }

  // 🚫 Unfollow user
  unfollowUser(
    userId: string,
    initiatorId?: string
  ): Observable<ApiResponse<any>> {
    return new Observable((observer) => {
      this.http
        .delete<ApiResponse<any>>(`${this.baseAuthUrl}/unfollow/${userId}`)
        .subscribe({
          next: (res) => {
            this.followChangeSubject.next({
              userId,
              action: 'unfollow',
              initiatorId: initiatorId || '',
            });
            observer.next(res);
            observer.complete();
          },
          error: (err) => observer.error(err),
        });
    });
  }

  // 👥 Get followers
  getFollowers(
    userId: string,
    page = 0,
    size = 20
  ): Observable<ApiResponse<Page<UserProfile>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<Page<UserProfile>>>(
      `${this.baseUrl}/get-followers/${userId}`,
      { params }
    );
  }

  // 👉 Get followings
  getFollowings(
    userId: string,
    page = 0,
    size = 20
  ): Observable<ApiResponse<Page<UserProfile>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<Page<UserProfile>>>(
      `${this.baseUrl}/get-followings/${userId}`,
      { params }
    );
  }

  isFollowing(
    followerId: string,
    followingId: string
  ): Observable<ApiResponse<boolean>> {
    const params = new HttpParams()
      .set('followerId', followerId)
      .set('followingId', followingId);

    return this.http.get<ApiResponse<boolean>>(`${this.baseUrl}/is-following`, {
      params,
    });
  }
}
