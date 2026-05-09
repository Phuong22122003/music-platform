package com.MusicPlatForm.music_service.service.iface;

import com.MusicPlatForm.music_service.dto.reponse.GenreResponse;
import com.MusicPlatForm.music_service.dto.request.GenreRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface GenreServiceInterface {
    /**
     * Create tag
     * @param request
     */
    public GenreResponse createGenre(GenreRequest request);
    public String deleteGenre(String id);
    public List<GenreResponse> getGenres();
    public List<GenreResponse> getGenresByIds(List<String>ids);
    public GenreResponse getGenreById(String id);
    public List<GenreResponse> getAllGenres();
}
