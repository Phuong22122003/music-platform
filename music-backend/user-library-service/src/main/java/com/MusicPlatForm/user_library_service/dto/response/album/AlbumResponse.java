package com.MusicPlatForm.user_library_service.dto.response.album;

import com.MusicPlatForm.user_library_service.dto.response.client.GenreResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.ProfileWithCountFollowResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TagResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AlbumResponse {
    String albumTitle;
    String mainArtists;
    String genreId;
    String albumType;
    List<TagResponse> tags;
    String description;
    String privacy;
    String albumLink;
    String imagePath;
    String userId;
    String id;
    LocalDateTime createdAt;
    List<TrackResponse> tracks;
    GenreResponse genre;
    Boolean isLiked;
    ProfileWithCountFollowResponse user;
    public Boolean getIsLiked(){
        if(isLiked==null) return false;
        return isLiked;
    }
}
