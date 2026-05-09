package com.MusicPlatForm.user_library_service.controller;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddTrackAlbumRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.AlbumRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.client.TrackRequest;
import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.album.AlbumResponse;
import com.MusicPlatForm.user_library_service.service.iface.AlbumServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth/album")
public class AuthAlbumController {
    AlbumServiceInterface  albumService;

    @PostMapping(path = "/add-album", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<AlbumResponse> addAlbum(@Valid @RequestPart("meta-data") AlbumRequest request,
                                               @RequestPart(value = "cover-album", required = false) MultipartFile coverAlbum,
                                               @RequestPart(value = "track-request") List<TrackRequest> trackRequests,
                                               @RequestPart(value = "track-files") List<MultipartFile> trackFiles
                                               ) throws JsonProcessingException {
        return  ApiResponse.<AlbumResponse>builder().data(albumService.addAlbum(request, coverAlbum, trackFiles, trackRequests)).build();
    }

    @DeleteMapping(path = "/{albumId}")
    public ApiResponse<String> deleteAlbum(@PathVariable("albumId") String albumId) {
        albumService.deleteAlbumById(albumId);
        return  ApiResponse.<String>builder().data("Delete album successfully!").build();
    }

    @PutMapping(path = "/{albumId}")
    public ApiResponse<AlbumResponse> updateAlbum(@PathVariable("albumId") String albumId,
                                                  @Valid @RequestPart("meta-data") AlbumRequest request,
                                                  @RequestPart(value = "cover-album", required = false) MultipartFile coverAlbum) {
        return ApiResponse.<AlbumResponse>builder().data(albumService.editAlbum(request, coverAlbum, albumId)).build();
    }

    @PostMapping("/like/{albumId}")
    public ApiResponse<Void> likeAlbum(@PathVariable("albumId") String albumId) {
        albumService.likeAlbum(albumId);
        return  ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/unlike/{albumId}")
    public ApiResponse<Void> unLikeAlbum(@PathVariable("albumId") String albumId)
    {
        albumService.unLikeAlbum(albumId);
        return  ApiResponse.<Void>builder().build();
    }

    @PostMapping("/add-track")
    public ApiResponse<?> addTrack(@RequestBody AddTrackAlbumRequest request) {
        albumService.addTrackToAlbum(request);
        return ApiResponse.builder().message("Add track to album successfully").build();
    }
}
