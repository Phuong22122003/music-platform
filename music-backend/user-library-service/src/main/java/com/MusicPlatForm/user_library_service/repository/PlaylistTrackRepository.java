package com.MusicPlatForm.user_library_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.MusicPlatForm.user_library_service.entity.PlaylistTrack;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack,String> {
    public void deleteAllPlaylistByPlaylistId(String playlistId);

    // @Query("SELECT P FROM PlaylistTrack WHERE trackId=:trackId AND playlist.id=:playlistID")
    // public PlaylistTrack findByTrackIdAndPlaylistID(String trackId,String playlistId);
    @Query("SELECT p FROM PlaylistTrack p WHERE p.trackId = :trackId AND p.playlist.id = :playlistId")
    PlaylistTrack findByTrackIdAndPlaylistId( String trackId,  String playlistId);

}
