package com.MusicPlatForm.comment_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LikedCommentRequest {
     String commentId;
     String userId;
     LocalDateTime likeAt;
}
