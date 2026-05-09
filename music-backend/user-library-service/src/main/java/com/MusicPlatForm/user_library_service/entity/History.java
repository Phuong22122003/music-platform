package com.MusicPlatForm.user_library_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Table(
    name = "History"
    // uniqueConstraints = @UniqueConstraint(columnNames = {"User_id", "Track_id"})
)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class History {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "Track_id",nullable = false)
    String trackId;
    
    @Column(name = "User_id",nullable = false)
    String userId;

    @Column(name="Listened_at",nullable = false)
    LocalDateTime listenedAt;
    
    @Column(name = "Count",nullable = false)
    Integer count;
}
