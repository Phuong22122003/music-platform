package com.MusicPlatForm.user_library_service.repository;

import com.MusicPlatForm.user_library_service.entity.LikedTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LikedTrackRepository extends JpaRepository<LikedTrack,String> {
    public List<LikedTrack> findAllByUserId(String userId);
    public LikedTrack findByUserIdAndTrackId(String userId, String trackId);
    public int countByTrackId(String trackId);
    public List<LikedTrack> findByTrackId(String trackId);

    @Query("FROM LikedTrack WHERE trackId IN :trackIds AND likedAt BETWEEN :fromDate AND :toDate")
    public List<LikedTrack> findLikedTrackFromDateToDateByTrackIds(LocalDateTime fromDate, LocalDateTime toDate, List<String> trackIds);
    
    @Query("FROM LikedTrack WHERE trackId IN :trackIds")
    public List<LikedTrack> findAllLikedTrackByTrackIds(List<String> trackIds);
    
    
    @Query("FROM LikedTrack WHERE likedAt BETWEEN :fromDate AND :toDate")
    public List<LikedTrack> findAllLikedTrackFromDateToDate(LocalDateTime fromDate, LocalDateTime toDate);
    @Query("FROM LikedTrack")
    public List<LikedTrack> findAllLikedTrack();

}
