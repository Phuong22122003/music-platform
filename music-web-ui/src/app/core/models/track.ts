import { GenreResponse } from './genre/genre_response.model';
import { TagResponse } from './tag/tag_response.model';

export interface Track {
  id: string;
  name: string;
  title: string;
  description?: string;
  fileName: string;
  coverImagePath: string;
  coverImageName?: string;
  userId: string;
  duration: string;
  createdAt: string;
  username: string;
  countPlay?: number;
  countLike?: number;
  countComment?: number;
  tags?: TagResponse[];
  genre?: GenreResponse;
  displayName?: string;
  isLiked?: boolean;
  privacy?: string;
  belongToTrackListId?: string;
}
