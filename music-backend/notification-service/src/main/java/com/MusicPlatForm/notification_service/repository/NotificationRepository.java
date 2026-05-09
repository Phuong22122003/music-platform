package com.MusicPlatForm.notification_service.repository;

import com.MusicPlatForm.notification_service.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification,String> {
    List<Notification> findByRecipientIdOrderByCreatedAtAsc(String recipientId);

    List<Notification> findAllByRecipientId(String recipientId);

    List<Notification> findAllByIdOrderByCreatedAtDesc(List<String> ids);
}
