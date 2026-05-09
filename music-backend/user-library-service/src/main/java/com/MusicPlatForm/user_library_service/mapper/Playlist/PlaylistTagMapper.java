package com.MusicPlatForm.user_library_service.mapper.Playlist;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import com.MusicPlatForm.user_library_service.entity.PlaylistTag;
@Mapper(componentModel = "spring")
public interface PlaylistTagMapper {
    default List<PlaylistTag> mapTagIdsToPlaylistTags(List<String> tagIds) {
        return tagIds.stream().map(PlaylistTag::new).collect(Collectors.toList());
    }
}
