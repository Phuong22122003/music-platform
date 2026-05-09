package com.example.identity_service.repository;

import com.example.identity_service.configuration.FeignClientConfig;
import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.ProfileCreationRequest;
import com.example.identity_service.dto.request.ProfileUpdateRequest;
import com.example.identity_service.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "profile-client", url = "${app.services.profile}", configuration = FeignClientConfig.class)
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    UserProfileResponse createProfile(@RequestBody ProfileCreationRequest request);
    @PutMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    UserProfileResponse updateProfile(@RequestBody ProfileUpdateRequest request);
    @GetMapping(value = "/users/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserProfileResponse> getProfile(@PathVariable("email") String email);
}
