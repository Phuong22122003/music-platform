package com.MusicPlatForm.user_library_service.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.dto.response.history.HistoryResponse;
import com.MusicPlatForm.user_library_service.entity.History;
import com.MusicPlatForm.user_library_service.httpclient.MusicClient;
import com.MusicPlatForm.user_library_service.mapper.Playlist.HistoryMapper;
import com.MusicPlatForm.user_library_service.repository.HistoryRepository;
import com.MusicPlatForm.user_library_service.repository.LikedTrackRepository;
import com.MusicPlatForm.user_library_service.service.iface.HistorySerivceInterface;

import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@RequiredArgsConstructor 
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Service
public class HistoryService implements HistorySerivceInterface {
    HistoryRepository historyRepository;
    HistoryMapper historyMapper;
    LikedTrackRepository likedTrackRepository;
    MusicClient musicClient;

    @PreAuthorize("isAuthenticated()")
    @Override
    public List<TrackResponse> getHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<History> histories = this.historyRepository.findAllByUserIdOrderByListenedAtDesc(userId);
        List<String> trackIds = new ArrayList<>();

        for (History history : histories) {
            if (!trackIds.contains(history.getTrackId())) {
                trackIds.add(history.getTrackId());
            }
        }
    
        List<TrackResponse> trackResponses = musicClient.getTrackByIds(trackIds).getData();
        List<String> likedTrackIds =  likedTrackRepository.findAllByUserId(userId).stream().map(l->l.getTrackId()).toList();
        for(var track: trackResponses){
            if(likedTrackIds.contains(track.getId())){
                track.setIsLiked(true);
            }
        }
        
        return trackResponses;
    }
    public static int convertMinuteSecondToSeconds(String time) {
        String[] parts = time.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time format, expected m:ss");
        }
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public HistoryResponse addHistory(String trackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        History history = this.historyRepository.findFirstByUserIdAndTrackIdOrderByListenedAtAsc(userId,trackId);
        LocalDateTime now = LocalDateTime.now();
        boolean isUpdatePlayCount = false;
        if(history==null)
        {
            history = new History();
            history.setUserId(userId);
            history.setTrackId(trackId);
            history.setCount(1);
        }
        else{
            LocalDateTime listenedAt = history.getListenedAt();
            if(listenedAt.toLocalDate().isBefore(now.toLocalDate())){
                history = new History();
                history.setUserId(userId);
                history.setTrackId(trackId);
                history.setCount(1);
            }
            else 
            {
                TrackResponse track = musicClient.getTrackById(history.getTrackId()).getData();
                if(track!=null)
                if(Duration.between(listenedAt, now).getSeconds()>convertMinuteSecondToSeconds(track.getDuration())){
                        history.setCount(history.getCount()+1);
                        isUpdatePlayCount = true;
                }
            } 
        }
        history.setListenedAt(now);
        History savedHistory = this.historyRepository.save(history);
        if(isUpdatePlayCount)musicClient.updatePlayCount(trackId);
        return historyMapper.toHistoryResponse(savedHistory);
    }
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void clearAllHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        this.historyRepository.deleteAllByUserId(userId);
    }
    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void deleteHistoryById(String trackId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        this.historyRepository.deleteByUserIdAndTrackId(userId, trackId);
    }
    
}
