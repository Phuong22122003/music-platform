// comment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { ErrorHandlerService } from './error-handler-service';
import { CommentResponse } from '../models/comment/comment-response.model';
import { ApiResponse } from '../models/api_response';
import { CommentRequest } from '../models/comment/comment-request.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private commentAddedSource = new Subject<void>();
  commentAdded$ = this.commentAddedSource.asObservable();
  private apiUrl = environment.apiBaseUrl + '/comment-service/comments';
  emitCommentAdded(): void {
    this.commentAddedSource.next(); // Gọi khi comment được thêm
  }
  constructor(
    private http: HttpClient,
    private errorHandlerService: ErrorHandlerService
  ) {}

  addComment(
    comment: CommentRequest
  ): Observable<ApiResponse<CommentResponse>> {
    return this.http.post<ApiResponse<CommentResponse>>(
      `${this.apiUrl}`,
      comment
    );
  }

  getComments(trackId: string): Observable<ApiResponse<CommentResponse[]>> {
    return this.http.get<ApiResponse<CommentResponse[]>>(
      `${this.apiUrl}/track/${trackId}`
    );
  }

  getCommentLikeCount(commentId: string): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(
      `${this.apiUrl}/likes/${commentId}`
    );
  }

  likeComment(
    commentId: string,
    userId: string
  ): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      `${this.apiUrl}/${commentId}/likes`,
      {}
    );
  }

  unlikeComment(
    commentId: string,
    userId: string
  ): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.apiUrl}/${commentId}/likes`
    );
  }

  updateComment(
    id: string,
    content: string
  ): Observable<ApiResponse<CommentResponse>> {
    return this.http.put<ApiResponse<CommentResponse>>(
      `${this.apiUrl}/update/${id}`,
      content,
      {
        headers: { 'Content-Type': 'text/plain' },
      }
    );
  }

  deleteComment(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/delete/${id}`);
  }

  replyToComment(
    commentId: string,
    request: CommentRequest
  ): Observable<ApiResponse<CommentResponse>> {
    return this.http.post<ApiResponse<CommentResponse>>(
      `${this.apiUrl}/${commentId}/replies`,
      request
    );
  }
}
