import { Track } from './track';

export interface PlayList {
  id: string;
  title?: string;
  description?: string;
  track_link?: string;
  privacy?: string;
  user_id?: string;
  release_date?: string;
}

export interface RelatedTrack {
  playlistId: string;
  trackName: string;
  authors: string;
  trackPath: string;
  userId: string;
}
