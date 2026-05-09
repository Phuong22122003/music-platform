package com.MusicPlatForm.music_service.httpclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.ProfileWithCountFollowResponse;

@FeignClient(name = "profile-serivce",url = "${app.services.profile}")
public interface ProfileClient {
    @GetMapping("/users/bulk")
    public ApiResponse<List<ProfileWithCountFollowResponse>> getUserProfileByIds(@RequestParam List<String> userIds);
    @GetMapping("/users/{userId}")
    public ApiResponse<ProfileWithCountFollowResponse> getUserProfile(@PathVariable("userId") String userId);
}
