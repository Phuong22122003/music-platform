package com.MusicPlatForm.user_library_service.httpclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.ProfileWithCountFollowResponse;

@FeignClient(name = "profile-service",url = "${app.services.profile}")
public interface ProfileClient {
  @GetMapping("/users/bulk")
  public ApiResponse<List<ProfileWithCountFollowResponse>> getUserProfileByIds(@RequestParam List<String> userIds);
  @GetMapping("/users/top-followed")
  public ApiResponse<List<ProfileWithCountFollowResponse>> getTopFollowedUser(@RequestParam(name = "limit",defaultValue = "10") int limit );
  @GetMapping("/users/{userId}")
  ApiResponse<ProfileWithCountFollowResponse> getUserProfile(@PathVariable("userId") String userId);
}
