package com.MusicPlatForm.user_library_service.repository;

import com.MusicPlatForm.user_library_service.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, String> {
    Page<Album> findByUserId(Pageable pageable, String userid);
    List<Album> findByUserId(String userid);
    List<Album> findByUserIdAndPrivacy(String userid, String privacy);
    @Query("""
        SELECT a FROM Album a 
        LEFT JOIN LikedAlbum la ON la.album.id = a.id AND la.userId = :userId
        WHERE a.userId = :userId OR la.userId = :userId
        ORDER BY COALESCE(la.likedAt, a.createdAt) DESC
    """)
    List<Album> findCreatedAndLikedAlbums(String userId);
    Optional<Album> findByAlbumLink(String albumLink);

    @Query("From Album where genreId=:genreId and privacy='public'")
    List<Album> findAlbumByGenreId(String genreId);
}
