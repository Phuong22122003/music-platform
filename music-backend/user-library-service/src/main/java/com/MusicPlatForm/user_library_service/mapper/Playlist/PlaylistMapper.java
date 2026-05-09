package com.MusicPlatForm.user_library_service.mapper.Playlist;


import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddPlaylistRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.UpdatePlaylistInfoRequest;
import com.MusicPlatForm.user_library_service.dto.response.playlist.PlaylistResponse;
import com.MusicPlatForm.user_library_service.entity.Playlist;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    

    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "likedPlaylists", ignore = true)
    @Mapping(target = "playlistTags", ignore = true)
    @Mapping(target = "playlistTracks", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "genreId", ignore = true)
    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "createdAt",expression = "java(java.time.LocalDateTime.now())")
    Playlist toPlaylist(AddPlaylistRequest addPlaylistRequest);

    /**
    *<pre>{@code
    playlist.setDescription(addPlaylistRequest.getDescription());
    playlist.setGenreId(addPlaylistRequest.getGenreId());
    playlist.setId(addPlaylistRequest.getId());
    playlist.setPrivacy(addPlaylistRequest.getPrivacy());
    playlist.setReleaseDate(addPlaylistRequest.getReleaseDate());
    playlist.setTitle(addPlaylistRequest.getTitle());
    * }</pre>
     * @param addPlaylistRequest
     * @param playlist
     */
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "likedPlaylists", ignore = true)
    @Mapping(target = "playlistTags", ignore = true)
    @Mapping(target = "playlistTracks", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updatePlaylistFromRequest(UpdatePlaylistInfoRequest addPlaylistRequest,@MappingTarget Playlist playlist);
    
    


    //==============================//
    List<PlaylistResponse> toPlaylistResponses(List<Playlist> playlist);

    @Mapping(target = "id",source = "id")
    @Mapping(target = "title",source = "title")
    @Mapping(target = "releaseDate",source = "releaseDate")
    @Mapping(target = "description",source = "description")
    @Mapping(target = "privacy",source = "privacy")
    @Mapping(target = "userId",source = "userId")
    @Mapping(target = "imagePath",source = "imagePath")
    @Mapping(target = "createdAt",source = "createdAt")
    @Mapping(target = "playlistTags",ignore = true)
    @Mapping(target = "playlistTracks",ignore = true)
    @Mapping(target = "genre",ignore = true)
    PlaylistResponse toPlaylistResponse(Playlist playlist);
    
   
}
