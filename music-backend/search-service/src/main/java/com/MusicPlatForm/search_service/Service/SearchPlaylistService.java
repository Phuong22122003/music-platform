package com.MusicPlatForm.search_service.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.search_service.Dto.Request.PlaylistRequest;
import com.MusicPlatForm.search_service.Entity.Playlist;
import com.MusicPlatForm.search_service.Mapper.PlaylistMapper;
import com.MusicPlatForm.search_service.Repository.PlaylistRepository;

@Service
public class SearchPlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistMapper playlistMapper;

    private Playlist toEntity(PlaylistRequest playlistRequest){
        Playlist playlist = new Playlist();
        playlist.setDescription(playlistRequest.getDescription());
        playlist.setPlaylistId(playlistRequest.getPlaylistId());
        playlist.setTitle(playlistRequest.getTitle());
        return playlist;
    }
    public Playlist save(PlaylistRequest request) {
        Playlist playlist = toEntity(request);
        return playlistRepository.save(playlist);
    }
    public void update(PlaylistRequest playlistRequest){
        Playlist playlist = this.playlistRepository.findByPlaylistId(playlistRequest.getPlaylistId()).get();
        if(playlist==null) return;
        playlist.setDescription(playlistRequest.getDescription());
        playlist.setTitle(playlistRequest.getTitle());
        this.playlistRepository.save(playlist);
    }
    public void deletePlaylistByPlaylistId(String playlistId) {
        playlistRepository.deleteByPlaylistId(playlistId);
    }
    public List<String> searchPlaylists(String query) {
        List<Playlist> playlists = playlistRepository.findPlaylists(query);
        return playlistMapper.toPlaylistIds(playlists);
    }
    public List<Playlist> getAllPlaylist(){
        return (List<Playlist>) this.playlistRepository.findAll();
    }
}
