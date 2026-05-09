package com.MusicPlatForm.comment_service.mapper;

import com.MusicPlatForm.comment_service.dto.request.CommentRequest;
import com.MusicPlatForm.comment_service.dto.request.LikedCommentRequest;
import com.MusicPlatForm.comment_service.dto.request.RepliedCommentRequest;
import com.MusicPlatForm.comment_service.dto.response.CommentResponse;
import com.MusicPlatForm.comment_service.dto.response.LikedCommentResponse;
import com.MusicPlatForm.comment_service.entity.Comment;
import com.MusicPlatForm.comment_service.entity.LikedComment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "trackId", target = "trackId")
    @Mapping(source = "content", target = "content")
    Comment toComment(CommentRequest commentRequest);
        
    @Mapping(source = "content", target = "content")
    Comment toRepliedComment(RepliedCommentRequest repliedCommentRequest);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trackId", target = "trackId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "commentAt", target = "commentAt")
    @Mapping(source = "likeCount", target = "likeCount")
    @Mapping(source = "repliedUserId", target = "repliedUserId")
    @Mapping(target = "replies", ignore = true)
    CommentResponse toCommentResponse(Comment comment);

    List<CommentResponse> toCommentResponseList(List<Comment> comments);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "likeAt", target = "likeAt")
    @Mapping(source = "comment.id", target = "commentId")
    LikedCommentResponse toLikedCommentResponse(LikedComment likedComment);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "likeAt", target = "likeAt")
    @Mapping(source = "commentId", target = "comment.id")
    LikedComment toLikedComment(LikedCommentRequest likedCommentRequest);
}
