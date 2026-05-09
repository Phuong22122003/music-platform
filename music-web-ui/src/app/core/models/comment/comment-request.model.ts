// comment-request.model.ts
export interface CommentRequest {
  content: string;
  trackId: string;
  userId: string;
  likeCount: number;
}
