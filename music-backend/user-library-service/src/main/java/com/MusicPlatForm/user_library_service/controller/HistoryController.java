package com.MusicPlatForm.user_library_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.dto.response.history.HistoryResponse;
import com.MusicPlatForm.user_library_service.service.iface.HistorySerivceInterface;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/history")
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryController {
    HistorySerivceInterface historySerivce;
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TrackResponse>>> getHistory(){
        var a = historySerivce.getHistory();
        return ResponseEntity
        .ok()
        .body(
            ApiResponse.<List<TrackResponse>>builder()
            .data(a)
            .code(HttpStatus.OK.value())
            .message("Get data successfully")
            .build()
            );
    }  

    @PostMapping
    public ResponseEntity<ApiResponse<HistoryResponse>> listenTrack(@RequestParam(name = "track_id") String trackId){
        return ResponseEntity
        .ok()
        .body(
            ApiResponse.<HistoryResponse>builder()
            .code(HttpStatus.OK.value())
            .data(historySerivce.addHistory(trackId)    )
            .message("Updated successfully")
            .build()
            );
    }
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<?>> clearAllHistory(){
        this.historySerivce.clearAllHistory();
        return ResponseEntity
        .ok()
        .body(
            ApiResponse.builder()
            .code(HttpStatus.OK.value())
            .message("Cleared successfully")
            .build()
            );
    }
    @DeleteMapping("/delete/{track_id}")
    public ResponseEntity<ApiResponse<?>> deleteHistoryById(@PathVariable(name = "track_id") String trackId){
        this.historySerivce.deleteHistoryById(trackId);
        return ResponseEntity
        .ok()
        .body(
            ApiResponse.builder()
            .code(HttpStatus.OK.value())
            .message("Deleted successfully")
            .build()
            );
    }
}
