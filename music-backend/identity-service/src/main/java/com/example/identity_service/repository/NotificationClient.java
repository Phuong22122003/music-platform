package com.example.identity_service.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.identity_service.configuration.FeignClientConfig;
import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.EmailResetPasswordRequest;
import com.example.identity_service.dto.request.PasswordRequest;

@FeignClient(name = "notification-service", url = "${app.services.notification}")
public interface NotificationClient {
    @PostMapping("/notification/password/otp")
    public ApiResponse<?> sendOtp(EmailResetPasswordRequest request);
    @PostMapping("/notification/password/new")
      public ApiResponse<?> sendNewPassword(PasswordRequest request);
}
