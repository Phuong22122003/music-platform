package com.MusicPlatForm.file_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MusicPlatForm.file_service.entity.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar,String>{
    public Optional<Avatar> findByFileName(String filename);
}
