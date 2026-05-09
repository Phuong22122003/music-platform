package com.MusicPlatForm.notification_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.notification_service.dto.ApiResponse;
import com.MusicPlatForm.notification_service.dto.request.EmailResetPasswordRequest;
import com.MusicPlatForm.notification_service.dto.request.PasswordRequest;
import com.MusicPlatForm.notification_service.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notification")
public class EmailController {
    @Autowired private NotificationService notificationService;

    @PostMapping("/password/otp")
    public ApiResponse<?> sendOtp(@RequestBody @Valid EmailResetPasswordRequest request){
        notificationService.emailNotification(request);
        return ApiResponse.builder().message("Successfully").build();
    } 
    @PostMapping("/password/new")
      public ApiResponse<?> sendNewPassword(@RequestBody @Valid PasswordRequest request){
        notificationService.emailNewPassword(request);
        return ApiResponse.builder().message("Successfully").build();
    } 

}
