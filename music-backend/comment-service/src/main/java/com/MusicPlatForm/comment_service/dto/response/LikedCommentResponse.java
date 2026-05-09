package com.MusicPlatForm.comment_service.dto.response;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikedCommentResponse {
    String id;
    String commentId;
    String userId;
    LocalDateTime likeAt;
}
