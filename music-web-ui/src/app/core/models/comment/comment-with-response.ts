import { UserProfile } from '../user_profile';
import { CommentResponse } from './comment-response.model';

export interface CommentWithReply extends CommentResponse {
  userProfile?: UserProfile;
  replyUserProfile?: UserProfile;
  replies?: CommentWithReply[];
}
