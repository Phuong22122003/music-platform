package com.MusicPlatForm.comment_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

import com.MusicPlatForm.comment_service.dto.client.UserProfileResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String trackId;
    String userId;
    String repliedUserId;
    Boolean isLiked;
    String content;
    LocalDateTime commentAt;
    int likeCount;
    List<CommentResponse> replies;
    UserProfileResponse user;

    public CommentResponse(String id, String trackId, String userId, String repliedUserId, Boolean isLiked,
            String content, LocalDateTime commentAt, int likeCount, List<CommentResponse> replies) {
        this.id = id;
        this.trackId = trackId;
        this.userId = userId;
        this.repliedUserId = repliedUserId;
        this.isLiked = isLiked;
        this.content = content;
        this.commentAt = commentAt;
        this.likeCount = likeCount;
        this.replies = replies;
    }

}
