package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.ProfileCreationRequest;
import com.example.identity_service.dto.request.ProfileUpdateRequest;
import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.request.UserUpdateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
    ProfileUpdateRequest toProfileUpdateRequest(UserUpdateRequest request);
}
