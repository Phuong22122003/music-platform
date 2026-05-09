package com.MusicPlatForm.search_service.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.search_service.Dto.ApiResponse;
import com.MusicPlatForm.search_service.Dto.Request.PlaylistRequest;
import com.MusicPlatForm.search_service.Entity.Playlist;
import com.MusicPlatForm.search_service.Service.SearchPlaylistService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/playlists")
public class SearchPlaylistRestController {
    private SearchPlaylistService searchPlaylistService;
    @GetMapping
    public ResponseEntity<?> search(@RequestParam(name = "q") String query){
        return ResponseEntity.ok().body(
            ApiResponse.<List<String>>builder().code(200).data(searchPlaylistService.searchPlaylists(query)).build()
        );
    }

    @GetMapping("/all")
    public List<Playlist> getAll(){
        return this.searchPlaylistService.getAllPlaylist();
    }

    
    @KafkaListener(topics = "add_playlist_to_search", groupId = "search_group")
    public void addPlaylistToSearch(PlaylistRequest playlist) {
        searchPlaylistService.save(playlist);
    }

    @KafkaListener(topics = "delete_playlist_from_search", groupId = "search_group")
    public void deletePlaylistFromSearch(String playlistId) {
        searchPlaylistService.deletePlaylistByPlaylistId(playlistId);
    }
    @KafkaListener(topics = "update_playlist_to_search",groupId = "search_group")
    public void updatePlaylistToSearch(PlaylistRequest playlist){
        
    }
}
