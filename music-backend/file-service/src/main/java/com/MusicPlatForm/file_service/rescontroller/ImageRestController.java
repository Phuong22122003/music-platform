package com.MusicPlatForm.file_service.rescontroller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.file_service.dto.ApiResponse;
import com.MusicPlatForm.file_service.dto.response.AvatarResponse;
import com.MusicPlatForm.file_service.dto.response.CoverResponse;
import com.MusicPlatForm.file_service.service.FileStorageService;
import com.MusicPlatForm.file_service.type.FileType;

@RestController
@RequestMapping("/images") //-> image-> images
public class ImageRestController {
    private FileStorageService fileStorageService;
    @Value("${file.upload-dir}")
    private String uploadDir;
    public ImageRestController(FileStorageService fileStorageService){
        this.fileStorageService = fileStorageService;
    }

    /*
     * GET
     */
    @GetMapping("avatars/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) throws IOException{
   
        Path path = Paths.get(uploadDir).resolve("avatars").resolve(filename);
        byte[] imageBytes = Files.readAllBytes(path);

        ByteArrayResource resource = new ByteArrayResource(imageBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(imageBytes.length)
                .body(resource);
            
    }
    @GetMapping("covers/{filename}")
    public ResponseEntity<Resource> getCoverImage(@PathVariable String filename) throws IOException{

        Path path = Paths.get(uploadDir).resolve("covers").resolve(filename);
        byte[] imageBytes = Files.readAllBytes(path);

        ByteArrayResource resource = new ByteArrayResource(imageBytes);
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(imageBytes.length)
                .body(resource);
            
    }

    /*
     * ADD
     */
    @PostMapping(value = "avatars",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AvatarResponse>> addAvatar(@RequestPart MultipartFile avatar) throws IOException{
        String avatarName = fileStorageService.upload(FileType.AVATAR,avatar).toString();
        return ResponseEntity.ok().body(
                ApiResponse.<AvatarResponse>
                    builder()
                        .code(200)
                        .message("Added avatar successfully")
                        .data(
                            AvatarResponse.builder().avatarName(avatarName).build()
                        ).build()
                );
    }
    @PostMapping(value = "covers",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CoverResponse>> addCoverImage(@RequestPart MultipartFile cover) throws IOException{
        String coverName = fileStorageService.upload(FileType.COVER,cover).toString();
        return ResponseEntity.ok().body(
                ApiResponse.<CoverResponse>
                    builder()
                        .code(200)
                        .message("Added cover successfully")
                        .data(
                            CoverResponse.builder().coverName(coverName).build()
                        ).build()
                );
    }

    

    @DeleteMapping("avatars/{avatarName}")
    public ResponseEntity<ApiResponse<String>> deleteAvatar(@PathVariable String avatarName) throws IOException, NoSuchFileException{
        fileStorageService.delete(FileType.AVATAR, avatarName);;
        return ResponseEntity.ok().body(
            ApiResponse.<String>
                builder()
                    .code(200)
                    .message("Deleted avatar successfully")
                    .build()
            );
    }


    @DeleteMapping("covers/{coverName}")
    public ResponseEntity<ApiResponse<String>> deleteCover(@PathVariable String coverName) throws IOException, NoSuchFileException{
        fileStorageService.delete(FileType.COVER,coverName);
        return ResponseEntity.ok().body(
            ApiResponse.<String>
                builder()
                    .code(200)
                    .message("Deleted cover successfully")
                    .build()
            );
    }

    
    /*
     * PUT
     */

    @PutMapping(value="avatars/{avatarName}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AvatarResponse>> replaceAvatar(@RequestPart("newAvatar") MultipartFile newAvatar,@PathVariable(name = "avatarName") String oldAvatarName) throws IOException, NoSuchFileException{
        String newAvatarName = fileStorageService.replace(FileType.AVATAR,newAvatar, oldAvatarName);
        return ResponseEntity.ok().body(
                ApiResponse.<AvatarResponse>
                    builder()
                        .code(200)
                        .message("Replaced avatar successfully")
                        .data(
                            AvatarResponse.
                            builder().
                            avatarName(newAvatarName)
                            .build()
                            )
                        .build()
                );
    }
    @PutMapping(value = "covers/{coverName}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CoverResponse>> replaceCover(@RequestPart MultipartFile newCover,@PathVariable(name="coverName") String oldCoverName) throws IOException, NoSuchFileException{
        String newCoverName = fileStorageService.replace(FileType.COVER,newCover, oldCoverName);
        return ResponseEntity.ok().body(
                ApiResponse.<CoverResponse>
                    builder()
                        .code(200)
                        .message("Replaced cover successfully")
                        .data(
                            CoverResponse.
                            builder().
                            coverName(newCoverName)
                            .build()
                            )
                        .build()
                );
    }
}
