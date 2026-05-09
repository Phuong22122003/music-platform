package com.devteria.profile.repository;

import com.devteria.profile.dto.request.ApiResponse;
import com.devteria.profile.dto.request.DeleteAvatarRequest;
import com.devteria.profile.dto.response.UploadAvatarResponse;
import com.devteria.profile.dto.response.UploadCoverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-client", url = "${app.services.file}")
public interface FileClient {
    @PostMapping(value = "/images/avatars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UploadAvatarResponse> addAvatar(@RequestPart("avatar") MultipartFile avatar);
    @DeleteMapping(value = "/images/avatars", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<String> deleteAvatar(@RequestBody DeleteAvatarRequest request);
    @PutMapping(value = "/images/avatars/{avatarName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UploadAvatarResponse> replaceAvatar(@RequestPart("newAvatar") MultipartFile newAvatar,
                                                    @PathVariable("avatarName") String oldAvatarName);
    @PostMapping(value = "/images/covers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UploadCoverResponse> addCover(@RequestPart("cover") MultipartFile cover);

    @PutMapping(value = "/images/covers/{coverName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UploadCoverResponse> replaceCover(@RequestPart("newCover") MultipartFile newCover,
                                                    @PathVariable("coverName") String oldCoverName);
}
