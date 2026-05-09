package com.MusicPlatForm.music_service.controller;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.TagResponse;
import com.MusicPlatForm.music_service.dto.request.TagRequest;
import com.MusicPlatForm.music_service.service.implement.TagService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/tags")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthTagController {
    TagService tagService;
    @PostMapping()
    public ApiResponse<TagResponse> createTag(@RequestBody TagRequest request) {
        return ApiResponse.<TagResponse>builder().data(tagService.createTag(request)).build();
    }
    @DeleteMapping("/{tagId}")
    public ApiResponse<String> deleteTag(@PathVariable("tagId") String tagId) {
        return ApiResponse.<String>builder().data(tagService.deleteTag(tagId)).build();
    }
}
