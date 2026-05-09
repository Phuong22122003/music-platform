package com.MusicPlatForm.user_library_service.mapper.Playlist;

import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.dto.response.liketrack.LikedTrackResponse;
import com.MusicPlatForm.user_library_service.entity.LikedTrack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LikedTrackMapper {

    // Ánh xạ từ LikedTrack sang TrackResponse
    @Mapping(source = "trackId", target = "id")
    @Mapping(source = "userId", target = "userId")
    TrackResponse toTrackResponse(LikedTrack likedTrack);

    // Ánh xạ danh sách LikedTrack sang danh sách TrackResponse
    List<TrackResponse> toTrackResponses(List<LikedTrack> likedTracks);

    // ánh xạ ngược từ TrackResponse sang LikedTrack
    @Mapping(source = "id", target = "trackId")
    @Mapping(source = "userId", target = "userId")
    LikedTrack toLikedTrack(TrackResponse trackResponse);

    //ánh xạ từ LikedTrack sang LikedTrackResponse
    @Mapping(source = "id", target = "id")
    @Mapping(source = "trackId", target = "trackId")
    @Mapping(source = "userId", target = "userId")
    LikedTrackResponse toLikedTrackResponse(LikedTrack likedTrack);

    //ánh xạ danh sách từ LikedTrack sang LikedTrackResponse
    List<LikedTrackResponse> toLikedTrackResponses(List<LikedTrack> likedTracks);
}
