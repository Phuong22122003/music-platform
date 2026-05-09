package com.devteria.profile.repository;

import com.devteria.profile.entity.Follows;
import com.devteria.profile.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowsRepository extends JpaRepository<Follows, String> {
    Optional<Follows> findByFollowerAndFollowing(UserProfile follower, UserProfile following);
    boolean existsByFollower_UserIdAndFollowing_UserId(String followerId, String followingId);
    List<Follows> findByFollowerId(String followerId);
    List<Follows> findByFollowingId(String followingId);
    Page<Follows> findByFollowerId(Pageable pageable, String followerId);
    Page<Follows> findByFollowingId(Pageable pageable, String followingId);
    int countByFollowing_UserId(String userId);
    int countByFollower_UserId(String userId);
}
