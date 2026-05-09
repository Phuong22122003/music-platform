package com.MusicPlatForm.notification_service.service.implement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.MusicPlatForm.notification_service.dto.response.NotificationResponse;
import com.MusicPlatForm.notification_service.service.FireBaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FireBaseServiceImplement implements FireBaseService {
    public void sendNotificationToFirebase(String token, String title,String body){
         try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Gửi FCM thành công: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotifcationWithDataToFirebase(String token, NotificationResponse notificationResponse) {
       try {
            Map<String, String> data = new HashMap<>();
            data.put("title", "Music App");
            data.put("body", notificationResponse.getMessage());
            data.put("type", notificationResponse.getType());
            data.put("trackId", notificationResponse.getTrackId());
            if(notificationResponse.getCommentId()!=null)
                data.put("commentId", notificationResponse.getCommentId());
            data.put("message", notificationResponse.getMessage());
            data.put("senderId", notificationResponse.getSenderId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String formatted = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS).format(formatter);
            data.put("createdAt", formatted.toString());

            Message message = Message.builder()
                    .putAllData(data) // chỉ gửi data
                    .setToken(token)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Gửi FCM thành công: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
