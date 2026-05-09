package com.MusicPlatForm.user_library_service.dto.request.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumKafkaRequest {
    private String title;
    private String description;
    private String albumId;
}
