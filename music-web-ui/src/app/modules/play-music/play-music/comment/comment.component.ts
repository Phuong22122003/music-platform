import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { CommentService } from '../../../../core/services/comment.service';
import { AuthService } from '../../../../core/services/auth-service';
import { CommentResponse } from '../../../../core/models/comment/comment-response.model';
import { ApiResponse } from '../../../../core/models/api_response';
import { CommentRequest } from '../../../../core/models/comment/comment-request.model';
import { Subscription, takeUntil } from 'rxjs';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  standalone: false,
})
export class CommentComponent implements OnInit, OnDestroy {
  @Input() trackId!: string;
  subscription!: Subscription;
  selectedSortType: string = 'newest';

  commentSortTypes = [
    { id: 'newest', name: 'Newest' },
    { id: 'oldest', name: 'Oldest' },
    { id: 'track-time', name: 'Track Time' },
  ];

  commentContent: string = '';
  comments: CommentResponse[] = [];

  constructor(
    private commentService: CommentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.fetchComments();
    this.subscription = this.commentService.commentAdded$.subscribe(() => {
      this.fetchComments();
    });
  }

  fetchComments(): void {
    if (!this.trackId) return;

    this.commentService.getComments(this.trackId).subscribe({
      next: (res: ApiResponse<CommentResponse[]>) => {
        this.comments = this.sortComments(res.data);
      },
      error: (err) => console.error(err),
    });
  }

  sortComments(data: CommentResponse[]): CommentResponse[] {
    switch (this.selectedSortType) {
      case 'oldest':
        return [...data].sort(
          (a, b) =>
            new Date(a.commentAt).getTime() - new Date(b.commentAt).getTime()
        );
      case 'track-time':
        // Giả sử bạn có `trackTime` trong comment, nếu không thì bỏ đi
        return [...data].sort(
          (a, b) => (a as any).trackTime - (b as any).trackTime
        );
      case 'newest':
      default:
        return [...data].sort(
          (a, b) =>
            new Date(b.commentAt).getTime() - new Date(a.commentAt).getTime()
        );
    }
  }

  onSortChange(): void {
    this.comments = this.sortComments(this.comments);
  }

  addComment(): void {
    if (!this.commentContent.trim()) return;

    const commentReq: CommentRequest = {
      content: this.commentContent,
      trackId: this.trackId,
      userId: this.authService.getUserId() as string,
      likeCount: 0,
    };

    this.commentService.addComment(commentReq).subscribe({
      next: (res) => {
        this.commentContent = '';
        this.fetchComments(); // hoặc unshift vào mảng nếu muốn hiệu năng tốt hơn
      },
      error: (err) => console.error(err),
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
