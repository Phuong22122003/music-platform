package com.example.identity_service.controller;

import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.AuthenticatedResponse;
import com.example.identity_service.dto.response.CheckUsernameResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.service.AuthenticateService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/authenticate")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticateController {
    AuthenticateService authenticateService;

    @PostMapping("/login")
    ApiResponse<AuthenticatedResponse> login(@RequestBody @Valid AuthenticatedRequest request) {
        AuthenticatedResponse result = authenticateService.authenticate(request);
        return  ApiResponse.<AuthenticatedResponse>builder().data(result).build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse result = authenticateService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().data(result).build();
    }
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticateService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
    @PostMapping("/refresh-token")
    ApiResponse<AuthenticatedResponse> refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        return ApiResponse.<AuthenticatedResponse>builder().data(authenticateService.refreshToken(request)).build();
    }

    @GetMapping("")
    public String home() {
        return "Welcome! <a href='/oauth2/authorization/google'>Login with Google</a>";
    }

    @GetMapping("/user")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User user) {
        return user.getAttributes();
    }



    @GetMapping("/check-username/{username}")
    ApiResponse<CheckUsernameResponse> isUsernameExisted(@PathVariable("username") String username) {
        return ApiResponse.<CheckUsernameResponse>builder().data(authenticateService.checkUsername(username)).build();
    }

    @PostMapping("/password-reset/request")
    public ApiResponse<Void> requestReset(@RequestBody EmailRequest request) {
        authenticateService.sendResetLink(request);
        return  ApiResponse.<Void>builder().build();
    }

    @PostMapping("/password-reset/reset")
    public ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticateService.resetPassword(request);
        return  ApiResponse.<Void>builder().build();
    }

    @PostMapping("/otp")
    public ApiResponse<Void> sendOtp(@RequestBody EmailRequest emailRequest){
        authenticateService.sendOtp(emailRequest);
        return  ApiResponse.<Void>builder().build();
    }
    @PostMapping("/otp/confirm")
      public ApiResponse<Void> confirmOtp(@RequestBody @Valid ConfirmOtpRequest confirmOtpRequest){
        authenticateService.confirmOtp(confirmOtpRequest);
        return  ApiResponse.<Void>builder().build();
    }
}
