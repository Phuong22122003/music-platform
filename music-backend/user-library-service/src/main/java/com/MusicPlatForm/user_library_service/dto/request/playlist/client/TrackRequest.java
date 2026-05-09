package com.MusicPlatForm.user_library_service.dto.request.playlist.client;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackRequest{
    private String id;
    private String title;
    private String description;
    private String userId;
    private String privacy;
    private int countPlay;
    private String genreId;
    private List<String> tagIds;
}
