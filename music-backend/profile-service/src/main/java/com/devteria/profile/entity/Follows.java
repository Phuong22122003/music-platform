package com.devteria.profile.entity;

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
public class Follows {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    UserProfile follower;
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    UserProfile following;
    LocalDateTime followedAt;
}
