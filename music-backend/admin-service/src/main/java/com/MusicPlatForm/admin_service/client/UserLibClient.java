package com.MusicPlatForm.admin_service.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MusicPlatForm.admin_service.dto.ApiResponse;
import com.MusicPlatForm.admin_service.dto.client.LikeResponse;
import com.MusicPlatForm.admin_service.dto.client.PlayResponse;
import com.MusicPlatForm.admin_service.dto.client.TopTrack;

@FeignClient(name = "user-library-service",url = "${app.services.userlib}")
public interface UserLibClient {
    @GetMapping("/statistic/plays/all")
    public ApiResponse<PlayResponse> getAllPlays(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate);

    @GetMapping("/statistic/likes/all")
    public ApiResponse<LikeResponse> getAllLiked(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate);

    @GetMapping("/statistic/top-tracks/all")
    public ApiResponse<List<TopTrack>> getAllTopTracks(@RequestParam(name = "from_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,@RequestParam(name = "to_date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate);

}