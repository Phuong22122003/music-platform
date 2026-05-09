package com.MusicPlatForm.user_library_service.dto.response.playlist;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackResponse {
    private String id;
    private String trackId;
    private LocalDateTime addedAt;
}
