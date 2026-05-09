export interface AddPlaylistTrackRequest {
  trackId: string;
  playlistId: string;
}

export interface AddPlaylistRequest {
  title: string;
  privacy: string;
  trackIds: string[];
}

export interface UpdatePlaylistInfoRequest {
  id: string;
  title: string;
  privacy: string;
  trackIds: string[];
  releaseDate: string;
  description: string;
  genreId?: string;
  tagIds?: string[];
}
