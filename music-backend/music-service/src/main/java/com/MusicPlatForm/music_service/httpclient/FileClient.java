package com.MusicPlatForm.music_service.httpclient;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.music_service.dto.reponse.AddCoverFileResponse;
import com.MusicPlatForm.music_service.dto.reponse.AddTrackFileResponse;
import com.MusicPlatForm.music_service.dto.reponse.ApiResponse;




//Usee Kafka instead
@FeignClient(name = "file-service",url = "${app.services.file}")
public interface FileClient {

    /**
     * Add track cover
     * @param cover
     * @return ApiResponse<String>: File name
     */
    @PostMapping(value = "/images/covers",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AddCoverFileResponse> addCover(@RequestPart MultipartFile cover);
    @PostMapping(value = "/audios",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AddTrackFileResponse> addTrack(@RequestPart("audio") MultipartFile track);
    @PostMapping(value = "/audios/bulk",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<AddTrackFileResponse>> addTracks(@RequestPart("audioFiles") List<MultipartFile> tracks);
    
    @DeleteMapping(value = "/audios/{audioName}")
    public ResponseEntity<ApiResponse<String>> deleteAudio(@PathVariable String audioName); 
    @DeleteMapping(value = "/images/covers/{coverName}")
    public ResponseEntity<ApiResponse<String>> deleteCover(@PathVariable String coverName); 
}
