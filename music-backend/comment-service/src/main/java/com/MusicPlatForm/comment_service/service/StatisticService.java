package com.MusicPlatForm.comment_service.service;

import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.MusicPlatForm.comment_service.dto.response.CommentStatisticResponse;

@Service
public interface StatisticService {

    @PreAuthorize("isAuthenticated()")
    public CommentStatisticResponse getComments(LocalDate fromDate, LocalDate toDate) ;
    @PreAuthorize("hasRole('ADMIN')")
    public CommentStatisticResponse getAllComments(LocalDate fromDate, LocalDate toDate) ;
}
