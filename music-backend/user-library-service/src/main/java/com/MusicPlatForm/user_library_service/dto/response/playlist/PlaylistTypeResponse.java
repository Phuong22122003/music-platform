package com.MusicPlatForm.user_library_service.dto.response.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaylistTypeResponse {
    PlaylistResponse playlistResponse;
    //Liked, Create
    String type;
}
