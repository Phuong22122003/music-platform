package com.MusicPlatForm.user_library_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.playlist.LikedPlaylistResponse;
import com.MusicPlatForm.user_library_service.service.LikedPlaylistService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/playlist/liked")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikedPlaylistController {
    LikedPlaylistService likedPlaylistService;
    @Deprecated
    @GetMapping("/all")
    public ApiResponse<List<LikedPlaylistResponse>> getAllLikedPlaylist(){
        List<LikedPlaylistResponse> likedPlaylistResponses = this.likedPlaylistService.getAllLikedPlaylist();
        return ApiResponse.<List<LikedPlaylistResponse>>builder().data(likedPlaylistResponses).code(200).build();
    }
    @GetMapping("/is_liked/{playlistId}")
    public ApiResponse<Boolean> isLiked(@PathVariable String playlistId){
        return ApiResponse.<Boolean>builder().data(likedPlaylistService.isLiked(playlistId)).code(200).build();
    }
    
    @PostMapping("/like/{playlistId}")
    public ApiResponse<Boolean> likePlaylist(@PathVariable String playlistId){
        return ApiResponse.<Boolean>builder().data(likedPlaylistService.likePlaylist(playlistId)).code(200).build();
    }
    @PostMapping("/unlike/{playlistId}")
    public ApiResponse<Boolean> unLikePlaylist(@PathVariable(name = "playlistId") String playlistId){
        return ApiResponse.<Boolean>builder().data(likedPlaylistService.unLikePlaylist(playlistId)).code(200).build();
    }

    @GetMapping("/count/{playlistId}")
    public ApiResponse<Integer> getLikedCount(@PathVariable String playlistId){
        return ApiResponse.<Integer>builder().data(likedPlaylistService.getLikedCount(playlistId)).code(200).build();
        
    }

    @GetMapping("/users/{playlistId}")
    public ApiResponse<List<String>> getUserIdsLikedPlaylist(@PathVariable String playlistId){
        return ApiResponse.<List<String>>builder().data(this.likedPlaylistService.getUserIdsLikedPlaylist(playlistId)).code(200).build();
    }
}
