package com.MusicPlatForm.user_library_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LikedAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "User_id",nullable = false)
    String userId;

    @Column(name = "Liked_at")
    LocalDateTime likedAt;

    @ManyToOne
    @JoinColumn(name = "Album_id", nullable = false)
    Album album;
}
