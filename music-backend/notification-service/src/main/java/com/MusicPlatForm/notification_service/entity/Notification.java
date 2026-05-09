package com.MusicPlatForm.notification_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notifications")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @Id
    String id;
    String senderId;  // Người gửi thông báo (người follow, người comment, ...)
    String recipientId; // Người nhận thông báo
    String message;
    
    String trackId;  // ID bài nhạc liên quan (nếu có) -> có thể suy ra từ comment nhưng để vào giúp truy vấn nhanh
    String commentId; // ID comment liên quan (nếu có)

    boolean isRead;
    LocalDateTime createdAt;
}