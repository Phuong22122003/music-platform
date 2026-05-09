package com.MusicPlatForm.comment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "track_id",nullable = false)
    String trackId;

    @Column(name = "user_id",nullable = false)
    String userId;

    @Column(name = "replied_user_id")
    String repliedUserId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    String content;

    @Column(name = "comment_at",nullable = false)
    LocalDateTime commentAt;

    @Column(nullable = false)
    int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies;

    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL)
    List<LikedComment> likedComments;
}
