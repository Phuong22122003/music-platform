package com.MusicPlatForm.comment_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "liked_comments")
public class LikedComment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "user_id",nullable = false)
    String userId;

    @Column(nullable = false)
    LocalDateTime likeAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "comment_id",  nullable = false)
    Comment comment;
}