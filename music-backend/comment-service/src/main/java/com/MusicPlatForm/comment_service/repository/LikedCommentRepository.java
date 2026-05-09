package com.MusicPlatForm.comment_service.repository;

import com.MusicPlatForm.comment_service.entity.LikedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LikedCommentRepository extends JpaRepository<LikedComment,String> {
    LikedComment findByCommentIdAndUserId(String commentId, String userId);
    int countByCommentId(String commentId);
    List<LikedComment> findByCommentId(String commentId);
    List<LikedComment> findByUserId(String userId);
    void deleteByCommentIdAndUserId(String commentId, String userId);
    Integer countByCommentIdAndUserId(String commentId,String userId);
}
