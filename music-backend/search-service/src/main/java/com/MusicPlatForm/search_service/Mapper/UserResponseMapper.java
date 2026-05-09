package com.MusicPlatForm.search_service.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.MusicPlatForm.search_service.Dto.Request.UserRequest;
import com.MusicPlatForm.search_service.Entity.User;
@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    // Map one User to userId
    default String mapToUserId(User user) {
        return user.getUserId();
    }

    // Map list of User to list of userId (String)
    List<String> toUserIds(List<User> users);


}
