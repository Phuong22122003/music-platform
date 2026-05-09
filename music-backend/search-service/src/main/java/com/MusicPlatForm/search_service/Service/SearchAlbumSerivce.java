package com.MusicPlatForm.search_service.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.search_service.Dto.Request.AlbumRequest;
import com.MusicPlatForm.search_service.Entity.Album;
import com.MusicPlatForm.search_service.Mapper.AlbumMapper;
import com.MusicPlatForm.search_service.Repository.AlbumRepository;

@Service
public class SearchAlbumSerivce {
    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AlbumMapper albumMapper;

    public Album save(AlbumRequest request) {
        Album album = new Album();
        album.setAlbumId(request.getAlbumId());
        album.setDescription(request.getDescription());
        album.setTitle(request.getTitle());
        return albumRepository.save(album);
    }
    public void update(AlbumRequest request){
        Album album = this.albumRepository.findByAlbumId(request.getAlbumId());
        if(album == null)return;
        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        this.albumRepository.save(album);
    }
    public void deleteAlbumByAlbumId(String albumId) {
        albumRepository.deleteByAlbumId(albumId);
    }
    public List<String> searchAlbums(String query) {
        List<Album> albums = albumRepository.findAlbums(query);
        return albumMapper.toAlbumIds(albums);
    }
    public List<Album> getAllAlbums(){
        return (List<Album>) this.albumRepository.findAll();
    }
}
