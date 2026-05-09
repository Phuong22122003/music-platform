package com.MusicPlatForm.file_service.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.MusicPlatForm.file_service.type.FileType;


public interface FileStorageStrategy {
    Object store(MultipartFile file, String userId) throws IOException;
    void delete(String filename, String userId) throws IOException;
    Object replace(MultipartFile file, String filename,String userId)  throws IOException;
    boolean supports(FileType type);
}
