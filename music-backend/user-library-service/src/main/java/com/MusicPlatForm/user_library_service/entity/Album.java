package com.MusicPlatForm.user_library_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "Album_title", nullable = false,columnDefinition = "NVARCHAR(255)")
    String albumTitle;

    @Column(name = "Description", nullable = true,columnDefinition = "NVARCHAR(255)")
    String description;

    @Column(name = "Album_link", nullable = false)
    String albumLink;

    @CreationTimestamp
    @Column(name="created_at")
    LocalDateTime createdAt;

    @Column(name = "Privacy", nullable = false)
    String privacy;

    @Column(name = "User_id", nullable = false)
    String userId;

    @Column(name = "Image_path", nullable = true,columnDefinition = "NVARCHAR(255)")
    String imagePath;

    @Column(name = "Album_type", nullable = false)
    String albumType;

    @Column(name = "Genre_id", nullable = true)
    String genreId;

    @Column(name = "Main_artists", nullable = false,columnDefinition = "NVARCHAR(255)")
    String mainArtists;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlbumTrack> tracks;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlbumTag> tags;  // ✅ Corrected from AlbumTrack to AlbumTag

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private  List<LikedAlbum> likedAlbums;
}
