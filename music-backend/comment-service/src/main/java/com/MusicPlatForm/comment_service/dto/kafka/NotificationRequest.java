package com.MusicPlatForm.comment_service.dto.kafka;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String senderId;
    private String recipientId;
    private String message;
    private String trackId;
    private String commentId;
}
