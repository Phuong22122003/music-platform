package com.devteria.profile.controller;

import com.devteria.profile.dto.request.ApiResponse;
import com.devteria.profile.dto.response.ProfileWithCountFollowResponse;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.service.FollowsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/follows")
public class FollowsController {
    FollowsService followsService;

    @GetMapping("/get-followers/{userId}")
    public ApiResponse<Page<ProfileWithCountFollowResponse>> getFollowers(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @PathVariable("userId") String userId) {
        return ApiResponse.<Page<ProfileWithCountFollowResponse>>builder().data(followsService.getFollowers(page, size, userId)).build();
    }

    @GetMapping("/get-followings/{userId}")
    public ApiResponse<Page<ProfileWithCountFollowResponse>> getFollowings(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @PathVariable("userId") String userId) {
        return ApiResponse.<Page<ProfileWithCountFollowResponse>>builder().data(followsService.getFollowing(page, size, userId)).build();
    }

    @GetMapping("/is-following")
    public ApiResponse<Boolean> isFollowing(@RequestParam("followerId") String followerId,
                                            @RequestParam("followingId") String followingId) {
        boolean isFollowing = followsService.isFollowing(followerId, followingId);
        return ApiResponse.<Boolean>builder().data(isFollowing).build();
    }
}
