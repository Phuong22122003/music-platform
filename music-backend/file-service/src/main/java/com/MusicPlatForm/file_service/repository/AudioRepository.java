package com.MusicPlatForm.file_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MusicPlatForm.file_service.entity.Audio;

@Repository
public interface AudioRepository extends JpaRepository<Audio,String>{
        public Audio findByFileName(String filename);

}
