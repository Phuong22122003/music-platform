import { GenreResponse } from './genre/genre_response.model';
import { TagResponse } from './tag/tag_response.model';
import { Track } from './track';
import { TrackAndWave } from './track_wave';

export interface TrackList {
  listname: string;
  listId: string;
  releaseDate: string;
  description: string;
  privacy: string;
  userId: string;
  genre: GenreResponse;
  tags: TagResponse[];
  displayName?: string;
  tracks: Track[];
  likeCount: number;
  imagePath: string;
  isLiked: boolean;
  link?: string;
  type: 'playlist' | 'album';
  albumType?: string;
}
