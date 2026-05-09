package com.MusicPlatForm.comment_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MusicPlatForm.comment_service.dto.ApiResponse;
import com.MusicPlatForm.comment_service.dto.client.UserProfileResponse;


@FeignClient(name = "profile-service",url = "${app.services.profile}")
public interface ProfileClient {
  @GetMapping("/users/basic-info")
  public ApiResponse<List<UserProfileResponse>> getBasicUserInfoByIds(@RequestParam List<String> userIds);
}
