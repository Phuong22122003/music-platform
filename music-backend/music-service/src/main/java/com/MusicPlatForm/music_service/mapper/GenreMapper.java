package com.MusicPlatForm.music_service.mapper;

import com.MusicPlatForm.music_service.dto.reponse.TagResponse;
import com.MusicPlatForm.music_service.entity.Tag;
import org.mapstruct.Mapper;

import com.MusicPlatForm.music_service.dto.reponse.GenreResponse;
import com.MusicPlatForm.music_service.entity.Genre;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponse toGenreResponseFromGenre(Genre genre);
    List<GenreResponse> toGenreResponsesFromGenres(List<Genre> genres);
}
