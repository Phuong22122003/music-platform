package com.MusicPlatForm.file_service.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.file_service.dto.response.AudioResponse;
import com.MusicPlatForm.file_service.exception.AppException;
import com.MusicPlatForm.file_service.type.FileType;

@Service
public class FileStorageService {
    private final Map<FileType, FileStorageStrategy> strategyMap;

    public FileStorageService(List<FileStorageStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> FileType.values()[
                            List.of(FileType.values()).indexOf(
                                List.of(FileType.values()).stream()
                                    .filter(strategy::supports).findFirst().orElseThrow()
                            )
                        ],
                        strategy -> strategy
                ));
    }

    public Object upload(FileType type, MultipartFile file) throws IOException {
        String userId = getCurrentUserId();
        return strategyMap.get(type).store(file, userId);
    }

    public void delete(FileType type, String filename) throws IOException, AppException {
        String userId = getCurrentUserId();
        strategyMap.get(type).delete(filename, userId);
    }

    public String replace(FileType type,MultipartFile file,String filename) throws IOException{
        String userId = getCurrentUserId();
        return (String) strategyMap.get(type).replace(file,filename, userId);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
