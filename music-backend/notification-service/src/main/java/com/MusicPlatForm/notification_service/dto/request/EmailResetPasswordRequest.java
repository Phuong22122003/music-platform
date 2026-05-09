package com.MusicPlatForm.notification_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailResetPasswordRequest {

    @Email(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_REQUIRED")
    private String userEmail;
    @NotBlank(message = "OTP_REQUIRED")
    private String otp;
    public EmailResetPasswordRequest() {
    }
    public EmailResetPasswordRequest(String userEmail, String otp) {
        this.userEmail = userEmail;
        this.otp = otp;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }

}
