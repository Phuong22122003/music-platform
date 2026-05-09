package com.MusicPlatForm.file_service.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.file_service.dto.response.AudioResponse;

@Service
public interface MultiAudioServiceInteface {
    public List<AudioResponse> storeMulti(List<MultipartFile> trackFiles) throws IOException;
}
