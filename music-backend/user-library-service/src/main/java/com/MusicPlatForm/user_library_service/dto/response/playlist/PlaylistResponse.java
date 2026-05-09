package com.MusicPlatForm.user_library_service.dto.response.playlist;

import java.time.LocalDateTime;
import java.util.List;


import com.MusicPlatForm.user_library_service.dto.response.client.GenreResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.ProfileWithCountFollowResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TagResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlaylistResponse {
    private String id;
    private String title;
    private LocalDateTime releaseDate;
    private String description;
    private String privacy;
    private String userId;
    private GenreResponse genre;
    private String imagePath;
    private LocalDateTime createdAt;
    private List<TrackResponse> playlistTracks;
    private List<TagResponse> playlistTags;
    private Boolean isLiked;
    ProfileWithCountFollowResponse user;
}
