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

@Table(
    name = "Playlist_tracks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"Track_id", "Playlist_id"})
)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrack {
    @Id
    @Column(name = "Id")
    String id;

    @Column(name = "Track_id",nullable = false)
    String trackId;
    
    @Column(name = "Added_at")
    LocalDateTime addedAt;
    
    @ManyToOne
    @JoinColumn(name = "Playlist_id")
    Playlist playlist;
    
    @PrePersist
    public void generateId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString(); // Generate Id;
        }
    }
}
