package com.MusicPlatForm.comment_service.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.comment_service.dto.ApiResponse;
import com.MusicPlatForm.comment_service.dto.response.CommentStatisticResponse;
import com.MusicPlatForm.comment_service.service.StatisticService;

@RestController
@RequestMapping("/statistic")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/comments")
    public ApiResponse<CommentStatisticResponse> getCommentStatistic(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<CommentStatisticResponse>builder().data(statisticService.getComments(fromDate, toDate)).build();
    }
    @GetMapping("/comments/all")
    public ApiResponse<CommentStatisticResponse> getAllCommentStatistic(@RequestParam(name = "from_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date",required = false)@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate){
        return ApiResponse.<CommentStatisticResponse>builder().data(statisticService.getAllComments(fromDate, toDate)).build();
    }

}
