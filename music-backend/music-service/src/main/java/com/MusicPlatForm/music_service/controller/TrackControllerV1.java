package com.MusicPlatForm.music_service.controller;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.MusicPlatForm.music_service.dto.request.UpdateTrackRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/tracks")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackControllerV1 {
    @Autowired private TrackServiceInterface trackService;

    @PostMapping("/plays-cout/{id}")
    public ApiResponse<String> updatePlayCount(@PathVariable String id){
        trackService.updatePlayCountByTrackId(id);
        return ApiResponse.<String>builder().data("Success").build();
    }

   @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ApiResponse<TrackResponse> addTrack(@RequestPart(name = "cover_image", required = false) MultipartFile coverImage,
                                       @RequestPart(name = "track_audio", required = false) MultipartFile trackAudio,
                                       @RequestPart(name = "track", required = false) TrackRequest trackRequest){
       TrackResponse trackResponse = this.trackService.uploadTrack(coverImage, trackAudio, trackRequest);
       return ApiResponse.<TrackResponse>builder()
                       .code(HttpStatus.OK.value())
                       .data(trackResponse)
                       .build();
   }

    @PostMapping(value = "bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @DeleteMapping(value = "/{trackId}")
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
    public ApiResponse<?> getTracksByUserId(@PathVariable(name = "user_id") String userId){
        List<TrackResponse> tracks = this.trackService.getTracksByUserId(userId);
        return ApiResponse.<List<TrackResponse>> builder().code(HttpStatus.OK.value()).data(tracks).build();
    }
    // V1
    @GetMapping("/bulk")
    public ApiResponse<List<TrackResponse>> getTracksByIds(@RequestParam List<String> ids){
        List<TrackResponse> tracks = this.trackService.getTracksByIds(ids);
        return ApiResponse.<List<TrackResponse>> builder().code(HttpStatus.OK.value()).data(tracks).build();
    }
    @GetMapping("/by-genre")
    public ApiResponse<List<TrackResponse>> getTracksByGenre(@RequestParam String genreId,@RequestParam int limit){
        List<TrackResponse> tracks = this.trackService.getTracksByGenre(genreId, limit);
        return ApiResponse.<List<TrackResponse>> builder().code(HttpStatus.OK.value()).data(tracks).build();
    }

    @PutMapping("/{trackId}")
    public ApiResponse<TrackResponse> updateTrack(@PathVariable("trackId") String trackId, @RequestPart("meta-data") UpdateTrackRequest request, @RequestPart(name = "image", required = false)MultipartFile imageFile, @RequestPart(name = "track", required = false) MultipartFile trackFile ) {
        return ApiResponse.<TrackResponse>builder().data(trackService.updateTrack(trackId, request, imageFile, trackFile)).build();
    }
}
