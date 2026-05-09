package com.MusicPlatForm.user_library_service.httpclient;

import java.time.LocalDate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.CommentStatisticResponse;

@FeignClient(name = "comment-service",url = "${app.services.comment}")
public interface CommentClient {
    @GetMapping("statistic/comments")
    public ApiResponse<CommentStatisticResponse> getCommentStatistic(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate);
}
