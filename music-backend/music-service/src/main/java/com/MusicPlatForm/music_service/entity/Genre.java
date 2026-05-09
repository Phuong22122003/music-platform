package com.MusicPlatForm.music_service.entity;

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
import lombok.*;
import lombok.experimental.FieldDefaults;

@Table(name = "Genre")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Genre {
    @Id
    @Column(name = "Id")
    String id;
    @Column(name = "Name",columnDefinition = "NVARCHAR(255)",nullable = false)
    String name;
    @Column(name = "Created_at")
    LocalDateTime createdAt;
    @Column(name = "User_id")
    String userId;
    @Column(name = "Is_used")
    boolean isUsed = true;
    @OneToMany(mappedBy = "genre",fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    List<Track> tracks;

    
    @PrePersist
    public void generateId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString() + userId; // Generate Id
        }
    }
}
