package com.MusicPlatForm.music_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.MusicPlatForm.music_service.entity.Track;

import feign.Param;

public interface TrackRepository extends JpaRepository<Track,String>{
    @Query(value = "SELECT TOP(:limit) * FROM Track WHERE Genre_id = :genreId ORDER BY NEWID()", nativeQuery = true)
    List<Track> findRandomTracksByGenre(@Param("genreId") String genreId, @Param("limit") int limit);
    @Query(value = "SELECT TOP(:limit) * FROM Track ORDER BY NEWID()", nativeQuery = true)
    List<Track> findRandomTracks(@Param("limit") int limit);

    List<Track> findTrackByUserId(String userId);
    
}
