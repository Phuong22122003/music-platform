import { PlaylistResponse } from './playlist.model';

export interface LikedPlaylistResponse {
  id: string;
  userId: string;
  likedAt: string;
  playlist: PlaylistResponse;
}
