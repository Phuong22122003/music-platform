package com.MusicPlatForm.user_library_service.mapper.Playlist;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import com.MusicPlatForm.user_library_service.dto.response.playlist.LikedPlaylistResponse;
import com.MusicPlatForm.user_library_service.entity.LikedPlaylist;
import com.MusicPlatForm.user_library_service.entity.PlaylistTag;

@Mapper(componentModel = "spring")
public interface LikedPlaylistMapper {
    public LikedPlaylistResponse toLikedPlaylistResponse(LikedPlaylist likedPlaylist);
    public List<LikedPlaylistResponse> toLikedPlaylistResponses(List<LikedPlaylist> likedPlaylist);
     // for toPlaylistResponse
    default List<String> mapTagsToString(List<PlaylistTag> playlistTags) {
        if (playlistTags == null) {
            return null;
        }
        return playlistTags.stream()
                .map(PlaylistTag::getTagId) // Hoặc getId() nếu muốn lấy ID
                .collect(Collectors.toList());
    }
}
