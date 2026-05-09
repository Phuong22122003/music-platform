package com.MusicPlatForm.music_service.controller;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.CommentResponse;
import com.MusicPlatForm.music_service.service.ManageTrackService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/track-statistics")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManageTrackController {
    ManageTrackService manageTrackService;

    @GetMapping("/comments/{trackId}")
    public ApiResponse<List<CommentResponse>> getCommentsForTrack(@PathVariable String trackId) {
        List<CommentResponse> comments = manageTrackService.getCommentsForTrack(trackId);
        return ApiResponse.<List<CommentResponse>> builder().code(HttpStatus.OK.value()).data(comments).build();
    }

    @GetMapping("/comments/count/{trackId}")
    public ApiResponse<Integer> getCommentCountForTrack(@PathVariable String trackId) {
        int count = manageTrackService.getCommentCountForTrack(trackId);
        return ApiResponse.<Integer> builder().code(HttpStatus.OK.value()).data(count).build();
    }

    @GetMapping("/cout_play/{trackId}")
    public ApiResponse<Integer> getCountPlayForTrack(@PathVariable String trackId){
        int count = manageTrackService.getCountPlay(trackId);
        return ApiResponse.<Integer> builder().code(HttpStatus.OK.value()).data(count).build();
    }

    @GetMapping("/comments/likes/{trackId}")
    public ApiResponse<Integer> getTotalCommentLikes(@PathVariable String trackId) {
        int count = manageTrackService.getTotalCommentLikes(trackId);
        return ApiResponse.<Integer> builder().code(HttpStatus.OK.value()).data(count).build();
    }

    @GetMapping("/liked/count/{trackId}")
    public ApiResponse<Integer> getTrackLikeCount(@PathVariable String trackId) {
        int count = manageTrackService.getTrackLikeCount(trackId);
        return ApiResponse.<Integer> builder().code(HttpStatus.OK.value()).data(count).build();
    }
}
