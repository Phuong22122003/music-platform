package com.MusicPlatForm.comment_service.service.implement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.comment_service.client.MusicClient;
import com.MusicPlatForm.comment_service.dto.client.TrackResponse;
import com.MusicPlatForm.comment_service.dto.response.CommentStatisticResponse;
import com.MusicPlatForm.comment_service.dto.response.DailyComment;
import com.MusicPlatForm.comment_service.dto.response.UserStatistic;
import com.MusicPlatForm.comment_service.entity.Comment;
import com.MusicPlatForm.comment_service.repository.CommentRepository;
import com.MusicPlatForm.comment_service.service.StatisticService;

@Service
public class StatisticServiceImplement  implements StatisticService{
    private MusicClient musicClient;
    private CommentRepository commentRepository;
    public StatisticServiceImplement(MusicClient musicClient, CommentRepository commentRepository){
        this.musicClient = musicClient;
        this.commentRepository = commentRepository;
    }
    private CommentStatisticResponse getCommentStats(List<Comment> comments){
        Map<LocalDate,DailyComment> groupCommentByDate= new HashMap<>();
        Map<String, Integer> userCommentCounts = new HashMap<>();
        for(var comment:comments){
            LocalDate date = comment.getCommentAt().toLocalDate();
            String trackId = comment.getTrackId();
            String commentUserId = comment.getUserId();
            DailyComment dailyComment = groupCommentByDate.computeIfAbsent(date, d -> {
                DailyComment dc = new DailyComment();
                dc.setDate(d);
                dc.setCommentCount(0);
                dc.setDetailComment(new HashMap<>());
                return dc;
            });
            dailyComment.setCommentCount(dailyComment.getCommentCount()+1);
            dailyComment.getDetailComment().merge(trackId, 1, Integer::sum);
            userCommentCounts.merge(commentUserId, 1, Integer::sum);  
        }
        List<DailyComment> dailyComments = groupCommentByDate
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(c->c.getValue())
            .toList();
        List<UserStatistic> userStatistics = userCommentCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(u->{
                return new UserStatistic(u.getKey(),u.getValue());
            }).toList();
        CommentStatisticResponse commentStatisticResponse = new CommentStatisticResponse();
        commentStatisticResponse.setDailyComments(dailyComments);
        commentStatisticResponse.setWhoCommented(userStatistics);
        return commentStatisticResponse;
    }

    @PreAuthorize("isAuthenticated()")
    public CommentStatisticResponse getComments(LocalDate fromDate, LocalDate toDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<TrackResponse> tracks = musicClient.getTracksByUserId(userId).getData();
        List<String> trackIds = tracks.stream().map(TrackResponse::getId).toList();
        List<Comment> comments;
        if(fromDate != null){
            comments = this.commentRepository.findCommentsFromDateToDateByTrackIds(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX), trackIds);
        }else comments = this.commentRepository.findAllCommentsByTrackIds(trackIds);
        return getCommentStats(comments);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public CommentStatisticResponse getAllComments(LocalDate fromDate, LocalDate toDate) {
        List<Comment> comments = this.commentRepository.findAllCommentsFromDateToDate(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));
                return getCommentStats(comments);
    }

}
