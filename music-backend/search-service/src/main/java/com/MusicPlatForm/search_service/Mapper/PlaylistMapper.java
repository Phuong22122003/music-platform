package com.MusicPlatForm.search_service.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.MusicPlatForm.search_service.Dto.Request.PlaylistRequest;
import com.MusicPlatForm.search_service.Entity.Playlist;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    

    List<String> toPlaylistIds(List<Playlist> playlists);

    default String mapPlaylistToId(Playlist playlist) {
        return playlist.getPlaylistId();
    }
}
