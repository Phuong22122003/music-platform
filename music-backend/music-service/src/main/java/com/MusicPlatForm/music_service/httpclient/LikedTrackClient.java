package com.MusicPlatForm.music_service.httpclient;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.TrackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-library-service",url = "${app.services.user-library}")
public interface LikedTrackClient {

    @GetMapping("/liked/all")
    public ApiResponse<List<TrackResponse>> getAllLikedTracks();

    @GetMapping("/liked/count/{trackId}")
    public ApiResponse<Integer> getTrackLikeCount(@PathVariable String trackId);

    @GetMapping("/liked/tracks/filter_ids")
    public ApiResponse<List<String>> filterLikedTrackId(@RequestParam List<String> ids);
}
