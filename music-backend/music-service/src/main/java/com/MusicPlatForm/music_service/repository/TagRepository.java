package com.MusicPlatForm.music_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MusicPlatForm.music_service.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,String>{
    Optional<Tag> findByName(String name);
    List<Tag> findAllByIsUsedTrue();
}
