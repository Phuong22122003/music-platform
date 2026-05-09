package com.MusicPlatForm.user_library_service.service.iface;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.response.client.ProfileWithCountFollowResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;

@Service
public interface RecommendedServiceInterface {
    public List<TrackResponse> getMixedForUser();
    public List<TrackResponse> getRelatedTrack(String trackId);
    public Map<String,List<TrackResponse>> getGroupedTrackByGenres();
    public List<TrackResponse> getMoreOfWhatYouLike();
    public List<ProfileWithCountFollowResponse> getArtirstsShouldKnow();
    public List<TrackResponse> getTrendingTracksByGenre(String genreId);
}
