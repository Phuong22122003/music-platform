export interface AddTrackAlbumRequest {
  trackId: string;
  albumId: string;
}
export interface AlbumRequest {
  albumTitle: string;
  mainArtists: string;
  genreId?: string;
  albumType: string;
  tagsId?: string[];
  description?: string;
  privacy: string;
  albumLink: string;
  trackIds: string[];
}
