package com.MusicPlatForm.user_library_service.service.iface;

import java.util.List;

import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.entity.LikedTrack;

@Service
public interface LikeTrackServiceInterface {
    public List<String> getUserIdsLikedTrack(String trackId);

    public List<LikedTrack> getLikedTrack();

    public List<TrackResponse> getAllLikedTracks();

    public int getTrackLikeCount(String trackId);

    public Boolean isLiked(String trackId);

    public Boolean likeTrack(String trackId) ;

    //unlike theo id
    public Boolean unLikeTrack(String track_id);

    public List<String> filterLikedIds(List<String> ids);
}
