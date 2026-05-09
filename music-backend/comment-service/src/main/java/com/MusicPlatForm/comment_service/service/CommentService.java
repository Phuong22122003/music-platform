package com.MusicPlatForm.comment_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.MusicPlatForm.comment_service.dto.request.CommentRequest;
import com.MusicPlatForm.comment_service.dto.request.RepliedCommentRequest;
import com.MusicPlatForm.comment_service.dto.response.CommentResponse;

@Service
public interface CommentService {

    public CommentResponse addComment(CommentRequest commentRequest) ;

    public int getCommentLikeCount(String commentId);

    public int getCommentCountByTrackId(String trackId);

    public List<CommentResponse> getCommentsByTrackId(String trackId) ;

    public void likeComment(String commentId) ;

    public void unlikeComment(String commentId);

    public CommentResponse updateComment(String id, String content) ;

    public void deleteComment(String id) ;

    @Deprecated
    // xóa
    public CommentResponse replyToComment(String commentId, CommentRequest request);
    
    public CommentResponse replyComment(String commentId, RepliedCommentRequest request) ;

    public List<CommentResponse> getComments(LocalDate fromDate, LocalDate toDate, List<String> trackIds);
}