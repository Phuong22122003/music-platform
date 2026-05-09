package com.MusicPlatForm.notification_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private String id;
    private String senderId;
    private String recipientId;
    private String message;
    private String trackId;
    private String commentId;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String type;
}
