package com.MusicPlatForm.user_library_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.MusicPlatForm.user_library_service.entity.LikedPlaylist;

@Repository
public interface LikedPlaylistRepository extends JpaRepository<LikedPlaylist,String> {
    public List<LikedPlaylist> findAllByUserId(String userId);

    @Query("From LikedPlaylist where userId=:userId and playlist.id =:playlistId")
    public LikedPlaylist findByUserIdAndPlaylistId(String userId,String playlistId);

    @Query("From LikedPlaylist where playlist.id =:playlistId")
    public List<LikedPlaylist> findByPlaylistId(String playlistId);

    // @Query("SELECT COUNT(lp) FROM LikedPlaylist lp WHERE lp.playlist.id = :playlistId")
    Integer countByPlaylistId(String playlistId);

}
