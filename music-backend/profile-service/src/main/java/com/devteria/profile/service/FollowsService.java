package com.devteria.profile.service;

import com.devteria.profile.dto.request.AddFollowRequest;
import com.devteria.profile.dto.response.ProfileWithCountFollowResponse;
import com.devteria.profile.dto.response.UserProfileResponse;
import com.devteria.profile.entity.Follows;
import com.devteria.profile.entity.UserProfile;
import com.devteria.profile.exception.AppException;
import com.devteria.profile.exception.ErrorCode;
import com.devteria.profile.mapper.UserProfileMapper;
import com.devteria.profile.repository.FollowsRepository;
import com.devteria.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowsService {
    FollowsRepository followsRepository;
    UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Transactional
    public void addFollower(AddFollowRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserProfile follower = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        UserProfile following = userProfileRepository.findByUserId(request.getFollowingId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        if(request.getFollowingId().equals(userId)){
                throw new AppException(ErrorCode.ALREADY_FOLLOWING_YOURSELF);
        }

        if (followsRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            throw new AppException(ErrorCode.ALREADY_FOLLOWING);
        }

        Follows follows = Follows.builder()
                .follower(follower)
                .following(following)
                .followedAt(LocalDateTime.now())
                .build();

        followsRepository.save(follows);
    }
    public Page<ProfileWithCountFollowResponse> getFollowers(int page, int size, String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size, Sort.by("followedAt").descending());

        Page<Follows> followers = followsRepository.findByFollowingId(pageable, userProfile.getId());
        Page<UserProfile> userProfileList = followers
                .map(Follows::getFollower);
        return userProfileList.map(profile -> {
            ProfileWithCountFollowResponse response = userProfileMapper.toProfileWithCountFollowResponse(profile);
            int followingCount = followsRepository.countByFollowing_UserId(userId);
            int followerCount = followsRepository.countByFollower_UserId(userId);
            response.setFollowingCount(followingCount);
            response.setFollowerCount(followerCount);
            response.setFollowing(isFollowing(userId, response.getUserId()));
            return response;
        });
    }
    public Page<ProfileWithCountFollowResponse> getFollowing(int page, int size, String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size, Sort.by("followedAt").descending());
        Page<Follows> followings = followsRepository.findByFollowerId(pageable, userProfile.getId());
        Page<UserProfile> userProfileList = followings
                .map(Follows::getFollowing);
        return userProfileList.map(profile -> {
            ProfileWithCountFollowResponse response = userProfileMapper.toProfileWithCountFollowResponse(profile);
            int followingCount = followsRepository.countByFollowing_UserId(userId);
            int followerCount = followsRepository.countByFollower_UserId(userId);
            response.setFollowingCount(followingCount);
            response.setFollowerCount(followerCount);
            response.setFollowing(true);
            return response;
        });
    }
    @Transactional
    public void unfollow(String unfollowUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        UserProfile unfollowUserProfile = userProfileRepository.findByUserId(unfollowUserId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        followsRepository.findByFollowerAndFollowing(userProfile, unfollowUserProfile)
                .ifPresent(followsRepository::delete);
    }

    public boolean isFollowing(String followerId, String followingId) {
        return followsRepository.existsByFollower_UserIdAndFollowing_UserId(followerId, followingId);
    }

}
