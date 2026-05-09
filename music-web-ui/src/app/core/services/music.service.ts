import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiResponse } from '../models/api_response';
import { Observable } from 'rxjs';
import { TrackResponse } from '../models/track/track-response.model';
import { TrackRequest } from '../models/track/track_request.model';
import { environment } from '../../../environments/environment';
@Injectable({
  providedIn: 'root',
})
export class MusicService {
  private baseUrl = `${environment.apiBaseUrl}/music-service`;
  private trackUrl = this.baseUrl + '/tracks';

  constructor(private http: HttpClient) {}

  uploadTrack(
    coverImage: File | null,
    trackAudio: File | null,
    trackRequest: TrackRequest
  ): Observable<ApiResponse<TrackResponse>> {
    const formData = new FormData();

    if (coverImage) {
      formData.append('cover_image', coverImage);
    }
    if (trackAudio) {
      formData.append('track_audio', trackAudio);
    }

    formData.append(
      'track',
      new Blob([JSON.stringify(trackRequest)], { type: 'application/json' })
    );

    return this.http.post<ApiResponse<TrackResponse>>(this.trackUrl, formData);
  }
}
