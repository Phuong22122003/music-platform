export interface TrackRequest {
  title: string;
  description: string;
  userId: string;
  privacy: string;
  countPlay: number;
  genreId?: string;
  tagIds?: string[];
}
