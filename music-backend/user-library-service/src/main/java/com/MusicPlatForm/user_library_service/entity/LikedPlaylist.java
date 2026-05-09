package com.MusicPlatForm.user_library_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// -> unique key
@Table(
    name = "Liked_playlist",
    uniqueConstraints = @UniqueConstraint(columnNames = {"User_id", "Playlist_id"})
)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikedPlaylist {
    @Id
    @Column(name="Id")
    String id;

    @Column(name = "User_id",nullable = false)
    String userId;
    
    @Column(name = "Liked_at")
    LocalDateTime likedAt;

    @ManyToOne
    @JoinColumn(name = "Playlist_id",nullable = false)
    Playlist playlist;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString()+userId; // Generate Id;
        }
    }
}
