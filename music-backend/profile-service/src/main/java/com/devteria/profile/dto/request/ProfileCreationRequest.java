package com.devteria.profile.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProfileCreationRequest {
    String userId;
    String firstName;
    String lastName;
    String displayName;
    String email;
    LocalDate dob;
    Boolean gender;
    String profilePicture;
    LocalDateTime createdAt;
    LocalDateTime updateAt;
    String avatar;
}
