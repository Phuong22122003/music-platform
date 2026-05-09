package com.MusicPlatForm.comment_service.client;


import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.MusicPlatForm.comment_service.dto.ApiResponse;
import com.MusicPlatForm.comment_service.dto.client.TrackResponse;


@FeignClient(name = "music-service",url = "${app.services.music}")
public interface MusicClient {
    @GetMapping("tracks/users/{user_id}")
    public ApiResponse<List<TrackResponse>> getTracksByUserId(@PathVariable(name = "user_id") String userId);
    @GetMapping("/tracks/{id}")
    public ApiResponse<TrackResponse> getTrackById(@PathVariable String id);
}
