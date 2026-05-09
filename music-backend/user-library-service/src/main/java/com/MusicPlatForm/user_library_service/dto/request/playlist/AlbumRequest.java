package com.MusicPlatForm.user_library_service.dto.request.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AlbumRequest {
    @NotBlank(message = "ALBUM_TITLE_REQUIRED")
    String albumTitle;

    @NotBlank(message = "Main artists are required")
    String mainArtists;

    String genreId;

    @NotBlank(message = "ALBUM_TYPE_REQUIRED")
    String albumType;

    List<String> tagsId;

    String description;

    @Pattern(regexp = "private|public", message = "ALBUM_PRIVACY_INVALID")
    @NotBlank(message = "ALBUM_PRIVACY_REQUIRED")
    String privacy;

    @NotBlank(message = "ALBUM_LINK_REQUIRED")
    String albumLink;
}
