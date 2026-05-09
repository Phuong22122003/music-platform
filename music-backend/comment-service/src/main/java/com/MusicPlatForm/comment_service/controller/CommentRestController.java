package com.MusicPlatForm.comment_service.controller;

import com.MusicPlatForm.comment_service.dto.ApiResponse;
import com.MusicPlatForm.comment_service.dto.request.CommentRequest;
import com.MusicPlatForm.comment_service.dto.request.RepliedCommentRequest;
import com.MusicPlatForm.comment_service.dto.request.UpdateCommentRequest;
import com.MusicPlatForm.comment_service.dto.response.CommentResponse;
import com.MusicPlatForm.comment_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentRestController {
    CommentService commentService;

    @GetMapping("/count")
    public ApiResponse<Integer> getCommentCountByTrackId(@RequestParam("track_id") String trackId){
        return ApiResponse.<Integer>builder()
            .data(commentService.getCommentCountByTrackId(trackId))
            .build();
    }

    @Deprecated
    @PostMapping("/add")
    public ApiResponse<CommentResponse> addComment(@RequestBody @Valid CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Comment added successfully")
                .data(commentService.addComment(request))
                .build();
    }
    @PostMapping()
    public ApiResponse<CommentResponse> addCommentV1(@RequestBody @Valid CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Comment added successfully")
                .data(commentService.addComment(request))
                .build();
    }

    @Deprecated
    @GetMapping("/track/{trackId}")
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable String trackId) {
        List<CommentResponse> comments = commentService.getCommentsByTrackId(trackId);
        return ApiResponse.<List<CommentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success") 
                .data(comments)
                .build();
    }
    @GetMapping()
    public ApiResponse<List<CommentResponse>> getCommentsByTrackId(@RequestParam(name = "track_id") String trackId) {
        List<CommentResponse> comments = commentService.getCommentsByTrackId(trackId);
        return ApiResponse.<List<CommentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success") 
                .data(comments)
                .build();
    }

    @GetMapping("/likes/{commentId}")
    public ApiResponse<Integer> getCommentLikeCount(@PathVariable String commentId) {
        return ApiResponse.<Integer>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(commentService.getCommentLikeCount(commentId))
                .build();
    }

    @PostMapping("/{id}/likes")
    public ApiResponse<Void> likeComment(@PathVariable(name = "id") String commentId) {
        commentService.likeComment(commentId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Comment liked successfully")
                .build();
    }

    @DeleteMapping("/{id}/likes")
    public ApiResponse<Void> unlikeComment(@PathVariable(name = "id") String commentId) {
        commentService.unlikeComment(commentId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Comment unliked successfully")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable String id, @RequestBody UpdateCommentRequest content) {
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Comment updated successfully")
                .data(commentService.updateComment(id, content.getContent()))
                .build();
    }

    @Deprecated
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Comment deleted successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCommentById(@PathVariable String id) {
        commentService.deleteComment(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Comment deleted successfully")
                .build();
    }

    @PostMapping("/{commentId}/replies")
    public ApiResponse<CommentResponse> replyComment(@PathVariable String commentId, @RequestBody @Valid RepliedCommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Reply added successfully")
                .data(commentService.replyComment(commentId, request))
                .build();
    }

    @GetMapping("/bulk")
    public ApiResponse<List<CommentResponse>> getComments(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate,@RequestParam(name = "track_ids")List<String> ids){
        return ApiResponse.<List<CommentResponse>>builder().data(commentService.getComments(fromDate, toDate, ids)).build();
    }
}