import { Track } from '../track';
import { PlaylistTrackResponse } from './playlist_track';

export interface PlaylistTypeResponse {
  type: string;
  playlistResponse: PlaylistResponse;
}

export interface PlaylistResponse {
  id: string;
  title: string;
  releaseDate: string;
  description: string;
  privacy: string;
  userId: string;
  genreId: string;
  imagePath: string;
  createdAt: string;
  playlistTracks: Track[];
  playlistTags: string[];
}
