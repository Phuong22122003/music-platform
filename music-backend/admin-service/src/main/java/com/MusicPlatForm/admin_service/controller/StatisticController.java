package com.MusicPlatForm.admin_service.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.admin_service.dto.ApiResponse;
import com.MusicPlatForm.admin_service.dto.client.CommentStatisticResponse;
import com.MusicPlatForm.admin_service.dto.client.LikeResponse;
import com.MusicPlatForm.admin_service.dto.client.PlayResponse;
import com.MusicPlatForm.admin_service.service.StatisticService;

@RestController
@RequestMapping("/statistic")
public class StatisticController {
    
    private StatisticService statisticService;
    public StatisticController(StatisticService statisticService){
        this.statisticService = statisticService;
    }

    @GetMapping("/plays")
    public ApiResponse<PlayResponse> getAllPlays(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<PlayResponse>builder().data(statisticService.getPlayResponse(fromDate, toDate)).build();
    }

    @GetMapping("/likes")
    public ApiResponse<LikeResponse> getAllLiked(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<LikeResponse>builder().data(statisticService.getLikeResponse(fromDate, toDate)).build();
    }
    
    @GetMapping("/comments")
    public ApiResponse<CommentStatisticResponse> getAllComments(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<CommentStatisticResponse>builder().data(statisticService.getCommentStatisticResponse(fromDate, toDate)).build();
    }    
}
