package com.MusicPlatForm.user_library_service.mapper.Playlist;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddPlaylistTrackRequest;
import com.MusicPlatForm.user_library_service.dto.response.playlist.PlaylistTrackResponse;
import com.MusicPlatForm.user_library_service.entity.PlaylistTrack;

@Mapper(componentModel = "spring")
public interface PlaylistTrackMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    @Mapping(target = "playlist", ignore = true)
    PlaylistTrack toPlaylistTrack(AddPlaylistTrackRequest addPlaylistTrackRequest);

    
    List<PlaylistTrack> toPlaylistTracks(List<AddPlaylistTrackRequest> addPlaylistTrackRequests);
    
    List<PlaylistTrackResponse> toPlaylistTracksResponse(List<PlaylistTrack> playlistTracks);
    PlaylistTrackResponse toPlaylistTrackResponse(PlaylistTrack playlistTrack);
    
    List<PlaylistTrack> toPlaylistTracksFromTrackIds(List<String> trackIds);

    default PlaylistTrack fromTrackId(String trackId) {
        PlaylistTrack track = new PlaylistTrack();
        track.setTrackId(trackId);
        return track;
    }
}
