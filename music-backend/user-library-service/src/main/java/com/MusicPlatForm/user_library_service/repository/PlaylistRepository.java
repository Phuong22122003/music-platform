package com.MusicPlatForm.user_library_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.MusicPlatForm.user_library_service.entity.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist,String>{
    @Deprecated
    @Query("FROM Playlist WHERE userId=:userId")
    public List<Playlist> getPlaylists(String userId);
    
    @Query("FROM Playlist WHERE userId=:userId")
    public List<Playlist> getPlaylistsByUserId(String userId);

    @Query("FROM Playlist WHERE userId=:userId AND privacy='public'")
    public List<Playlist> getPublicPlaylistsByUserId(String userId);
    
    
    @Query("FROM Playlist WHERE genreId=:genreId AND privacy='public'")
    public List<Playlist> getPlaylistByGenreId(String genreId);
}
