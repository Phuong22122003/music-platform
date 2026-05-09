package com.MusicPlatForm.user_library_service.service.iface;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddTrackAlbumRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.AlbumRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.client.TrackRequest;
import com.MusicPlatForm.user_library_service.dto.response.album.AlbumResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;


@Service
public interface AlbumServiceInterface {

    @Transactional
    public AlbumResponse addAlbum(AlbumRequest request, MultipartFile coverAlbum, List<MultipartFile> trackFiles, List<TrackRequest> trackRequests) throws JsonProcessingException;

    @PostAuthorize("returnObject.privacy == 'public' or returnObject.userId == authentication.name")
    public AlbumResponse getAlbumById(String albumId);

    @PostAuthorize("returnObject.privacy == 'public' or returnObject.userId == authentication.name")
    public AlbumResponse getAlbumByLink(String albumLink);

    public List<AlbumResponse> getAlbumByUserId(String userId);

    public List<AlbumResponse> getByIds(List<String> ids) ;

    @Transactional
    public void deleteAlbumById(String albumId);

    @Transactional
    public AlbumResponse editAlbum(AlbumRequest request, MultipartFile file, String albumId);

    public void likeAlbum(String albumId);

    public List<AlbumResponse> getLikedAlbums(String userId) ;

    public void unLikeAlbum(String albumId);

    public List<AlbumResponse> getCreatedAndLikedAlbum(String userId);

    public void addTrackToAlbum(AddTrackAlbumRequest request) ;

    public Integer getLikedCount(String albumId);

    public List<String> getUserIdsLikedAlbum(String albumId);

    public List<AlbumResponse> getAlbumByGenre(String genreId);
}
