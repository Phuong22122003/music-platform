package com.devteria.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String userId;
    String firstName;
    String lastName;
    LocalDate dob;
    Boolean gender;
    String email;
    String cover;
    String avatar;
    String displayName;
    LocalDateTime updateAt;
    LocalDateTime createdAt;
    @OneToMany(mappedBy = "follower",targetEntity = Follows.class ,cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Follows> followings;
    @OneToMany(mappedBy = "following",targetEntity = Follows.class ,cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Follows> followers;
}
