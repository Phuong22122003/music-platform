package com.MusicPlatForm.admin_service.dto.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackResponse {
    String id;
    String title;
    String description;
    String fileName;
    String coverImageName;
    LocalDateTime createdAt;
    String userId;
    String duration;
    String privacy;
    int countPlay;

}
