package com.MusicPlatForm.user_library_service.controller;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.service.iface.LikeTrackServiceInterface;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/liked")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikedTrackController {
    LikeTrackServiceInterface likedTrackService;

    /**
     * Lấy toàn bộ nhạc user đó đã like
     * @return
     */
    @GetMapping("/all")
    public ApiResponse<List<TrackResponse>> getAllLikedTracks() {
        List<TrackResponse> likedTracks = likedTrackService.getAllLikedTracks();
        return ApiResponse.<List<TrackResponse>>builder().data(likedTracks).code(200).build();
    }

    @GetMapping("/count/{trackId}")
    public ApiResponse<Integer> getTrackLikeCount(@PathVariable String trackId) {
        int likeCount = likedTrackService.getTrackLikeCount(trackId);
        return ApiResponse.<Integer>builder().code(200).data(likeCount).build();
    }

    @GetMapping("/is_liked/{trackId}")
    public ApiResponse<Boolean> isLiked(@PathVariable String trackId) {
        return ApiResponse.<Boolean>builder().data(likedTrackService.isLiked(trackId)).code(200).build();
    }

    @PostMapping("/like/{trackId}")
    public ApiResponse<Boolean> likeTrack(@PathVariable String trackId) {
        return ApiResponse.<Boolean>builder().data(likedTrackService.likeTrack(trackId)).code(200).build();
    }

    @PostMapping("/unlike/{track_id}")
    public ApiResponse<Boolean> unLikeTrack(@PathVariable String track_id) {
        return ApiResponse.<Boolean>builder().data(likedTrackService.unLikeTrack(track_id)).code(200).build();
    }

    
    @GetMapping("/users/{trackId}")
    public ApiResponse<List<String>> getUserIdsLikedTrack(@PathVariable String trackId){
        return ApiResponse.<List<String>>builder().data(this.likedTrackService.getUserIdsLikedTrack(trackId)).code(200).build();
    }
    @GetMapping("/tracks/filter_ids")
    public ApiResponse<List<String>> filterLikedTrackId(@RequestParam List<String> ids){
        return ApiResponse.<List<String>>builder().data(this.likedTrackService.filterLikedIds(ids)).code(200).build();

    } 
}
