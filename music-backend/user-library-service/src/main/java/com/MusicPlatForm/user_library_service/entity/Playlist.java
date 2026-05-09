package com.MusicPlatForm.user_library_service.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(
    name = "Playlist",
    uniqueConstraints = @UniqueConstraint(columnNames = {"Tag_id", "Playlist_id"})
)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Playlist {
    @Id
    @Column(name = "Id")
    String id;

    @Column(name = "Title",nullable = false,columnDefinition = "NVARCHAR(255)")
    String title;

    @Column(name = "Release_date")
    LocalDateTime releaseDate;
    
    @Column(name = "Description",columnDefinition = "NVARCHAR(255)",nullable = true)
    String description;
    
    @Column(name = "Privacy",nullable = false)
    String privacy;

    @Column(name = "User_id",nullable = false)
    String userId;

    @Column(name = "Genre_id")
    String genreId;

    @Column(name = "Image_path",columnDefinition = "NVARCHAR(255)",nullable = true)
    String imagePath;

    @Column(name="created_at")
    LocalDateTime createdAt;
    
    // orphanRemoval = true -> delete automatically when remove playlist track out of list
    @OneToMany(mappedBy = "playlist",fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    List<LikedPlaylist> likedPlaylists;
    
    @OneToMany(mappedBy = "playlist",fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    List<PlaylistTrack> playlistTracks;
    
    @OneToMany(mappedBy = "playlist",fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    List<PlaylistTag> playlistTags;

    

    @PrePersist
    public void generateId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString() + userId; // Generate Id;
        }
    }

    
}
