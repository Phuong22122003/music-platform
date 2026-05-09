package com.MusicPlatForm.user_library_service.dto.request.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddPlaylistTrackRequest {
    @NotBlank
    @NotNull
    @Size(min = 1, message = "Track ID must be at least 1 character")
    String trackId;
    @NotBlank
    @NotNull
    String playlistId;
}
