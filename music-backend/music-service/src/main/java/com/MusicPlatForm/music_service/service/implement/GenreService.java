package com.MusicPlatForm.music_service.service.implement;

import com.MusicPlatForm.music_service.dto.reponse.GenreResponse;
import com.MusicPlatForm.music_service.dto.request.GenreRequest;
import com.MusicPlatForm.music_service.entity.Genre;
import com.MusicPlatForm.music_service.exception.AppException;
import com.MusicPlatForm.music_service.exception.ErrorCode;
import com.MusicPlatForm.music_service.mapper.GenreMapper;
import com.MusicPlatForm.music_service.repository.GenreRepository;
import com.MusicPlatForm.music_service.service.iface.GenreServiceInterface;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GenreService implements GenreServiceInterface {
    GenreRepository genreRepository;
    GenreMapper genreMapper;
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse createGenre(GenreRequest request) {
        if(genreRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.GENRE_EXISTED);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Genre genre = Genre.builder().createdAt(LocalDateTime.now()).name(request.getName()).userId(userId).isUsed(true).build();
        Genre newGenre = genreRepository.save(genre);
        return genreMapper.toGenreResponseFromGenre(newGenre);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public String deleteGenre(String id) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
        genre.setUsed(false);
        genreRepository.save(genre);
        return "Delete successfully";
    }
    @Override
    public List<GenreResponse> getGenres() {
        List<Genre> genres = genreRepository.findAllByIsUsedTrue();
        return  genreMapper.toGenreResponsesFromGenres(genres);
    }
    @Override
    public List<GenreResponse> getGenresByIds(List<String>ids) {
        List<Genre> genres = genreRepository.findAllById(ids);
        return  genreMapper.toGenreResponsesFromGenres(genres);
    }
    @Override
    public GenreResponse getGenreById(String id) {
        Genre genres = genreRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.GENRE_NOT_FOUND));
        return  genreMapper.toGenreResponseFromGenre(genres);
    }
    @Override
    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return  genreMapper.toGenreResponsesFromGenres(genres);
    }
}
