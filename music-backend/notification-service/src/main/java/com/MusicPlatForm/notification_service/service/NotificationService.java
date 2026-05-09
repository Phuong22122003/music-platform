package com.MusicPlatForm.notification_service.service;

import com.MusicPlatForm.notification_service.dto.request.EmailResetPasswordRequest;
import com.MusicPlatForm.notification_service.dto.request.NotificationRequest;
import com.MusicPlatForm.notification_service.dto.request.PasswordRequest;
import com.MusicPlatForm.notification_service.dto.response.NotificationResponse;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public interface NotificationService {
    public void sendNotificationWebSocket(NotificationRequest request);
    public void emailNotification(EmailResetPasswordRequest request) ;
    public void emailNewPassword(PasswordRequest request) ;
    public List<NotificationResponse> getNotificationByIds(List<String> ids);
    public List<String> getAllByUserId();
    public void markAsRead(List<String>ids);
    public void saveToke(String token);
  
}
