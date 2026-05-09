package com.devteria.profile.dto.response;

import com.devteria.profile.entity.Follows;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class GetFollowersResponse {
    List<Follows> followers;
}
