package com.devteria.profile.mapper;

import com.devteria.profile.dto.request.ProfileCreationRequest;
import com.devteria.profile.dto.request.ProfileUpdateRequest;
import com.devteria.profile.dto.response.ProfileWithCountFollowResponse;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.entity.UserProfile;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
    ProfileWithCountFollowResponse toProfileWithCountFollowResponse(UserProfile userProfile);
    List<UserProfileResponse> toUserProfileResponses(List<UserProfile> userProfiles);
    void updateUserProfile(@MappingTarget UserProfile userProfile, ProfileUpdateRequest request);
}
