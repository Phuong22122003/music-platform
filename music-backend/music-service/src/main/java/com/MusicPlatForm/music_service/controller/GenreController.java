package com.MusicPlatForm.music_service.controller;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.GenreResponse;
import com.MusicPlatForm.music_service.service.implement.GenreService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreController {
    GenreService genreService;
    @GetMapping()
    public ApiResponse<List<GenreResponse>> getGenres() {
        return ApiResponse.<List<GenreResponse>>builder().data(genreService.getGenres()).build();
    }
    @GetMapping("/all")
    public ApiResponse<List<GenreResponse>> getAllGenres() {
        return ApiResponse.<List<GenreResponse>>builder().data(genreService.getAllGenres()).build();
    }
    
    @GetMapping("/bulk")
    public ApiResponse<List<GenreResponse>> getGenresByIds(@RequestParam List<String> ids) {
        return ApiResponse.<List<GenreResponse>>builder().data(genreService.getGenresByIds(ids)).build();
    }
    @GetMapping("/{id}")
    public ApiResponse<GenreResponse> getGenreById(@PathVariable String id) {
        return ApiResponse.<GenreResponse>builder().data(genreService.getGenreById(id)).build();
    }
}
