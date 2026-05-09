package com.MusicPlatForm.user_library_service.controller;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.album.AlbumResponse;
import com.MusicPlatForm.user_library_service.service.iface.AlbumServiceInterface;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/album")
public class AlbumController {
    AlbumServiceInterface  albumService;

    @GetMapping("/bulk")
    public ApiResponse<List<AlbumResponse>> getAllByIds(@RequestParam(name = "album_ids") List<String> ids){
        return  ApiResponse.<List<AlbumResponse>>builder().data(this.albumService.getByIds(ids)).build();
    }

    @GetMapping("/find-by-album-id/{albumId}")
    public ApiResponse<AlbumResponse> getAlbumById(@PathVariable("albumId") String albumId) {
        return  ApiResponse.<AlbumResponse>builder().data(albumService.getAlbumById(albumId)).build();
    }
    @GetMapping("/{albumLink}")
    public ApiResponse<AlbumResponse> getAlbumByAlbumLink(@PathVariable("albumLink") String albumLink) {
        return  ApiResponse.<AlbumResponse>builder().data(albumService.getAlbumByLink(albumLink)).build();
    }
    @GetMapping("/find-by-user-id/{userId}")
    public ApiResponse<List<AlbumResponse>> getAlbums(@PathVariable("userId") String userId){
        return  ApiResponse.<List<AlbumResponse>>builder().data(albumService.getAlbumByUserId(userId)).build();
    }
    @GetMapping("/liked-albums/{userId}")
    public ApiResponse<List<AlbumResponse>> getLikedAlbums(@PathVariable("userId") String userId) {
        return  ApiResponse.<List<AlbumResponse>>builder().data(albumService.getLikedAlbums(userId)).build();
    }

    @GetMapping("/get-liked-created-album/{userId}")
    public ApiResponse<List<AlbumResponse>> getAll(@PathVariable("userId") String userId) {
        return ApiResponse.<List<AlbumResponse>>builder().data(albumService.getCreatedAndLikedAlbum(userId)).build();
    }
    @GetMapping("/count/{albumId}")
    public ApiResponse<Integer> getLikedCount(@PathVariable String albumId){
        return ApiResponse.<Integer>builder().data(albumService.getLikedCount(albumId)).build();
    }
    @GetMapping("/users/{albumId}")
    public ApiResponse<List<String>> getUserLikedAlbum(@PathVariable String albumId){
        return ApiResponse.<List<String>> builder().data(this.albumService.getUserIdsLikedAlbum(albumId)).build();
    }
    // lam cho search

    @GetMapping("/{genre_id}/bulk")
    public ApiResponse<List<AlbumResponse>> getAlbumsByGenre(@PathVariable(name = "genre_id") String genreId){
        return ApiResponse.<List<AlbumResponse>> builder()
                .data(albumService.getAlbumByGenre(genreId))
                .build();
    }

}
