import { GenreResponse } from '../genre/genre_response.model';
import { TagResponse } from '../tag/tag_response.model';

export interface TrackResponse {
  id: string;
  title: string;
  description: string;
  fileName: string;
  coverImageName: string;
  createdAt: string; // ISO string
  userId: string;
  duration: string;
  privacy: string;
  countPlay: number;
  genre: GenreResponse;
  tags: TagResponse[];
}
