package com.MusicPlatForm.music_service.service;

import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;
import com.MusicPlatForm.music_service.dto.reponse.CommentResponse;
import com.MusicPlatForm.music_service.entity.Track;
import com.MusicPlatForm.music_service.httpclient.CommentClient;
import com.MusicPlatForm.music_service.httpclient.LikedTrackClient;
import com.MusicPlatForm.music_service.repository.TrackRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ManageTrackService {
    TrackRepository trackRepository;
    CommentClient commentClient;
    LikedTrackClient likedTrackClient;

    public List<CommentResponse> getCommentsForTrack(String trackId) {
        ApiResponse<List<CommentResponse>> response = commentClient.getComments((trackId));
        return response.getData();
    }

    public int getCountPlay(String trackId){
        return trackRepository.findById(trackId)
                .map(Track::getCountPlay)
                .orElse(0);
    }

    public int getCommentCountForTrack(String trackId) {
        ApiResponse<List<CommentResponse>> response = commentClient.getComments(trackId);
        return (response.getData() != null) ? response.getData().size() : 0;
    }

    public int getTotalCommentLikes(String trackId) {
        ApiResponse<List<CommentResponse>> response = commentClient.getComments(trackId);
        return (response.getData() != null) ? response.getData().stream().mapToInt(CommentResponse::getLikeCount).sum() : 0;
    }

    public int getTrackLikeCount(String trackId) {
        ApiResponse<Integer> response = likedTrackClient.getTrackLikeCount(trackId);
        return response.getData();
    }
}
