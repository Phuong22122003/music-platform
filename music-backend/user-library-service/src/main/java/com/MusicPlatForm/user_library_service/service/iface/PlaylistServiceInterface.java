package com.MusicPlatForm.user_library_service.service.iface;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddPlaylistRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.UpdatePlaylistInfoRequest;
import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.playlist.PlaylistResponse;


@Service
public interface PlaylistServiceInterface {

    public ApiResponse<List<PlaylistResponse>> getPlaylistsByIds(List<String> ids) ;

    public ApiResponse<List<PlaylistResponse>> getPlaylistsByUserId(String userId) ;

    public ApiResponse<List<PlaylistResponse>> getPlaylists(String option);

    public List<PlaylistResponse> getPlaylistsByGenre(String genreId);

    //done
    public PlaylistResponse getPlaylistById(String id);

    //done
    @Transactional
    public PlaylistResponse addPlaylist(AddPlaylistRequest request);
    //done
    @Transactional
    public PlaylistResponse updatePlaylistInfo(MultipartFile playlistCoverImage,UpdatePlaylistInfoRequest request);

    @Transactional
    public void deletePlaylistById(String trackId);
}
