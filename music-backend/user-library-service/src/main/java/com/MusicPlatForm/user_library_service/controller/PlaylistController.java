package com.MusicPlatForm.user_library_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddPlaylistRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.UpdatePlaylistInfoRequest;
import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.playlist.PlaylistResponse;
import com.MusicPlatForm.user_library_service.service.iface.PlaylistServiceInterface;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/playlist")
@AllArgsConstructor
public class PlaylistController {
    private PlaylistServiceInterface playlistService;

    //Done
    @GetMapping("/you/all")
    public ApiResponse<List<PlaylistResponse>> getAllPlaylist(){
        return playlistService.getPlaylists("ALL");
    }
    @GetMapping("/you/liked")
    public ApiResponse<List<PlaylistResponse>> getLikedPlaylist(){
        return playlistService.getPlaylists("LIKED");
    }
    @GetMapping("/you/created")
    public ApiResponse<List<PlaylistResponse>> getCreatedPlaylist(){
        return playlistService.getPlaylists("CREATED");
    }

    @GetMapping("/users/{user_id}")
    public ApiResponse<List<PlaylistResponse>> getPlaylist(@PathVariable(name = "user_id") String userId){
        return playlistService.getPlaylistsByUserId(userId);
    }
    
    //Done
    @GetMapping("/{id}")
    public ApiResponse<PlaylistResponse> getPlaylistById(@PathVariable String id){
        return ApiResponse.<PlaylistResponse>builder().data(this.playlistService.getPlaylistById(id)).build();
    }

    @GetMapping("/bulk")
    public ApiResponse<List<PlaylistResponse>> getPlaylistsByIds(@RequestParam(name = "playlist_ids") List<String> ids){
        return this.playlistService.getPlaylistsByIds(ids);
    }
    //Done
    @PostMapping()
    public ApiResponse<PlaylistResponse> savePlaylist(@Valid @RequestBody AddPlaylistRequest request){
        PlaylistResponse playlistResponse = playlistService.addPlaylist(request);
        return ApiResponse.<PlaylistResponse>builder()
                .code(200)
                .data(playlistResponse)
                .build();
    }

    //done
    @Deprecated
    @PutMapping("/update")
    public ApiResponse<PlaylistResponse> updatePlaylistInfo(@RequestPart(name = "image",required = false) MultipartFile image, @RequestPart(name = "playlist") UpdatePlaylistInfoRequest updatePlaylistInfoRequest){
        PlaylistResponse playlistResponse =  playlistService.updatePlaylistInfo(image, updatePlaylistInfoRequest);
        return ApiResponse.<PlaylistResponse>builder()
                .code(200)
                .data(playlistResponse)
                .build();
    }

    @PutMapping()
    public ApiResponse<PlaylistResponse> updatePlaylistInfoV1(@RequestPart(name = "image",required = false) MultipartFile image, @RequestPart(name = "playlist") UpdatePlaylistInfoRequest updatePlaylistInfoRequest){
        PlaylistResponse playlistResponse =  playlistService.updatePlaylistInfo(image, updatePlaylistInfoRequest);
        return ApiResponse.<PlaylistResponse>builder()
                .code(200)
                .data(playlistResponse)
                .build();
    }

    //done
    @Deprecated
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteTrackById(@PathVariable String id){
        playlistService.deletePlaylistById(id);
        return ApiResponse.<String>builder()
                .code(200)
                .data("Delete successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTrackByIdV1(@PathVariable String id){
        playlistService.deletePlaylistById(id);
        return ApiResponse.<String>builder()
                .code(200)
                .data("Delete successfully")
                .build();
    }

    // cho tim kiem
    @GetMapping("/{genre_id}/bulk")
    public ApiResponse<List<PlaylistResponse>> getPlaylistsByGenre(@PathVariable(name = "genre_id") String genreId){
       
        return ApiResponse.<List<PlaylistResponse>> builder()
                .data(playlistService.getPlaylistsByGenre(genreId))
                .build();
        
    }
}
