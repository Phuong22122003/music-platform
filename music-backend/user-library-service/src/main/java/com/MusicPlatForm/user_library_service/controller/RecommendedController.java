package com.MusicPlatForm.user_library_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.ProfileWithCountFollowResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.service.iface.RecommendedServiceInterface;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/recommended")
@RequiredArgsConstructor 
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RecommendedController {
    RecommendedServiceInterface recommendedService;
    @GetMapping("/mixed-for")
    public ResponseEntity<ApiResponse<?>> getMixforUser(){
        return ResponseEntity.ok().body( ApiResponse.builder().data(recommendedService.getMixedForUser()).build());
    } 


    /**
     * Lấy track theo trackId từ đó lấy thể loại và tìm track theo thể loại.
     * Nếu track không có thể loại thì lấy toàn bộ nhạc user đã like làm response;
     * @param trackId
     * @return
     */
    @GetMapping("/related-track/{track_id}")
    public ResponseEntity<ApiResponse<List<TrackResponse>>> getSimilartToLikedTrack(@PathVariable(name = "track_id") String trackId){
        return ResponseEntity.ok().body(
            ApiResponse.<List<TrackResponse>>builder()
                                            .code(HttpStatus.OK.value())
                                            .data(recommendedService.getRelatedTrack(trackId))
                                            .build()
        );
    } 

    @GetMapping("/group-by-genre")
    public ResponseEntity<ApiResponse<Map<String,List<TrackResponse>>>> getTracksGroupedByGenre(){
        return ResponseEntity.ok().body(ApiResponse.<Map<String,List<TrackResponse>>>builder().data(recommendedService.getGroupedTrackByGenres()).build());
    }

    @GetMapping("/more-of-you-like")
    public ResponseEntity<ApiResponse<?>> moreOfWhatYouLike(){
        return ResponseEntity.ok().body(ApiResponse.builder().data(recommendedService.getMoreOfWhatYouLike()).build());
    }

    @GetMapping("/artist-you-should-know")
    public ApiResponse<List<ProfileWithCountFollowResponse>> artirstYouShouldKnow(){
        return ApiResponse.<List<ProfileWithCountFollowResponse>> builder().data(this.recommendedService.getArtirstsShouldKnow()).build();
    }


    // trending
    // theo the loai
    @GetMapping("/{genre_id}/trending/tracks")
    public ApiResponse<List<TrackResponse>> trending(@PathVariable(name = "genre_id") String genreId){
        return ApiResponse.<List<TrackResponse>> builder()
                .data(this.recommendedService.getTrendingTracksByGenre(genreId))
                .build();
    }
}
