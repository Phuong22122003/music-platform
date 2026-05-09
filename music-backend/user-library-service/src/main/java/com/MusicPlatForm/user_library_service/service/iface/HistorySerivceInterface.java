package com.MusicPlatForm.user_library_service.service.iface;

import java.util.List;

import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.dto.response.history.HistoryResponse;

@Service
public interface HistorySerivceInterface {
    public List<TrackResponse> getHistory();
    public HistoryResponse addHistory(String trackId);
    public void clearAllHistory();
    public void deleteHistoryById(String trackId);
}
