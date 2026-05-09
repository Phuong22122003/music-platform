package com.MusicPlatForm.search_service.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.search_service.Dto.ApiResponse;
import com.MusicPlatForm.search_service.Dto.Request.AlbumRequest;
import com.MusicPlatForm.search_service.Entity.Album;
import com.MusicPlatForm.search_service.Service.SearchAlbumSerivce;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/albums")
public class SearchAlbumRestController {
    private SearchAlbumSerivce searchAlbumService;
    
    @KafkaListener(topics = "add_album_to_search", groupId = "search_group")
    public void addAlbumToSearch(AlbumRequest album) {
        searchAlbumService.save(album);
    }

    @KafkaListener(topics = "delete_album_from_search", groupId = "search_group")
    public void deleteAlbumFromSearch(String albumId) {
        searchAlbumService.deleteAlbumByAlbumId(albumId);
    }
    @KafkaListener(topics = "update_album_to_search", groupId = "search_group")
    public void updateAlbum(AlbumRequest album) {
        searchAlbumService.update(album);
    }
    

   @GetMapping
    public ResponseEntity<?> search(@RequestParam(name = "q") String query){
        return ResponseEntity.ok().body(
            ApiResponse.<List<String>>builder().code(200).data(searchAlbumService.searchAlbums(query)).build()
        );
    }

    @GetMapping("/all")
    public List<Album> getAll(){
        return this.searchAlbumService.getAllAlbums();
    }
}
