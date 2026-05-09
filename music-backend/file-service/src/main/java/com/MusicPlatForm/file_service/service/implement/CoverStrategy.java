package com.MusicPlatForm.file_service.service.implement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.file_service.entity.Cover;
import com.MusicPlatForm.file_service.exception.AppException;
import com.MusicPlatForm.file_service.exception.ErrorCode;
import com.MusicPlatForm.file_service.repository.CoverRepository;
import com.MusicPlatForm.file_service.service.FileStorageStrategy;
import com.MusicPlatForm.file_service.type.FileType;

@Service
public class CoverStrategy implements FileStorageStrategy  {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.covers-dir}")
    private String coversDir;
    @Autowired private CoverRepository coverRepository;

    private String addImage(MultipartFile image,String folder)throws IOException{
        String name = Instant.now().getEpochSecond() + toSlug(image.getOriginalFilename());
        Path filePath = Paths.get(uploadDir).resolve(folder).resolve(name);
        Files.write(filePath, image.getBytes());
        return name;
    }

    private boolean deleteImage(String name,String folder)throws IOException{
        Path filePath = Paths.get(uploadDir).resolve(folder).resolve(name);
        boolean isDeleted = Files.deleteIfExists(filePath);
        return isDeleted;
    }

    private String repaceImage(MultipartFile image, String name, String folder) throws IOException, NoSuchFileException{
        boolean isDeleted = deleteImage(name, folder);
        if(!isDeleted) throw new NoSuchFileException("Image not found");
        String newName = addImage(image, folder);
        return newName;
    }
    public String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "") // remove accents â -> a
                        .replaceAll("[^a-zA-Z0-9\\.\\-]", "_"); // replace special characters a&&b-> ab
    }
    @Override
    public Object store(MultipartFile file, String userId) throws IOException {
        String coverName = addImage(file, coversDir);
        Cover coverEntity = new Cover(coverName, userId);
        coverRepository.save(coverEntity);
       return coverName;
    }

    @Override
    public void delete(String filename, String userId) throws IOException, AppException {
        Cover cover = this.coverRepository.findByFileName(filename).orElseThrow(()->new AppException(ErrorCode.COVER_FILE_NOT_FOUND));
        if(!cover.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);
        boolean isDeleted = deleteImage(filename, coversDir);
        if(!isDeleted) throw new AppException(ErrorCode.COVER_FILE_NOT_FOUND);
        this.coverRepository.delete(cover);
    }

    @Override
    public boolean supports(FileType type) {
        return type == FileType.COVER;
    }

    @Override
    public Object replace(MultipartFile file, String filename,String userId) throws NoSuchFileException, IOException {        
       
        if(filename==null) return addImage(file, userId);
        Cover coverEntity = this.coverRepository.findByFileName(filename)
            .orElseThrow(() -> new AppException(ErrorCode.COVER_FILE_NOT_FOUND)); 
        if (!coverEntity.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        String newCoverName = repaceImage(file, filename, coversDir);
        coverEntity.setFileName(newCoverName);
        this.coverRepository.save(coverEntity);
        return newCoverName;
    }
    
}
