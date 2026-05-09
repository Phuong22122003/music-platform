package com.MusicPlatForm.comment_service.service.implement;

import com.MusicPlatForm.comment_service.client.MusicClient;
import com.MusicPlatForm.comment_service.client.ProfileClient;
import com.MusicPlatForm.comment_service.dto.client.TrackResponse;
import com.MusicPlatForm.comment_service.dto.client.UserProfileResponse;
import com.MusicPlatForm.comment_service.dto.kafka.NotificationRequest;
import com.MusicPlatForm.comment_service.dto.request.CommentRequest;
import com.MusicPlatForm.comment_service.dto.request.RepliedCommentRequest;
import com.MusicPlatForm.comment_service.dto.response.CommentResponse;
import com.MusicPlatForm.comment_service.entity.Comment;
import com.MusicPlatForm.comment_service.entity.LikedComment;
import com.MusicPlatForm.comment_service.exception.AppException;
import com.MusicPlatForm.comment_service.exception.ErrorCode;
import com.MusicPlatForm.comment_service.mapper.CommentMapper;
import com.MusicPlatForm.comment_service.repository.CommentRepository;
import com.MusicPlatForm.comment_service.repository.LikedCommentRepository;
import com.MusicPlatForm.comment_service.service.CommentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImplement implements CommentService{
    CommentRepository commentRepository;
    LikedCommentRepository likedCommentRepository;
    CommentMapper  commentMapper;
    KafkaTemplate<String,Object> kafkaTemplate;
    MusicClient musicClient;
    ProfileClient profileClient;
    private void sendNotification(Comment comment){
        String trackId = comment.getTrackId();
        TrackResponse track = musicClient.getTrackById(trackId).getData();
        NotificationRequest notification;
        // trường hợp 1 tự comment trên track của mình và không reply -> không cần thông báo
        if(track.getUserId().equals(comment.getUserId())&&comment.getRepliedUserId()==null) return;
        
        // trường hợp 2 người tự reply chính mình
        if(comment.getUserId().equals(comment.getRepliedUserId())) return;
        
        // trường hợp 3 người khác comment trên track của mình -> gửi thông báo tới chủ track
        if(!track.getUserId().equals(comment.getUserId())){
            notification = NotificationRequest
                            .builder()
                            .senderId(comment.getUserId())// người gửi là người comment;
                            .recipientId(track.getUserId())// người nhận là chủ track
                            .trackId(trackId)
                            .commentId(comment.getId())
                            .message("commented \"" + comment.getContent() +"\" on your track")
                            .build();
            kafkaTemplate.send("comment",notification);
        }
        // Trường hợp 4 người khác reply comment của 1 người khác nữa(không phải chủ track) -> gửi thông báo tới người khác đó
        if(comment.getRepliedUserId()!=null&&!comment.getRepliedUserId().equals("")){
             notification = NotificationRequest
                            .builder()
                            .senderId(comment.getUserId())// người gửi là người comment;
                            .recipientId(comment.getRepliedUserId())// người nhận là chủ track
                            .trackId(trackId)
                            .commentId(comment.getId())
                            .message("mention you \"" + comment.getContent()+"\"")
                            .build();
            kafkaTemplate.send("comment",notification);
        }
    }
    public CommentResponse addComment(CommentRequest commentRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(authentication==null || !authentication.isAuthenticated()||authentication instanceof AnonymousAuthenticationToken){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Comment comment = commentMapper.toComment(commentRequest);
        comment.setUserId(userId);
        comment.setCommentAt(LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setParentComment(null);
        comment.setRepliedUserId(null);
        comment = commentRepository.save(comment);
        CommentResponse commentResponse =  commentMapper.toCommentResponse(comment);
        commentResponse.setIsLiked(false);
        sendNotification(comment);
        return commentResponse;
    }

    public int getCommentLikeCount(String commentId) {
        return likedCommentRepository.countByCommentId(commentId);
    }

    
    public List<CommentResponse> getCommentsByTrackId(String trackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<Comment> comments = commentRepository.findByTrackId(trackId);
        List<CommentResponse> responseList = new ArrayList<>();

        Map<String,UserProfileResponse> idToUser = new HashMap<>();
        Set<String> userIds = new HashSet<>();
        for(Comment comment: comments){
            userIds.add(comment.getUserId());
            for(Comment repliedComment: comment.getReplies()){
                userIds.add(repliedComment.getUserId());
            }
        }
        List<UserProfileResponse> users = profileClient.getBasicUserInfoByIds(new ArrayList<>(userIds)).getData();
        users.forEach(u->idToUser.put(u.getUserId(), u));

        for (Comment parent : comments) {
            CommentResponse parentDto = commentMapper.toCommentResponse(parent);
            List<CommentResponse> replys = new ArrayList<>();
            List<Comment> replies = parent.getReplies();
            parentDto.setIsLiked(false);
            UserProfileResponse user =idToUser.get(parent.getUserId());
            parentDto.setUser(user);
            if(this.likedCommentRepository.countByCommentIdAndUserId(parent.getId(), userId)==1){
                parentDto.setIsLiked(true);
            }
            if (replies != null) {
                for (Comment reply : replies) {
                    CommentResponse commentResponse = commentMapper.toCommentResponse(reply);
                    UserProfileResponse userRepliedComment = idToUser.get(reply.getUserId());
                    commentResponse.setUser(userRepliedComment);
                    commentResponse.setIsLiked(false);
                    if(this.likedCommentRepository.countByCommentIdAndUserId(reply.getId(), userId)==1){
                        commentResponse.setIsLiked(true);
                    }
                    replys.add(commentResponse);
                }
            }
            parentDto.setReplies(replys);
            responseList.add(parentDto);
        }
        return responseList;
    }

    public void likeComment(String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        LikedComment likedComment = likedCommentRepository.findByCommentIdAndUserId(commentId, userId);
        if(likedComment!=null) return;
        likedComment = LikedComment.builder()
                .userId(userId)
                .likeAt(LocalDateTime.now())
                .comment(comment)
                .build();
        likedCommentRepository.save(likedComment);
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }

    public void unlikeComment(String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        LikedComment likedComments = likedCommentRepository.findByCommentIdAndUserId(commentId, userId);

        if (likedComments == null) {
            // throw new AppException(ErrorCode.ALREADY_UNLIKE);
            return;
        }

        likedCommentRepository.delete(likedComments);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        int updatedLikeCount = Math.max(0, comment.getLikeCount() - 1);
        comment.setLikeCount(updatedLikeCount);

        commentRepository.save(comment);
    }

    public CommentResponse updateComment(String id, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setContent(content);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public void deleteComment(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        if(!comment.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);
        commentRepository.delete(comment);
    }

    @Deprecated
    // xóa
    public CommentResponse replyToComment(String commentId, CommentRequest request) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        Comment replyComment = commentMapper.toComment(request);
        replyComment.setCommentAt(LocalDateTime.now());
        replyComment.setLikeCount(0);
        replyComment.setTrackId(parentComment.getTrackId());
        replyComment.setParentComment(parentComment);
        replyComment = commentRepository.save(replyComment);
        return commentMapper.toCommentResponse(replyComment);
    }
    public CommentResponse replyComment(String commentId, RepliedCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        Comment parentComment = comment.getParentComment();
        if(parentComment==null){
            parentComment = comment;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Comment replyComment = commentMapper.toRepliedComment(request);
        replyComment.setTrackId(comment.getTrackId());
        replyComment.setUserId(userId);
        replyComment.setRepliedUserId(comment.getUserId());
        replyComment.setCommentAt(LocalDateTime.now());
        replyComment.setLikeCount(0);
        replyComment.setTrackId(parentComment.getTrackId());
        replyComment.setParentComment(parentComment);
        replyComment = commentRepository.save(replyComment);
        sendNotification(replyComment);
        return commentMapper.toCommentResponse(replyComment);
    }

    public List<CommentResponse> getComments(LocalDate fromDate, LocalDate toDate, List<String> trackIds){
        List<Comment> comments = this.commentRepository.findCommentsFromDateToDateByTrackIds(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX), trackIds);
        return commentMapper.toCommentResponseList(comments);
    }
    @Override
    public int getCommentCountByTrackId(String trackId) {
        return this.commentRepository.countByTrackId(trackId);
    }
}