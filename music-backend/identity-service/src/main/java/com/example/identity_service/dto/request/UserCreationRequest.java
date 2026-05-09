package com.example.identity_service.dto.request;

import com.example.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserCreationRequest {
    @Size(min = 3, message = "USERNAME_MIN")
    String username;

    @Size(min = 8, message = "PASSWORD_MIN")
    String password;

    @Email(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_REQUIRED")
    String email;

    String firstName;
    String lastName;
    String gender;
    @DobConstraint(min = 16)
    LocalDate dob;
    Set<String> roles;
    @NotBlank(message = "DISPLAY_NAME_REQUIRED")
    String displayName;
    String avatar;
    String provider;
}
