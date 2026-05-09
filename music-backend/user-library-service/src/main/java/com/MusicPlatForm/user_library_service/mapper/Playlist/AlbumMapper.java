package com.MusicPlatForm.user_library_service.mapper.Playlist;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AlbumRequest;
import com.MusicPlatForm.user_library_service.dto.response.album.AlbumResponse;
import com.MusicPlatForm.user_library_service.entity.Album;
import com.MusicPlatForm.user_library_service.entity.AlbumTag;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", imports = {AlbumTag.class, Collectors.class})
public interface AlbumMapper {
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    Album toAlbum(AlbumRequest albumRequest);

    
    // @Mapping(target = "tagsId", expression = "java(album.getTags().stream().map(AlbumTag::getTagId).collect(Collectors.toList()))")
    @Mapping(target="tags",ignore = true)
    @Mapping(target="tracks",ignore = true)
    @Mapping(target="genre",ignore = true)
    AlbumResponse toAlbumResponse(Album album);

    void updateAlbumFromRequest(AlbumRequest albumRequest, @MappingTarget Album album);
}
