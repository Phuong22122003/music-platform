export interface TrackCreation {
  title: string;
  description?: string;
  userId: string;
  privacy: string;
  genreId: string;
  tagIds: string[];
  mainArtists: string;
}
