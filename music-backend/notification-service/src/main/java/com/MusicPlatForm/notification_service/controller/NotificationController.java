package com.MusicPlatForm.notification_service.controller;

import com.MusicPlatForm.notification_service.dto.ApiResponse;
import com.MusicPlatForm.notification_service.dto.request.EmailResetPasswordRequest;
import com.MusicPlatForm.notification_service.dto.request.NotificationRequest;
import com.MusicPlatForm.notification_service.dto.response.NotificationResponse;
import com.MusicPlatForm.notification_service.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class NotificationController {
    NotificationService notificationService;
    KafkaTemplate<String, NotificationRequest> notificationKafkaTemplate;
    KafkaTemplate<String, EmailResetPasswordRequest> emailKafkaTemplate;

    // Listener cho thông báo chung
    @KafkaListener(topics = "notification_to_user", groupId = "notification_group")
    public void listenerKafka(NotificationRequest notificationRequest) {
        System.out.println("Received notification: " + notificationRequest);
        notificationService.sendNotificationWebSocket(notificationRequest);
    }

    @KafkaListener(topics = "like", groupId = "notification_group")
    public void likeListener(NotificationRequest request) {;
        notificationService.sendNotificationWebSocket(request);
    }

    // Listener cho hành động comment
    @KafkaListener(topics = "comment", groupId = "notification_group")
    public void commentListener(NotificationRequest request) {
        // request.setMessage(request.getSenderId() + " commented: " + request.getMessage());
        notificationService.sendNotificationWebSocket(request);
    }

    // Listener cho email (OTP từ identity-service)
    @KafkaListener(topics = "email", groupId = "notification_group")
    public void emailListener(EmailResetPasswordRequest emailResetPasswordRequest) {
        System.out.println("Received email reset password: " + emailResetPasswordRequest);
        notificationService.emailNotification(emailResetPasswordRequest);
    }

    // Gửi thông báo chung
    @PostMapping("/send/general")
    public ApiResponse<String> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationKafkaTemplate.send("notification_to_user", notificationRequest);
        return ApiResponse.<String>builder()
                .code(200)
                .message("General notification sent")
                .data("OK")
                .build();
    }

    // Gửi thông báo email (OTP)
    @PostMapping("/send/email")
    public ApiResponse<String> sendEmailNotification(@RequestBody EmailResetPasswordRequest emailRequest) {
        emailKafkaTemplate.send("email", emailRequest);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Email notification sent")
                .data("OK")
                .build();
    }

    @GetMapping("/bulk")
    public ApiResponse<List<NotificationResponse>> getNotificationByIds(@RequestParam(name = "ids") List<String> ids){
        return ApiResponse.<List<NotificationResponse>>builder()
                .code(200)
                .message("Notification by bulk")
                .data(this.notificationService.getNotificationByIds(ids))
                .build();
    }
    @GetMapping("/all")
    public ApiResponse<List<String>> getAll(){
        return ApiResponse.<List<String>>builder()
                .code(200)
                .message("All notification ids")
                .data(this.notificationService.getAllByUserId())
                .build();
    }

    @PutMapping("/bulk")
    public ApiResponse<?> markAsRead(@RequestParam(name = "ids") List<String> ids){
        this.notificationService.markAsRead(ids);
        return ApiResponse.<String>builder()
            .code(200)
            .message("Update successfully")
            .build();
    }
    @PostMapping("/token/{token}")
    public void postMethodName(@PathVariable String token) {
        notificationService.saveToke(token);;
        return;
    }
    
}
