package com.MusicPlatForm.user_library_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.request.kafka.AlbumKafkaRequest;
import com.MusicPlatForm.user_library_service.dto.request.kafka.NotificationRequest;
import com.MusicPlatForm.user_library_service.dto.request.kafka.PlaylistKafkaRequest;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.entity.Album;
import com.MusicPlatForm.user_library_service.entity.LikedTrack;
import com.MusicPlatForm.user_library_service.entity.Playlist;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaService {
    KafkaTemplate<String,Object> kafkaTemplate;
    public void sendAlbumToSearchService(Album album){
        AlbumKafkaRequest albumKafkaRequest = new AlbumKafkaRequest();
        albumKafkaRequest.setAlbumId(album.getId());
        albumKafkaRequest.setDescription(album.getDescription());
        albumKafkaRequest.setTitle(album.getAlbumTitle());
        this.kafkaTemplate.send("add_album_to_search",albumKafkaRequest);
    }
    public void sendPlaylistToSearchService(Playlist playlist){
        try {
            PlaylistKafkaRequest playlistKafkaRequest = new PlaylistKafkaRequest();
            playlistKafkaRequest.setPlaylistId(playlist.getId());
            playlistKafkaRequest.setDescription(playlist.getDescription());
            playlistKafkaRequest.setTitle(playlist.getTitle());
            this.kafkaTemplate.send("add_playlist_to_search",playlistKafkaRequest);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void deletePlaylistFromSearchService(String playlistId){
        try {
            this.kafkaTemplate.send("delete_playlist_from_search",playlistId);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void deleteAlbumFromSearchService(String albumId){
        try {
            this.kafkaTemplate.send("delete_album_from_search",albumId);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void sendUpdatedAlbumToSearchService(Album album){
        try {
            AlbumKafkaRequest albumKafkaRequest = new AlbumKafkaRequest();
            albumKafkaRequest.setAlbumId(album.getId());
            albumKafkaRequest.setDescription(album.getDescription());
            albumKafkaRequest.setTitle(album.getAlbumTitle());
            this.kafkaTemplate.send("update_album_to_search",albumKafkaRequest);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void sendUpdatedPlaylistToSearchService(Playlist playlist){
        try {
            PlaylistKafkaRequest playlistKafkaRequest = new PlaylistKafkaRequest();
            playlistKafkaRequest.setPlaylistId(playlist.getId());
            playlistKafkaRequest.setDescription(playlist.getDescription());
            playlistKafkaRequest.setTitle(playlist.getTitle());
            this.kafkaTemplate.send("update_playlist_to_search",playlistKafkaRequest);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void sendNotificationForLikedTrack(TrackResponse track, LikedTrack likedTrack){
        try {
            if(likedTrack.getUserId().equals(track.getUserId()))return;
            NotificationRequest notification = NotificationRequest
                                                .builder()
                                                .trackId(track.getId())
                                                .recipientId(track.getUserId())
                                                .senderId(likedTrack.getUserId())
                                                .message(track.getUser().getDisplayName()+ " like your track \"" + track.getTitle()+"\"")
                                                .build();
            this.kafkaTemplate.send("like",notification);
    
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
