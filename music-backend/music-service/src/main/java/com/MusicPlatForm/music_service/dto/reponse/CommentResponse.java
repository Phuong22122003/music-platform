package com.MusicPlatForm.music_service.dto.reponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String trackId;
    String userId;
    String content;
    LocalDateTime commentAt;
    int likeCount;
}
