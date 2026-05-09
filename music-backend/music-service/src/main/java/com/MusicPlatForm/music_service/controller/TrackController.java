package com.MusicPlatForm.music_service.controller;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.MusicPlatForm.music_service.dto.request.UpdateTrackRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.TrackResponse;
import com.MusicPlatForm.music_service.dto.request.TrackRequest;
import com.MusicPlatForm.music_service.service.iface.TrackServiceInterface;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/track")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Deprecated
public class TrackController {
    TrackServiceInterface trackService;
    @GetMapping("/test")
    public void a(){}

   @PostMapping(value = "/add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ApiResponse<String> addTrack(@RequestPart(name = "cover_image", required = false) MultipartFile coverImage,
                                       @RequestPart(name = "track_audio", required = false) MultipartFile trackAudio,
                                       @RequestPart(name = "track", required = false) TrackRequest trackRequest){
       this.trackService.uploadTrack(coverImage, trackAudio, trackRequest);
       return ApiResponse.<String>builder()
                       .code(HttpStatus.OK.value())
                       .data("Added track successfully")
                       .build();
   }

    @PostMapping(value = "/add/multi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> addTracks(@RequestPart("trackFiles") List<MultipartFile> trackFiles, @RequestPart("trackRequests") String trackJsonRequests){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert JSON string back to List<TrackRequest>
            List<TrackRequest> trackRequestList = objectMapper.readValue(trackJsonRequests, new TypeReference<List<TrackRequest>>() {});

            List<TrackResponse> trackResponses = trackService.uploadTracks(trackFiles, trackRequestList);
            return ApiResponse.<List<TrackResponse>>builder()
                    .code(HttpStatus.OK.value())
                    .data(trackResponses)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .code(400)
                    .message("Failed to parse track request")
                    .build();
        }
    } 

    @DeleteMapping(value = "delete/{trackId}")
    public ApiResponse<String> deleteTrack(@PathVariable String trackId){
        trackService.deleteTrack(trackId);
        return ApiResponse.<String>builder().code(200).message("Deleted successfully").build();
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getTrackById(@PathVariable String id){
        TrackResponse track = this.trackService.getTrackById(id);
        return ApiResponse.<TrackResponse> builder().code(HttpStatus.OK.value()).data(track).build();
    }
    @GetMapping("/users/{user_id}")
    public ApiResponse<?> getTracksByUserId(@PathVariable("user_id") String userId){
        List<TrackResponse> tracks = this.trackService.getTracksByUserId(userId);
        return ApiResponse.<List<TrackResponse>> builder().code(HttpStatus.OK.value()).data(tracks).build();
    }

    @GetMapping("/list")
    public ApiResponse<List<TrackResponse>> getTracksByIds(@RequestParam List<String> ids){
        List<TrackResponse> tracks = this.trackService.getTracksByIds(ids);
        return ApiResponse.<List<TrackResponse>> builder().code(HttpStatus.OK.value()).data(tracks).build();
    }
    @GetMapping("/list-by-genre")
    public ApiResponse<List<TrackResponse>> getTracksByGenre(@RequestParam String genreId,@RequestParam int limit){
        List<TrackResponse> tracks = this.trackService.getTracksByGenre(genreId, limit);
        return ApiResponse.<List<TrackResponse>> builder().code(HttpStatus.OK.value()).data(tracks).build();
    }
    @GetMapping("/random")
    public ApiResponse<?> getRandomTracks( @RequestParam(name = "limit") int limit) {
        return ApiResponse.builder()
            .data(trackService.getRandomTracks(limit))
            .code(200).build();
    }
    @PutMapping("/update/{trackId}")
    public ApiResponse<TrackResponse> updateTrack(@PathVariable("trackId") String trackId, @RequestPart("meta-data") UpdateTrackRequest request, @RequestPart(name = "image", required = false)MultipartFile imageFile, @RequestPart(name = "track", required = false) MultipartFile trackFile ) {
        return ApiResponse.<TrackResponse>builder().data(trackService.updateTrack(trackId, request, imageFile, trackFile)).build();
    }
}
