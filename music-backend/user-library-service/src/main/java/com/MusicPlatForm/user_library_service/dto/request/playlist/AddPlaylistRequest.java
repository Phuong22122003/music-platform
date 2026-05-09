package com.MusicPlatForm.user_library_service.dto.request.playlist;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddPlaylistRequest {
    @NotBlank
    String title;
    @NotBlank
    String privacy;
    List<String> trackIds;
}
