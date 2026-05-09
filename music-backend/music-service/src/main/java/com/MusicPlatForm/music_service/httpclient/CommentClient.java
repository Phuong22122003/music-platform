package com.MusicPlatForm.music_service.httpclient;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.CommentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "comment-service",url = "${app.services.comment}")
public interface CommentClient {

    @GetMapping("/comments/track/{trackId}")
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable String trackId);

    @GetMapping("/comments/likes/{commentId}")
    public ApiResponse<Integer> getCommentLikeCount(@PathVariable String commentId);
}
