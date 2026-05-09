package com.MusicPlatForm.file_service.configuration;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class FileStorageConfig {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
            System.out.println("Created upload directory: " + uploadDir);
        }
        else System.out.println("Folder exist: " + uploadDir);


        File avatarsFolder = new File(uploadDir+"/avatars");
        if (!avatarsFolder.exists()) {
            avatarsFolder.mkdirs();
            System.out.println("Created avatar folder directory: " + avatarsFolder);
        }
        File musicsFolder = new File(uploadDir+"/covers");
        if (!musicsFolder.exists()) {
            musicsFolder.mkdirs();
            System.out.println("Created avatar folder directory: " + musicsFolder);
        }
        File coversFolder = new File(uploadDir+"/musics");
        if (!coversFolder.exists()) {
            coversFolder.mkdirs();
            System.out.println("Created avatar folder directory: " + coversFolder);
        }
       

    }
}
