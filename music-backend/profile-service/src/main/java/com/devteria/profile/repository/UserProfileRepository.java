package com.devteria.profile.repository;

import com.devteria.profile.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String>{
    Optional<UserProfile> findByUserId(String userId);
    Optional<UserProfile> findByEmail(String email);
    List<UserProfile> findAllByUserIdIn(List<String> userId);
    Page<UserProfile> findAll(Pageable pageable);
    
    @Query("""
        SELECT u
        FROM UserProfile u
        LEFT JOIN u.followers f
        GROUP BY u
        ORDER BY COUNT(f) DESC
        """)
    List<UserProfile> findTopFollowedUsers(Pageable pageable);

}
