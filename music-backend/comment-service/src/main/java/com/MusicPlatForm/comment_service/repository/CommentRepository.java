package com.MusicPlatForm.comment_service.repository;

import com.MusicPlatForm.comment_service.entity.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,String> {
    List<Comment> findByUserId(String userId);

    public int countByTrackId(String trackId);

    @Query("FROM Comment c WHERE c.trackId = :trackId AND c.repliedUserId is null ORDER BY c.commentAt DESC")
    List<Comment> findByTrackId(String trackId);

    @Query("FROM Comment c WHERE c.trackId IN :trackIds AND c.commentAt BETWEEN :fromDate AND :toDate")
    public List<Comment> findCommentsFromDateToDateByTrackIds(LocalDateTime fromDate, LocalDateTime toDate, List<String> trackIds);

    @Query("FROM Comment c WHERE c.trackId IN :trackIds")
    public List<Comment> findAllCommentsByTrackIds(List<String> trackIds);

    @Query("FROM Comment c WHERE c.commentAt BETWEEN :fromDate AND :toDate")
    public List<Comment> findAllCommentsFromDateToDate(LocalDateTime fromDate, LocalDateTime toDate);
    
    @Query("FROM Comment")
    public List<Comment> findAllComments();
}
