package com.MusicPlatForm.user_library_service.repository;

import com.MusicPlatForm.user_library_service.entity.AlbumTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumTrackRepository extends JpaRepository<AlbumTrack, String> {

}
