package com.MusicPlatForm.user_library_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LikedTrack {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(name="Track_id", nullable = false)
    String trackId;
    @Column(name="User_id",nullable = false)
    String userId;
    @Column(name="Liked_at",nullable = false)
    LocalDateTime likedAt;
}
