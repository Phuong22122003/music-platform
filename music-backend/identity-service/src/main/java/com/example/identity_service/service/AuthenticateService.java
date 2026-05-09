package com.example.identity_service.service;

import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.example.identity_service.dto.request.AuthenticatedRequest;
import com.example.identity_service.dto.request.ConfirmOtpRequest;
import com.example.identity_service.dto.request.EmailRequest;
import com.example.identity_service.dto.request.IntrospectRequest;
import com.example.identity_service.dto.request.LogoutRequest;
import com.example.identity_service.dto.request.RefreshTokenRequest;
import com.example.identity_service.dto.request.ResetPasswordRequest;
import com.example.identity_service.dto.response.AuthenticatedResponse;
import com.example.identity_service.dto.response.CheckUsernameResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.entity.User;
import com.nimbusds.jose.JOSEException;

@Service
public interface AuthenticateService {
    public AuthenticatedResponse authenticate(AuthenticatedRequest authenticatedRequest) ;
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException ;
    public String generateToken(User user);
    public void logout(LogoutRequest request) throws ParseException, JOSEException ;
    public AuthenticatedResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException ;
    public CheckUsernameResponse checkUsername(String username) ;
    public void sendResetLink(EmailRequest request) ;
    public void resetPassword(ResetPasswordRequest request) ;
    public void sendOtp(EmailRequest email);
    public void confirmOtp(ConfirmOtpRequest otp);
}
