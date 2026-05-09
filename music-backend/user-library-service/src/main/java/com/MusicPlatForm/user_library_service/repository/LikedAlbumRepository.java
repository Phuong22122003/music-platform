package com.MusicPlatForm.user_library_service.repository;

import com.MusicPlatForm.user_library_service.entity.LikedAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikedAlbumRepository extends JpaRepository<LikedAlbum, String> {
    boolean existsByUserIdAndAlbumId(String userId, String albumId);
    List<LikedAlbum> findByUserId(String userId);
    List<LikedAlbum> findByAlbumId(String albumId);
    List<LikedAlbum> findByUserIdOrderByLikedAtDesc(String userId);
    Optional<LikedAlbum> findByUserIdAndAlbumId(String userId, String albumId);
    Integer countByAlbumId(String albumId);
    
}
