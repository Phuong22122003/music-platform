package com.MusicPlatForm.user_library_service.dto.request.playlist;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePlaylistInfoRequest  extends AddPlaylistRequest{
    @NotBlank
    @NotNull
    String id;
    
    @NotNull
    LocalDateTime releaseDate;
    String description;
    String genreId;
    List<String> tagIds;
}
