package com.MusicPlatForm.user_library_service.dto.response.playlist;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikedPlaylistResponse {
    String id;
    String userId;
    LocalDateTime likedAt;
    PlaylistResponse playlist;
}
