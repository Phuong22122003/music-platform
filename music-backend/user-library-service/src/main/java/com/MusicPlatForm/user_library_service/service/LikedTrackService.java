package com.MusicPlatForm.user_library_service.service;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.entity.LikedTrack;
import com.MusicPlatForm.user_library_service.exception.AppException;
import com.MusicPlatForm.user_library_service.exception.ErrorCode;
import com.MusicPlatForm.user_library_service.httpclient.MusicClient;
import com.MusicPlatForm.user_library_service.repository.LikedTrackRepository;
import com.MusicPlatForm.user_library_service.service.iface.LikeTrackServiceInterface;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class LikedTrackService implements LikeTrackServiceInterface {
    LikedTrackRepository likedTrackRepository;
    MusicClient musicClient;
    KafkaService kafkaService;
    public List<String> getUserIdsLikedTrack(String trackId){
        return likedTrackRepository.findByTrackId(trackId).stream().map(l->l.getUserId()).distinct().toList();
    }


    public List<LikedTrack> getLikedTrack(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        List<LikedTrack> likedTracks = likedTrackRepository.findAllByUserId(userId);
        return likedTracks;
    }
    public List<TrackResponse> getAllLikedTracks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        List<LikedTrack> likedTracks = likedTrackRepository.findAllByUserId(userId);
        List<String> trackIds = likedTracks.stream().map(LikedTrack::getTrackId).collect(Collectors.toList());

        if (trackIds.isEmpty()) {
            return Collections.emptyList();
        }

        ApiResponse<List<TrackResponse>> response = musicClient.getTrackByIds(trackIds);
        response.getData().forEach(tr->tr.setIsLiked(true));
        return response.getData();
    }

    public int getTrackLikeCount(String trackId) {
        return likedTrackRepository.countByTrackId(trackId);
    }

    public Boolean isLiked(String trackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return likedTrackRepository.findByUserIdAndTrackId(userId, trackId) != null;
    }

    public Boolean likeTrack(String trackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        TrackResponse track = musicClient.getTrackById(trackId).getData();

        if (track == null) {
            throw new AppException(ErrorCode.TRACK_NOT_FOUND);
        }
 
        if (likedTrackRepository.findByUserIdAndTrackId(userId, trackId) != null) {
            throw new AppException(ErrorCode.ALREADY_LIKED);
        }
        LikedTrack likedTrack = new LikedTrack();
        likedTrack.setTrackId(trackId);
        likedTrack.setUserId(userId);
        likedTrack.setLikedAt(LocalDateTime.now());
        likedTrackRepository.save(likedTrack);
        this.kafkaService.sendNotificationForLikedTrack(track, likedTrack);
        return true;
    }
    //unlike theo id
    public Boolean unLikeTrack(String track_id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        LikedTrack likedTrack = likedTrackRepository.findByUserIdAndTrackId(userId,track_id);
        if(likedTrack==null) throw new AppException(ErrorCode.ALREADY_UNLIKED);
        if (!likedTrack.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        likedTrackRepository.delete(likedTrack);
        return true;
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public List<String> filterLikedIds(List<String> ids) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<LikedTrack> likedTracks = this.likedTrackRepository.findAllByUserId(userId);
        Set<String> likedIds = new HashSet<>();
        likedTracks.forEach(t->likedIds.add(t.getTrackId()));
        ids.removeIf(i->!likedIds.contains(i));
        return ids;
    }
}
