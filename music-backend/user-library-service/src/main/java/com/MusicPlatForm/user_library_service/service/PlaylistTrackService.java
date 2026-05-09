package com.MusicPlatForm.user_library_service.service;

import java.time.LocalDateTime;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddPlaylistTrackRequest;
import com.MusicPlatForm.user_library_service.dto.response.playlist.PlaylistTrackResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.entity.Playlist;
import com.MusicPlatForm.user_library_service.entity.PlaylistTrack;
import com.MusicPlatForm.user_library_service.exception.AppException;
import com.MusicPlatForm.user_library_service.exception.ErrorCode;
import com.MusicPlatForm.user_library_service.mapper.Playlist.PlaylistTrackMapper;
import com.MusicPlatForm.user_library_service.repository.PlaylistRepository;
import com.MusicPlatForm.user_library_service.repository.PlaylistTrackRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class PlaylistTrackService {
    PlaylistTrackRepository playlistTrackRepository;
    PlaylistRepository playlistRepository;
    PlaylistTrackMapper playlistTrackMapper;

    @PreAuthorize("isAuthenticated()")
    public void deleteAllByPlaylistId(String playlistId){
        playlistTrackRepository.deleteAllPlaylistByPlaylistId(playlistId);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteTrackFromPlaylist(AddPlaylistTrackRequest trackRequest){
        PlaylistTrack playlistTrack = this.playlistTrackRepository.findByTrackIdAndPlaylistId(trackRequest.getTrackId(), trackRequest.getPlaylistId());
        if(playlistTrack ==null) throw new AppException(ErrorCode.TRACK_IN_PLAYLIST_NOT_FOUND);
        this.playlistTrackRepository.delete(playlistTrack);
    }
    @PreAuthorize("isAuthenticated()")
    public PlaylistTrackResponse addTrackToPlaylist(AddPlaylistTrackRequest trackRequest){
        PlaylistTrack playlistTrack = playlistTrackMapper.toPlaylistTrack(trackRequest);
        Playlist playlist = playlistRepository.findById(trackRequest.getPlaylistId())
                .orElseThrow(()->new AppException(ErrorCode.PLAYLIST_NOT_FOUND));
        playlistTrack.setPlaylist(playlist);
        playlistTrack.setAddedAt(LocalDateTime.now());

        PlaylistTrack savedPlaylistTrack = this.playlistTrackRepository.save(playlistTrack);
        PlaylistTrackResponse playlistTrackResponse = this.playlistTrackMapper.toPlaylistTrackResponse(savedPlaylistTrack);
        return playlistTrackResponse;
    }
}
