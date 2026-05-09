package com.MusicPlatForm.notification_service.service;

import org.springframework.stereotype.Service;

import com.MusicPlatForm.notification_service.dto.response.NotificationResponse;

@Service
public interface FireBaseService {
     public void sendNotificationToFirebase(String token, String title,String body);
     public void sendNotifcationWithDataToFirebase(String token, NotificationResponse  data);
}
