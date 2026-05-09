import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ErrorHandlerService } from './error-handler-service';
import { CommentResponse } from '../models/comment/comment-response.model';
import { ApiResponse } from '../models/api_response';
import { catchError, Observable } from 'rxjs';
import {
  CommentStatisticResponse,
  LikeResponse,
  PlayResponse,
  TopTrack,
} from '../models/statistic/statistic.model';

@Injectable({
  providedIn: 'root',
})
export class TrackStatisticsService {
  private apiUrl = `${environment.apiBaseUrl}/music-service/track-statistics`;
  private adminStatisticUrl = `${environment.apiBaseUrl}/admin-service/statistic`;
  private userStatisticUrl = `${environment.apiBaseUrl}/user-library/statistic`;
  constructor(private http: HttpClient) {}

  getCommentsForTrack(
    trackId: string
  ): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(
      `${this.apiUrl}/comments/${trackId}`
    );
  }

  getCommentCountForTrack(trackId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.apiUrl}/comments/count/${trackId}`
    );
  }

  getPlayCount(trackId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.apiUrl}/cout_play/${trackId}`
    );
  }

  getTotalCommentLikes(trackId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.apiUrl}/comments/likes/${trackId}`
    );
  }

  getTrackLikeCount(trackId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.apiUrl}/liked/count/${trackId}`
    );
  }
  // Admin Statistic Url
  getPlayResponse(
    fromDate: string,
    toDate: string
  ): Observable<ApiResponse<PlayResponse>> {
    const params = new HttpParams()
      .set('from_date', fromDate)
      .set('to_date', toDate);
    return this.http.get<ApiResponse<PlayResponse>>(
      `${this.adminStatisticUrl}/plays`,
      {
        params,
      }
    );
  }

  getLikeResponse(
    fromDate: string,
    toDate: string
  ): Observable<ApiResponse<LikeResponse>> {
    const params = new HttpParams()
      .set('from_date', fromDate)
      .set('to_date', toDate);
    return this.http.get<ApiResponse<LikeResponse>>(
      `${this.adminStatisticUrl}/likes`,
      {
        params,
      }
    );
  }

  getCommentStatisticResponse(
    fromDate: string,
    toDate: string
  ): Observable<ApiResponse<CommentStatisticResponse>> {
    const params = new HttpParams()
      .set('from_date', fromDate)
      .set('to_date', toDate);
    return this.http.get<ApiResponse<CommentStatisticResponse>>(
      `${this.adminStatisticUrl}/comments`,
      { params }
    );
  }
  // User
  getCommentStatisticUserResponse(
    fromDate: string,
    toDate: string
  ): Observable<ApiResponse<CommentStatisticResponse>> {
    const params = new HttpParams()
      .set('from_date', fromDate)
      .set('to_date', toDate);
    return this.http.get<ApiResponse<CommentStatisticResponse>>(
      `${this.userStatisticUrl}/comments`,
      { params }
    );
  }
  getLikeStatisticUserResponse(
    fromDate: string,
    toDate: string
  ): Observable<ApiResponse<LikeResponse>> {
    const params = new HttpParams()
      .set('from_date', fromDate)
      .set('to_date', toDate);
    return this.http.get<ApiResponse<LikeResponse>>(
      `${this.userStatisticUrl}/likes`,
      { params }
    );
  }
  getPlayResponseUser(
    fromDate: string,
    toDate: string
  ): Observable<ApiResponse<PlayResponse>> {
    const params = new HttpParams()
      .set('from_date', fromDate)
      .set('to_date', toDate);
    return this.http.get<ApiResponse<PlayResponse>>(
      `${this.userStatisticUrl}/plays`,
      { params }
    );
  }

  // User Top Tracks
  getUserTopTracks(
    userId: string,
    fromDate: string | null,
    toDate: string | null
  ): Observable<ApiResponse<TopTrack[]>> {
    let params = new HttpParams();
    if (fromDate && toDate) {
      params = params.set('from_date', fromDate).set('to_date', toDate);
    }
    if (userId) {
      params = params.set('user_id', userId);
    }
    return this.http.get<ApiResponse<TopTrack[]>>(
      `${this.userStatisticUrl}/top-tracks`,
      { params }
    );
  }

  getAllTopTracks(
    fromDate: string | null,
    toDate: string | null
  ): Observable<ApiResponse<TopTrack[]>> {
    let params = new HttpParams();
    if (fromDate && toDate) {
      params = params.set('from_date', fromDate).set('to_date', toDate);
    }

    return this.http.get<ApiResponse<TopTrack[]>>(
      `${this.userStatisticUrl}/top-tracks/all`,
      { params }
    );
  }
}
