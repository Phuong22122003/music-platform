package com.MusicPlatForm.music_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MusicPlatForm.music_service.entity.TrackTag;

public interface TrackTagRepository extends JpaRepository<TrackTag,String>{
    
}
