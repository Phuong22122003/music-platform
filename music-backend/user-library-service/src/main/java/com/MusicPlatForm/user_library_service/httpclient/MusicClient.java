package com.MusicPlatForm.user_library_service.httpclient;


import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.user_library_service.dto.request.playlist.client.TrackRequest;
import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TagResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.GenreResponse;

import feign.Headers;

@FeignClient(name = "music-service",url = "${app.services.music}")
public interface MusicClient {

    @Deprecated
    @PostMapping(value = "/track/add/multi",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<TrackResponse>> addMultiTrack(@RequestPart MultipartFile[] trackFiles,
                                                          @RequestPart TrackRequest[] trackRequests);
    
                                                          @Deprecated
    @PostMapping(value = "/track/add/multi",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<TrackResponse>> addMultiTrack(@RequestPart MultipartFile[] trackFiles,
                                                          @RequestPart String trackRequests);
    @Deprecated
    @PostMapping(value = "/track/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addTrack(@RequestPart("track_audio") MultipartFile trackAudio, @RequestPart("track") TrackRequest trackRequest);


    @PostMapping(value = "/tracks/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    ApiResponse<List<TrackResponse>> addMultiTrack(@RequestPart("trackFiles") List<MultipartFile> trackFiles,
                                               @RequestPart("trackRequests") String trackRequests);
    @GetMapping("/tracks/{id}")
    public ApiResponse<TrackResponse> getTrackById(@PathVariable String id);

    @PostMapping("/tracks/plays-cout/{id}")
    public ApiResponse<?> updatePlayCount(@PathVariable String id);
    @GetMapping("/tracks/bulk")//ok
    public ApiResponse<List<TrackResponse>> getTrackByIds(@RequestParam List<String> ids);
    @GetMapping("/tracks/by-genre")
    public ApiResponse<List<TrackResponse>> getTracksByGenre(@RequestParam String genreId,@RequestParam  int limit);
    @GetMapping("/tracks/random")
    public ApiResponse<List<TrackResponse>> getRandomTracks(@RequestParam int limit);

    @GetMapping("tracks/users/{user_id}")
    public ApiResponse<List<TrackResponse>> getTracksByUserId(@PathVariable(name = "user_id") String userId);

    // =========== Tag===============
    @GetMapping("/tags/bulk")//ok
    public ApiResponse<List<TagResponse>> getTagsByIds(@RequestParam List<String> ids);
    
    //============Genre=================
    @GetMapping("/genres/bulk")//ok
    public ApiResponse<List<GenreResponse>> getGenresByIds(@RequestParam List<String> ids);
    @GetMapping("/genres/{id}")
    public ApiResponse<GenreResponse> getGenreById(@PathVariable String id);
    @GetMapping("/genres")
    public ApiResponse<List<GenreResponse>> getGenres();
}
