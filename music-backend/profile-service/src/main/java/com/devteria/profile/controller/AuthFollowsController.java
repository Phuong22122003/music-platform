package com.devteria.profile.controller;

import com.devteria.profile.dto.request.AddFollowRequest;
import com.devteria.profile.dto.request.ApiResponse;
import com.devteria.profile.service.FollowsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth/follows")
public class AuthFollowsController {
    FollowsService followsService;

    @DeleteMapping("/unfollow/{userId}")
    public  ApiResponse unfollow(@PathVariable("userId") String userId) {
        followsService.unfollow(userId);
        return  ApiResponse.builder().build();
    }

    @PostMapping
    public ApiResponse addFollows(@RequestBody AddFollowRequest request) {
        followsService.addFollower(request);
        return ApiResponse.builder().build();
    }
}
