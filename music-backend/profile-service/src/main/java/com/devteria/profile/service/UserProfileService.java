package com.devteria.profile.service;

import com.devteria.profile.dto.request.ApiResponse;
import com.devteria.profile.dto.request.ProfileCreationRequest;
import com.devteria.profile.dto.request.ProfileUpdateRequest;
import com.devteria.profile.dto.response.ProfileWithCountFollowResponse;
import com.devteria.profile.dto.response.UploadAvatarResponse;
import com.devteria.profile.dto.response.UploadCoverResponse;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.entity.UserProfile;
import com.devteria.profile.exception.AppException;
import com.devteria.profile.exception.ErrorCode;
import com.devteria.profile.mapper.UserProfileMapper;
import com.devteria.profile.repository.FileClient;
import com.devteria.profile.repository.FollowsRepository;
import com.devteria.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    FileClient fileClient;
    FollowsRepository followsRepository;
    KafkaService kafkaService;
    @Transactional
    public UserProfileResponse create(ProfileCreationRequest request) {
        String email = request.getEmail();
        if (userProfileRepository.findByEmail(email).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        UserProfile userProfile =  userProfileRepository.save(userProfileMapper.toUserProfile(request));
        kafkaService.addUserToSearchService(userProfile);
        return  userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse update(ProfileUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = "";
        if (authentication != null) {
            id = authentication.getName();
        }
        UserProfile userProfile = userProfileRepository.findByUserId(id).orElseThrow(() ->new AppException(ErrorCode.PROFILE_NOT_FOUND));
        userProfileMapper.updateUserProfile(userProfile, request);
        userProfile.setUpdateAt(LocalDateTime.now());
        UserProfile updatedUserProfile =  userProfileRepository.save(userProfile);
        kafkaService.updateUserToSearchService(updatedUserProfile);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    @Transactional
    public List<ProfileWithCountFollowResponse> getByIds(List<String> userIds){
        List<UserProfile> userProfiles = userProfileRepository.findAllByUserIdIn(userIds);
        List<ProfileWithCountFollowResponse> userProfileResponses = new ArrayList<>();
        for(var userProfile: userProfiles){
            ProfileWithCountFollowResponse response = userProfileMapper.toProfileWithCountFollowResponse(userProfile);
            int followingCount = followsRepository.countByFollower_UserId(userProfile.getUserId());
            int followerCount = followsRepository.countByFollowing_UserId(userProfile.getUserId());
    
            response.setFollowingCount(followingCount);
            response.setFollowerCount(followerCount);
            userProfileResponses.add(response);
        }
        return userProfileResponses;
    }
    public List<UserProfileResponse> getBasicUserInfoByIds(List<String> ids){
        List<UserProfile> userProfiles = userProfileRepository.findAllByUserIdIn(ids);
        return userProfileMapper.toUserProfileResponses(userProfiles);
    }
    
    public ProfileWithCountFollowResponse get(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        ProfileWithCountFollowResponse response = userProfileMapper.toProfileWithCountFollowResponse(userProfile);

        int followingCount = followsRepository.countByFollower_UserId(userId);
        int followerCount = followsRepository.countByFollowing_UserId(userId);
        boolean isFollowing = false;
        if(authentication.isAuthenticated()){
            isFollowing = followsRepository.existsByFollower_UserIdAndFollowing_UserId(authentication.getName(), userId);

        }
        response.setFollowingCount(followingCount);
        response.setFollowerCount(followerCount);
        response.setFollowing(isFollowing);

        return response;
    }

    public UserProfileResponse getByEmail(String email) {
        UserProfile userProfile = userProfileRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("UserProfile is not exist"));
        return userProfileMapper.toUserProfileResponse(userProfile);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserProfileResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfile> userProfiles = userProfileRepository.findAll(pageable);
        return userProfiles.map(userProfileMapper::toUserProfileResponse);
    }
    public UserProfileResponse getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return userProfileMapper.toUserProfileResponse(userProfile);
    }
    public ApiResponse<UploadAvatarResponse> uploadAvatar(MultipartFile avatar) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        String oldAvatar = userProfile.getAvatar();
        ApiResponse<UploadAvatarResponse> response;
        if (oldAvatar != null && !oldAvatar.isEmpty() && !oldAvatar.startsWith("https")) {
            response = fileClient.replaceAvatar(avatar, oldAvatar);
        }
        else {
            response = fileClient.addAvatar(avatar);
        }
        userProfile.setAvatar(response.getData().getAvatarName());
        userProfileRepository.save(userProfile);
        return response;
    }

    public ApiResponse<UploadCoverResponse> uploadCover(MultipartFile cover) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        String oldCover = userProfile.getCover();
        ApiResponse<UploadCoverResponse> response;
        if (StringUtils.hasText(oldCover)) {
            response = fileClient.replaceCover(cover, oldCover);
        } else {
            response = fileClient.addCover(cover);
        }
        userProfile.setCover(response.getData().getCoverName());
        userProfileRepository.save(userProfile);
        return response;
    }

    public List<ProfileWithCountFollowResponse> getTopFollowedUser(int limit){
        Pageable pageable = PageRequest.of(0, limit);
        List<UserProfile> userProfiles = userProfileRepository.findTopFollowedUsers(pageable);
        List<ProfileWithCountFollowResponse> userProfileResponses = new ArrayList<>();
        for(var userProfile: userProfiles){
            ProfileWithCountFollowResponse response = userProfileMapper.toProfileWithCountFollowResponse(userProfile);
            int followingCount = followsRepository.countByFollower_UserId(userProfile.getUserId());
            int followerCount = followsRepository.countByFollowing_UserId(userProfile.getUserId());
    
            response.setFollowingCount(followingCount);
            response.setFollowerCount(followerCount);
            userProfileResponses.add(response);
        }
        return userProfileResponses;
    }
}
