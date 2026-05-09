package com.MusicPlatForm.user_library_service.service.iface;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.response.statistic.CommentStatisticResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.LikeResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.PlayResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.TopTrack;

@Service
public interface StatisticServiceInterface {
    public List<TopTrack> getUserTopTracks(String userId,LocalDate fromDate,LocalDate toDate);
    public List<TopTrack> getTopTracks(LocalDate fromDate,LocalDate toDate);
    public PlayResponse getPlays(LocalDate fromDate,LocalDate toDate);
    public PlayResponse getAllPlays(LocalDate fromDate, LocalDate toDate);
    public LikeResponse getLiked(LocalDate fromDate,LocalDate toDate);
    public LikeResponse getAllLiked(LocalDate fromDate,LocalDate toDate);
    public CommentStatisticResponse getComments(LocalDate fromDate,LocalDate toDate);
}
