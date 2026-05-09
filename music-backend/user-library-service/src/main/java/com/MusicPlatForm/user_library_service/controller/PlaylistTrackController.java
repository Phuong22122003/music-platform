package com.MusicPlatForm.user_library_service.controller;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddPlaylistTrackRequest;
import com.MusicPlatForm.user_library_service.dto.response.playlist.PlaylistTrackResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.service.PlaylistTrackService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/playlist/tracks")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class PlaylistTrackController {
    PlaylistTrackService playlistTrackService;
    //done
    @Deprecated
    @PostMapping("/add")
    public ApiResponse<PlaylistTrackResponse> addTrackToPlaylist(@Valid @RequestBody AddPlaylistTrackRequest trackRequest){
        PlaylistTrackResponse playlistTrackResponse = this.playlistTrackService.addTrackToPlaylist(trackRequest);
        return ApiResponse.<PlaylistTrackResponse>builder()
                .code(HttpStatus.OK.value())
                .data(playlistTrackResponse).build();
    }
    @PostMapping("")
    public ApiResponse<PlaylistTrackResponse> addTrackToPlaylistV1(@Valid @RequestBody AddPlaylistTrackRequest trackRequest){
        PlaylistTrackResponse playlistTrackResponse = this.playlistTrackService.addTrackToPlaylist(trackRequest);
        return ApiResponse.<PlaylistTrackResponse>builder()
                .code(HttpStatus.OK.value())
                .data(playlistTrackResponse).build();
    }
    //done
    @Deprecated
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteTrackFromPlaylist(@Valid @RequestBody AddPlaylistTrackRequest trackRequest){
        this.playlistTrackService.deleteTrackFromPlaylist(trackRequest);
        return ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .data("Deleted successfully").build();
    }
    @DeleteMapping("")
    public ApiResponse<String> deleteTrackFromPlaylistV1(@Valid @RequestBody AddPlaylistTrackRequest trackRequest){
        this.playlistTrackService.deleteTrackFromPlaylist(trackRequest);
        return ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .data("Deleted successfully").build();
    }
}
