package com.devteria.profile.controller;

import com.devteria.profile.dto.request.ApiResponse;
import com.devteria.profile.dto.response.ProfileWithCountFollowResponse;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserProfileController {
    UserProfileService userProfileService;
    @GetMapping("/{userId}")
    ApiResponse<ProfileWithCountFollowResponse> getUserProfile(@PathVariable("userId") String userId) {
        return ApiResponse.<ProfileWithCountFollowResponse>builder()
                .data(userProfileService.get(userId))
                .build();
    }
    @GetMapping("/bulk")
    public ApiResponse<List<ProfileWithCountFollowResponse>> getUserProfileByIds(@RequestParam List<String> userIds){
        List<ProfileWithCountFollowResponse> response = userProfileService.getByIds(userIds);
        return ApiResponse.<List<ProfileWithCountFollowResponse>>builder()
                .data(response)
                .build();
    }
    @GetMapping("/basic-info")
    public ApiResponse<List<UserProfileResponse>> getBasicUserInfoByIds(@RequestParam List<String> userIds){
         List<UserProfileResponse> response = userProfileService.getBasicUserInfoByIds(userIds);
        return ApiResponse.<List<UserProfileResponse>>builder()
                .data(response)
                .build();
    }

    @GetMapping("/email/{email}")
    ApiResponse<UserProfileResponse> getUserProfileByEmail(@PathVariable("email") String email) {
        UserProfileResponse response = userProfileService.getByEmail(email);
        return ApiResponse.<UserProfileResponse>builder()
                .data(response)
                .build();
    }
    @GetMapping("/top-followed")
    public ApiResponse<List<ProfileWithCountFollowResponse>> getTopFollowedUser(@RequestParam(name = "limit",defaultValue = "10") int limit ){
         List<ProfileWithCountFollowResponse> response = userProfileService.getTopFollowedUser(limit);
        return ApiResponse.<List<ProfileWithCountFollowResponse>>builder()
                .data(response)
                .message("Top followed user")
                .build();
    }
}
