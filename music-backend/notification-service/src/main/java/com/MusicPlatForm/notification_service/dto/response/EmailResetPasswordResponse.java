package com.MusicPlatForm.notification_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailResetPasswordResponse {
    String userEmail;
    String status; // "SUCCESS" hoặc "FAILED"
    String message; // Thông báo thêm nếu cần
}
