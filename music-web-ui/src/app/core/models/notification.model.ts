import { UserProfile } from './user_profile';

export interface Notification {
  id: string;
  message: string;
  type: string;
  createdAt: string;
  read: boolean;
  trackId?: string;
  commentId?: string;
  sender: UserProfile;
  recipient: UserProfile;
}
export interface NotificationResponse {
  id: string;
  senderId: string;
  recipientId: string;
  message: string;
  trackId: string;
  commentId: string;
  read: boolean;
  createdAt: Date;
  type: string;
}
