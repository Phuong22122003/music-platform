package com.MusicPlatForm.user_library_service.mapper.Playlist;

import java.util.List;

import org.mapstruct.Mapper;

import com.MusicPlatForm.user_library_service.dto.response.history.HistoryResponse;
import com.MusicPlatForm.user_library_service.entity.History;

@Mapper(componentModel = "spring")
public interface HistoryMapper {
    HistoryResponse toHistoryResponse(History history);
    List<HistoryResponse> toHistoryResponses(List<History> histories); 
}
