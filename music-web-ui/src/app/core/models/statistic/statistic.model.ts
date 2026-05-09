import { Track } from '../track';

export interface UserStatistic {
  userId: string;
  count: number;
}

export interface DailyPlay {
  date: string; // LocalDate => string (ISO or dd/MM/yyyy format)
  playCount: number;
  detailPlay: { [key: string]: number };
}

export interface PlayResponse {
  dailyPlays: DailyPlay[];
  topListenerIds: UserStatistic[];
}

export interface DailyLike {
  date: string;
  likedCount: number;
  detailLiked: { [key: string]: number };
}

export interface LikeResponse {
  dailyLikes: DailyLike[];
  whoLiked: UserStatistic[];
}

export interface DailyComment {
  date: string;
  commentCount: number;
  detailComment: { [key: string]: number };
}

export interface CommentStatisticResponse {
  dailyComments: DailyComment[];
  whoCommented: UserStatistic[];
}

export interface TopTrack {
  track: Track;
  playCount: number;
}
