package com.MusicPlatForm.user_library_service.dto.response.liketrack;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikedTrackResponse {
    String id;
    String trackId;
    String userId;
}