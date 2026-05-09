// comment-response.model.ts
export interface CommentResponse {
  id: string;
  trackId: string;
  userId: string;
  content: string;
  commentAt: string;
  likeCount: number;
  isLiked?: boolean;
  replies?: CommentResponse[];
  repliedUserId?: string;
}
