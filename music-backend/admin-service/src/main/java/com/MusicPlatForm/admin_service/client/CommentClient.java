package com.MusicPlatForm.admin_service.client;

import java.time.LocalDate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MusicPlatForm.admin_service.dto.ApiResponse;
import com.MusicPlatForm.admin_service.dto.client.CommentStatisticResponse;


@FeignClient(name = "comment-service",url = "${app.services.comment}")
public interface CommentClient {
    @GetMapping("/statistic/comments/all")
    public ApiResponse<CommentStatisticResponse> getCommentStatistic(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate);
}
