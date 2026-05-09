package com.MusicPlatForm.user_library_service.httpclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.user_library_service.dto.response.AddCoverFileResponse;
import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;


@FeignClient(name = "file-service",url = "${app.services.file}")
public interface FileClient {
    @PostMapping(value = "/images/covers",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AddCoverFileResponse> addCover(@RequestPart MultipartFile cover);


    @PutMapping(value = "/images/covers/{oldCoverName}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AddCoverFileResponse> replaceCover(@RequestPart MultipartFile newCover, @PathVariable String oldCoverName);

    @DeleteMapping(value = "/images/covers/{coverName}")
    ApiResponse<String> deleteCoverImage(@PathVariable String coverName);

}
