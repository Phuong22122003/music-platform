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
public class AlbumTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "Track_id", nullable = false)
    String trackId;

    @Column(name = "Added_at", nullable = false)
    LocalDateTime addedAt;

    @ManyToOne
    @JoinColumn(name = "Album_id", nullable = false)
    private Album album;
}
