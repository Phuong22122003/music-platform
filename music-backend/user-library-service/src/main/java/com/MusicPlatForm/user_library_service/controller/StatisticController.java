package com.MusicPlatForm.user_library_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.CommentStatisticResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.LikeResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.PlayResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.TopTrack;
import com.MusicPlatForm.user_library_service.service.iface.StatisticServiceInterface;

@RestController
@RequestMapping("/statistic")
public class StatisticController {
    @Autowired
    private StatisticServiceInterface statisticService;

    @GetMapping("/plays")
    public ApiResponse<PlayResponse> getPlays(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<PlayResponse>builder().data(statisticService.getPlays(fromDate, toDate)).build();
    }

    @GetMapping("/likes")
    public ApiResponse<LikeResponse> getLiked(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<LikeResponse>builder().data(statisticService.getLiked(fromDate, toDate)).build();
    }

    @GetMapping("/comments")
    public ApiResponse<CommentStatisticResponse> getCommentStatistic(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<CommentStatisticResponse>builder().data(statisticService.getComments(fromDate, toDate)).build();
    }

    @GetMapping("/top-tracks")
    public ApiResponse<List<TopTrack>> getTopTracks(@RequestParam(name = "user_id") String userId,@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<List<TopTrack>>builder().data(statisticService.getUserTopTracks(userId,fromDate, toDate)).build();
    }

    @GetMapping("/plays/all")
    public ApiResponse<PlayResponse> getAllPlays(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<PlayResponse>builder().data(statisticService.getAllPlays(fromDate, toDate)).build();
    }

    @GetMapping("/likes/all")
    public ApiResponse<LikeResponse> getAllLiked(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<LikeResponse>builder().data(statisticService.getAllLiked(fromDate, toDate)).build();
    }


    @GetMapping("/top-tracks/all")
    public ApiResponse<List<TopTrack>> getAllTopTracks(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<List<TopTrack>>builder().data(statisticService.getTopTracks(fromDate, toDate)).build();
    }
}
