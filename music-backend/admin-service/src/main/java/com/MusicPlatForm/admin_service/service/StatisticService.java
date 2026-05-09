package com.MusicPlatForm.admin_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.MusicPlatForm.admin_service.dto.client.CommentStatisticResponse;
import com.MusicPlatForm.admin_service.dto.client.LikeResponse;
import com.MusicPlatForm.admin_service.dto.client.PlayResponse;
import com.MusicPlatForm.admin_service.dto.client.TopTrack;

@Service
public interface StatisticService {
    // @PreAuthorize("hasRole('ADMIN')")
    public CommentStatisticResponse getCommentStatisticResponse(LocalDate fromDate,LocalDate toDate);

    @PreAuthorize("hasRole('ADMIN')")
    public LikeResponse getLikeResponse(LocalDate fromDate,LocalDate toDate);

    @PreAuthorize("hasRole('ADMIN')")
    public PlayResponse getPlayResponse(LocalDate fromDate,LocalDate toDate);

    @PreAuthorize("hasRole('ADMIN')")
    public List<TopTrack> getTopTracks(LocalDate fromDAte, LocalDate toDate);
}
