package com.MusicPlatForm.user_library_service.dto.request.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddTrackAlbumRequest {
    @NotBlank
    @NotNull
    @Size(min = 1, message = "Track ID must be at least 1 character")
    String trackId;
    @NotBlank
    @NotNull
    String albumId;
}
