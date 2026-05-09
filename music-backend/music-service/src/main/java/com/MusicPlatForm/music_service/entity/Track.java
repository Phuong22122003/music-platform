package com.MusicPlatForm.music_service.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Table(name = "Track")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Track {
    @Id
    @Column(name = "Id")
    String id;

    @Column(name = "Title", nullable = false, columnDefinition = "NVARCHAR(255)")
    String title;

    @Column(name = "Description", columnDefinition = "NVARCHAR(255)")
    String description;

    @Column(name = "File_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    String fileName;

    @Column(name = "Cover_image_name",nullable = true, columnDefinition = "NVARCHAR(255)")
    String coverImageName;

    @Column(name = "Created_at", nullable = false)
    LocalDateTime createdAt;


    @Column(name = "User_id", nullable = false)
    String userId;

    @Column(name = "Duration",nullable = false)
    String duration;

    @Column(name = "Privacy",nullable = false)
    String privacy;

    @Column(name = "Play",nullable = false)
    int countPlay;

    @ManyToOne
    @JoinColumn(name = "Genre_id")
    Genre genre;

    @OneToMany(mappedBy = "track",fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    List<TrackTag> trackTags;
    

    @PrePersist
    public void generateId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString() + userId; // Generate Id
        }
    }
}
