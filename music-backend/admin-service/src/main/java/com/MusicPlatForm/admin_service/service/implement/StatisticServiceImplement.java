package com.MusicPlatForm.admin_service.service.implement;


import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.admin_service.client.CommentClient;
import com.MusicPlatForm.admin_service.client.UserLibClient;
import com.MusicPlatForm.admin_service.dto.client.CommentStatisticResponse;
import com.MusicPlatForm.admin_service.dto.client.LikeResponse;
import com.MusicPlatForm.admin_service.dto.client.PlayResponse;
import com.MusicPlatForm.admin_service.dto.client.TopTrack;
import com.MusicPlatForm.admin_service.service.StatisticService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticServiceImplement implements StatisticService{
    UserLibClient userLibClient;
    CommentClient commentClient;

    @PreAuthorize("hasRole('ADMIN')")
    public CommentStatisticResponse getCommentStatisticResponse(LocalDate fromDate,LocalDate toDate){
        return commentClient.getCommentStatistic(fromDate,toDate).getData();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public LikeResponse getLikeResponse(LocalDate fromDate,LocalDate toDate){
        return userLibClient.getAllLiked(fromDate,toDate).getData();
    }
    @PreAuthorize("hasRole('ADMIN')")
    public PlayResponse getPlayResponse(LocalDate fromDate,LocalDate toDate){
        return userLibClient.getAllPlays(fromDate,toDate).getData();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TopTrack> getTopTracks(LocalDate fromDAte, LocalDate toDate){
        return userLibClient.getAllTopTracks(fromDAte, toDate).getData();
    }
}
