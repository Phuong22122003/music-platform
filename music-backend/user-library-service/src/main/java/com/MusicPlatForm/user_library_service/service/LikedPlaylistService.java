package com.MusicPlatForm.user_library_service.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.response.playlist.LikedPlaylistResponse;
import com.MusicPlatForm.user_library_service.entity.LikedPlaylist;
import com.MusicPlatForm.user_library_service.entity.Playlist;
import com.MusicPlatForm.user_library_service.exception.AppException;
import com.MusicPlatForm.user_library_service.exception.ErrorCode;
import com.MusicPlatForm.user_library_service.mapper.Playlist.LikedPlaylistMapper;
import com.MusicPlatForm.user_library_service.repository.LikedPlaylistRepository;
import com.MusicPlatForm.user_library_service.repository.PlaylistRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LikedPlaylistService {
    private LikedPlaylistRepository likedPlaylistRepository;
    private PlaylistRepository playlistRepository;
    private LikedPlaylistMapper likedPlaylistMapper;

    public List<String> getUserIdsLikedPlaylist(String playlistId){
        List<String> userIds = this.likedPlaylistRepository.findByPlaylistId(playlistId).stream().map(l->l.getUserId()).distinct().toList();
        return userIds;
    }

    public List<LikedPlaylistResponse> getAllLikedPlaylist(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        List<LikedPlaylist> likedPlaylists = this.likedPlaylistRepository.findAllByUserId(userId);
        
        List<LikedPlaylistResponse> likedPlaylistResponses = likedPlaylistMapper.toLikedPlaylistResponses(likedPlaylists);
        return likedPlaylistResponses;
    }

    public Boolean isLiked(String playlistId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        LikedPlaylist likedPlaylist = this.likedPlaylistRepository.findByUserIdAndPlaylistId(userId, playlistId);
        return likedPlaylist!=null;
    }
    public Boolean likePlaylist(String playlistId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        LikedPlaylist likedPlaylist =
        this.likedPlaylistRepository.findByUserIdAndPlaylistId(userId, playlistId);
        if(likedPlaylist!=null) throw new AppException(ErrorCode.ALREADY_LIKED);
        Playlist playlist = this.playlistRepository.findById(playlistId).orElseThrow(()->new AppException(ErrorCode.PLAYLIST_NOT_FOUND));
        if(playlist.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);
        likedPlaylist =new LikedPlaylist();
        likedPlaylist.setPlaylist(playlist);
        likedPlaylist.setUserId(userId);
        this.likedPlaylistRepository.save(likedPlaylist);
        return true;
    }
    public Boolean unLikePlaylist(String playlistId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        LikedPlaylist likedPlaylist = this.likedPlaylistRepository.findByUserIdAndPlaylistId(userId, playlistId);
        if(likedPlaylist == null) throw new AppException(ErrorCode.ALREADY_UNLIKED);
        if(likedPlaylist.getPlaylist().getUserId().equals(userId))throw new AppException(ErrorCode.UNAUTHORIZED);
        this.likedPlaylistRepository.delete(likedPlaylist);
        return true;
    }

    public Integer getLikedCount(String playlistId){
        return likedPlaylistRepository.countByPlaylistId(playlistId);
    }
}
