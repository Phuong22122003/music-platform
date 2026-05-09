package com.MusicPlatForm.notification_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.MusicPlatForm.notification_service.entity.Token;

public interface TokenRepository extends MongoRepository<Token,String> {
    Token findByUserId(String userId);
}
