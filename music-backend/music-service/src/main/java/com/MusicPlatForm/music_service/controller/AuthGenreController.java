package com.MusicPlatForm.music_service.controller;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.GenreResponse;
import com.MusicPlatForm.music_service.dto.request.GenreRequest;
import com.MusicPlatForm.music_service.service.implement.GenreService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/genres")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthGenreController {
    GenreService genreService;
    @PostMapping()
    public ApiResponse<GenreResponse> createGenre(@RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder().data(genreService.createGenre(request)).build();
    }
    @DeleteMapping("/{genreId}")
    public ApiResponse<String> getGenre(@PathVariable("genreId") String genreId) {
        return ApiResponse.<String>builder().data(genreService.deleteGenre(genreId)).build();
    }
}
