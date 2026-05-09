package com.MusicPlatForm.user_library_service.dto.response.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackResponse {
    String id;
    String title;
    String description;
    String fileName;
    String coverImageName;
    LocalDateTime createdAt;
    String userId;

    String duration;
    String privacy;
    int countPlay;

    GenreResponse genre;

    List<TagResponse> tags;

    Boolean isLiked=false;

    ProfileWithCountFollowResponse user;
}
