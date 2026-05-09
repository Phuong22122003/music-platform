package com.MusicPlatForm.user_library_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.user_library_service.dto.request.playlist.AddPlaylistRequest;
import com.MusicPlatForm.user_library_service.dto.request.playlist.UpdatePlaylistInfoRequest;
import com.MusicPlatForm.user_library_service.dto.response.AddCoverFileResponse;
import com.MusicPlatForm.user_library_service.dto.response.ApiResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TagResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.dto.response.playlist.PlaylistResponse;
import com.MusicPlatForm.user_library_service.entity.Playlist;
import com.MusicPlatForm.user_library_service.entity.PlaylistTag;
import com.MusicPlatForm.user_library_service.entity.PlaylistTrack;
import com.MusicPlatForm.user_library_service.exception.AppException;
import com.MusicPlatForm.user_library_service.exception.ErrorCode;
import com.MusicPlatForm.user_library_service.httpclient.FileClient;
import com.MusicPlatForm.user_library_service.httpclient.MusicClient;
import com.MusicPlatForm.user_library_service.httpclient.ProfileClient;
import com.MusicPlatForm.user_library_service.mapper.Playlist.PlaylistMapper;
import com.MusicPlatForm.user_library_service.mapper.Playlist.PlaylistTrackMapper;
import com.MusicPlatForm.user_library_service.repository.LikedPlaylistRepository;
import com.MusicPlatForm.user_library_service.repository.LikedTrackRepository;
import com.MusicPlatForm.user_library_service.repository.PlaylistRepository;
import com.MusicPlatForm.user_library_service.service.iface.PlaylistServiceInterface;
import com.MusicPlatForm.user_library_service.dto.response.client.GenreResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.ProfileWithCountFollowResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlaylistService  implements PlaylistServiceInterface{

    private final LikedTrackRepository likedTrackRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;
    private final PlaylistTrackMapper playlistTrackMapper;
    private final FileClient fileClient;
    private final MusicClient musicClient;
    private final ProfileClient profileClient;
    private final LikedPlaylistRepository likedPlaylistRepository;
    private final KafkaService kafkaService;
    private PlaylistResponse convertFromPlaylistToPlaylistResponse(Playlist playlist){
        List<String> trackIds = new ArrayList<>();
        List<String> tagIds = new ArrayList<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggingUserId = (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
                ? null : authentication.getName();

        List<String> likedTrackIds = (loggingUserId == null) ? null
        : likedTrackRepository.findAllByUserId(loggingUserId).stream()
                .map(track -> track.getTrackId()).toList();

        if(playlist.getPlaylistTags()!=null)
            for(var tag: playlist.getPlaylistTags()){
                tagIds.add(tag.getTagId());
            }
        if(playlist.getPlaylistTracks()!=null)
            for(var track: playlist.getPlaylistTracks()){
                trackIds.add(track.getTrackId());
            }
        ApiResponse<List<TrackResponse>> tracksResponse =  musicClient.getTrackByIds(trackIds);
        ApiResponse<List<TagResponse>> tagsResponse = musicClient.getTagsByIds(tagIds);
        PlaylistResponse playlistResponse = this.playlistMapper.toPlaylistResponse(playlist);
        
        if(playlist.getGenreId()!=null){
            ApiResponse<GenreResponse> genre = musicClient.getGenreById(playlist.getGenreId());
            playlistResponse.setGenre(genre.getData());
        }
        playlistResponse.setPlaylistTags(tagsResponse.getData());
        if(likedTrackIds!=null){
            tracksResponse.getData().forEach((track)->{
                if(likedTrackIds.contains(track.getId())){
                    track.setIsLiked(true);
                }
            });
        }
        playlistResponse.setPlaylistTracks(tracksResponse.getData());
        if(playlistResponse.getImagePath()==null&&playlistResponse.getPlaylistTracks()!=null&&playlistResponse.getPlaylistTracks().size()>0){
            playlistResponse.setImagePath(playlistResponse.getPlaylistTracks().get(0).getCoverImageName());
        }
        return playlistResponse;
    }
    
    public ApiResponse<List<PlaylistResponse>> getPlaylistsByIds(List<String> ids) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggingUserId = (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
                ? null : authentication.getName();

        List<String> trackIds = new ArrayList<>();
        List<String> tagIds = new ArrayList<>();
        List<String> genreIds = new ArrayList<>();
        Map<String, TrackResponse> idToTrackResponse = new HashMap<>();
        Map<String, TagResponse> idToTagResponse = new HashMap<>();
        Map<String, GenreResponse> idToGenreResponse = new HashMap<>();
        Map<String,ProfileWithCountFollowResponse> idToUserMapping = new HashMap<>();

        List<Playlist> playlists = this.playlistRepository.findAllById(ids);

        List<String> likedPlaylistIds = (loggingUserId == null) ? null
                : likedPlaylistRepository.findAllByUserId(loggingUserId).stream()
                        .map(likedPlaylist -> likedPlaylist.getPlaylist().getId()).toList();

        List<String> likedTrackIds = (loggingUserId == null) ? null
                : likedTrackRepository.findAllByUserId(loggingUserId).stream()
                        .map(track -> track.getTrackId()).toList();

        playlists.forEach(pl -> {
            pl.getPlaylistTracks().forEach(tr -> {
                if (!trackIds.contains(tr.getTrackId())) trackIds.add(tr.getTrackId());
            });
            pl.getPlaylistTags().forEach(tg -> {
                if (!tagIds.contains(tg.getTagId())) tagIds.add(tg.getTagId());
            });
            if (!genreIds.contains(pl.getGenreId())) genreIds.add(pl.getGenreId());
        });

        List<String> userIds = playlists.stream().map(p->p.getUserId()).distinct().toList();

        List<ProfileWithCountFollowResponse> users = profileClient.getUserProfileByIds(userIds).getData();
        users.forEach(user->idToUserMapping.put(user.getUserId(),user));

        musicClient.getTrackByIds(trackIds).getData().forEach(track -> idToTrackResponse.putIfAbsent(track.getId(), track));
        musicClient.getTagsByIds(tagIds).getData().forEach(tag -> idToTagResponse.putIfAbsent(tag.getId(), tag));
        musicClient.getGenresByIds(tagIds).getData().forEach(genre -> idToGenreResponse.putIfAbsent(genre.getId(), genre));

        List<PlaylistResponse> playlistResponses = playlists.stream().map(playlist -> {
            PlaylistResponse playlistResponse = playlistMapper.toPlaylistResponse(playlist);
            playlistResponse.setUser(idToUserMapping.get(playlist.getUserId()));
            playlistResponse.setIsLiked(likedPlaylistIds != null && likedPlaylistIds.contains(playlist.getId()));
            playlistResponse.setPlaylistTags(playlist.getPlaylistTags().stream()
                    .map(tag -> idToTagResponse.get(tag.getTagId())).collect(Collectors.toList()));
            playlistResponse.setPlaylistTracks(playlist.getPlaylistTracks()
            .stream()
            .filter(track -> idToTrackResponse.get(track.getTrackId()) != null)
            .map(track -> {
                TrackResponse trackResponse = idToTrackResponse.get(track.getTrackId());
                if (likedTrackIds != null && likedTrackIds.contains(track.getTrackId())) {
                    trackResponse.setIsLiked(true);
                }
                return trackResponse;
            }).collect(Collectors.toList()));
            if (playlist.getGenreId() != null) {
                playlistResponse.setGenre(idToGenreResponse.get(playlist.getGenreId()));
            }
            return playlistResponse;
        }).collect(Collectors.toList());



        return ApiResponse.<List<PlaylistResponse>>builder()
                .data(playlistResponses)
                .code(200)
                .message("Playlists for user")
                .build();
    }

    public ApiResponse<List<PlaylistResponse>> getPlaylistsByUserId(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggingUserId = (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
                ? null : authentication.getName();

        List<String> trackIds = new ArrayList<>();
        List<String> tagIds = new ArrayList<>();
        List<String> genreIds = new ArrayList<>();
        Map<String, TrackResponse> idToTrackResponse = new HashMap<>();
        Map<String, TagResponse> idToTagResponse = new HashMap<>();
        Map<String, GenreResponse> idToGenreResponse = new HashMap<>();

        ProfileWithCountFollowResponse user = profileClient.getUserProfile(userId).getData();

        List<Playlist> playlists = (loggingUserId == null || !loggingUserId.equals(userId))
                ? playlistRepository.getPublicPlaylistsByUserId(userId)
                : playlistRepository.getPlaylistsByUserId(loggingUserId);

        List<String> likedPlaylistIds = (loggingUserId == null) ? null
                : likedPlaylistRepository.findAllByUserId(loggingUserId).stream()
                        .map(likedPlaylist -> likedPlaylist.getPlaylist().getId()).toList();

        List<String> likedTrackIds = (loggingUserId == null) ? null
                : likedTrackRepository.findAllByUserId(loggingUserId).stream()
                        .map(track -> track.getTrackId()).toList();

        playlists.forEach(pl -> {
            pl.getPlaylistTracks().forEach(tr -> {
                if (!trackIds.contains(tr.getTrackId())) trackIds.add(tr.getTrackId());
            });
            pl.getPlaylistTags().forEach(tg -> {
                if (!tagIds.contains(tg.getTagId())) tagIds.add(tg.getTagId());
            });
            if (!genreIds.contains(pl.getGenreId())) genreIds.add(pl.getGenreId());
        });

        musicClient.getTrackByIds(trackIds).getData().forEach(track -> idToTrackResponse.putIfAbsent(track.getId(), track));
        musicClient.getTagsByIds(tagIds).getData().forEach(tag -> idToTagResponse.putIfAbsent(tag.getId(), tag));
        musicClient.getGenresByIds(tagIds).getData().forEach(genre -> idToGenreResponse.putIfAbsent(genre.getId(), genre));

        List<PlaylistResponse> playlistResponses = playlists.stream().map(playlist -> {
            PlaylistResponse playlistResponse = playlistMapper.toPlaylistResponse(playlist);
            playlistResponse.setUser(user);
            playlistResponse.setIsLiked(likedPlaylistIds != null && likedPlaylistIds.contains(playlist.getId()));
            playlistResponse.setPlaylistTags(playlist.getPlaylistTags().stream()
                    .map(tag -> idToTagResponse.get(tag.getTagId())).collect(Collectors.toList()));
            playlistResponse.setPlaylistTracks(playlist.getPlaylistTracks()
                .stream()
                .filter(track -> idToTrackResponse.get(track.getTrackId()) != null)
                .map(track -> {
                    TrackResponse trackResponse = idToTrackResponse.get(track.getTrackId());
                    if (likedTrackIds != null && likedTrackIds.contains(track.getTrackId())) {
                        trackResponse.setIsLiked(true);
                    }
                    return trackResponse;
                }).collect(Collectors.toList()));
            if (playlist.getGenreId() != null) {
                playlistResponse.setGenre(idToGenreResponse.get(playlist.getGenreId()));
            }
            return playlistResponse;
        }).collect(Collectors.toList());

        return ApiResponse.<List<PlaylistResponse>>builder()
                .data(playlistResponses)
                .code(200)
                .message("Playlists for user")
                .build();
    }

 
    public ApiResponse<List<PlaylistResponse>> getPlaylists(String option){
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.isAuthenticated()) throw new AppException(ErrorCode.UNAUTHENTICATED);
        String userId = authentication.getName();
        //To get from music service
        List<String> trackIds = new ArrayList<>();
        List<String> tagIds = new ArrayList<>();
        List<String> genreIds = new ArrayList<>();
        List<String> userIds = new ArrayList<>();

        // Map Id-> TrackResponse
        Map<String, TrackResponse> idToTrackResponse = new HashMap<>();
        Map<String, TagResponse> idToTagResponse = new HashMap<>();
        Map<String,GenreResponse> idToGenreResponse = new HashMap<>();
        Map<String,ProfileWithCountFollowResponse> idToUserMapping = new HashMap<>();
        List<Playlist> createdPlaylists = new ArrayList<>();
        List<Playlist> likedPlaylists = new ArrayList<>();

        if(option == "ALL"){
            createdPlaylists = this.playlistRepository.getPlaylistsByUserId(userId);
            likedPlaylists = this.likedPlaylistRepository.findAllByUserId(userId).stream().map((likedPlaylist)->likedPlaylist.getPlaylist()).toList();
            
        }
        else if(option=="LIKED"){
            likedPlaylists = this.likedPlaylistRepository.findAllByUserId(userId).stream().map((likedPlaylist)->likedPlaylist.getPlaylist()).toList();
        }
        else{
           createdPlaylists = this.playlistRepository.getPlaylistsByUserId(userId);
        }
  
        // trường hợp người dùng tạo và like playlist đó thì xóa bớt trong phần tạo -> truong hop all
        Set<String> likedPlaylistIds = likedPlaylists.stream().map(pl->pl.getId()).collect(Collectors.toSet());
        createdPlaylists.removeIf(pl->likedPlaylistIds.contains(pl.getId()));

        for(var pl: Stream.concat(createdPlaylists.stream(), likedPlaylists.stream())
                                .collect(Collectors.toList())){
            if(!userIds.contains(pl.getUserId())) userIds.add(pl.getUserId());
            for(var tr: pl.getPlaylistTracks()){
                if(!trackIds.contains(tr.getTrackId())){
                    trackIds.add(tr.getTrackId());
                }
            }
            for(var tg: pl.getPlaylistTags()){
                if(!tagIds.contains(tg.getTagId())){
                    tagIds.add(tg.getTagId());
                }
            }
            if(!genreIds.contains(pl.getGenreId())){
                genreIds.add(pl.getGenreId());
            }
        }
        List<ProfileWithCountFollowResponse> users = profileClient.getUserProfileByIds(userIds).getData();
        List<TrackResponse> tracksResponse =  musicClient.getTrackByIds(trackIds).getData();
        List<TagResponse> tagsResponse = musicClient.getTagsByIds(tagIds).getData();
        List<GenreResponse> genresResponse = musicClient.getGenresByIds(tagIds).getData();
    
        tracksResponse.forEach(t->idToTrackResponse.put(t.getId(), t));
        tagsResponse.forEach(tag->idToTagResponse.put(tag.getId(), tag));
        genresResponse.forEach(genre->idToGenreResponse.put(genre.getId(),genre));
        users.forEach(user->idToUserMapping.put(user.getUserId(), user));

        List<PlaylistResponse> playlistResponses =  new ArrayList<>();// playlistMapper.toPlaylistResponses(createdPlaylists);
        for(var playlist: Stream.concat(createdPlaylists.stream(),likedPlaylists.stream()).collect(Collectors.toList())){
            var playlistResponse = playlistMapper.toPlaylistResponse(playlist);
            playlistResponse.setPlaylistTags(new ArrayList<>());
            playlistResponse.setPlaylistTracks(new ArrayList<>());
            for(var tag: playlist.getPlaylistTags()){
                playlistResponse.getPlaylistTags().add(idToTagResponse.get(tag.getTagId()));
            }
            for(var track: playlist.getPlaylistTracks()){
                var t = idToTrackResponse.get(track.getTrackId());
                if(t==null)    continue;
                
                playlistResponse.getPlaylistTracks().add(t);
            }
            if(playlist.getGenreId()!=null){
                playlistResponse.setGenre(idToGenreResponse.get(playlist.getGenreId()));
            }
            
            if(likedPlaylistIds.contains(playlist.getId()))
                playlistResponse.setIsLiked(true);
            else  playlistResponse.setIsLiked(false);

            var user = idToUserMapping.get(playlist.getUserId());
            playlistResponse.setUser(user);
            playlistResponses.add(playlistResponse);
        }


        playlistResponses.sort((e1,e2)-> e2.getCreatedAt().compareTo(e1.getCreatedAt()));

        return ApiResponse.<List<PlaylistResponse>>builder()
                                            .data(playlistResponses)
                                            .code(200)
                                            .message("Playlists for user")
                                            .build();
    }

    
    //done
    public PlaylistResponse getPlaylistById(String id){
        Playlist playlist = this.playlistRepository.findById(id)
                    .orElseThrow(()->new AppException(ErrorCode.PLAYLIST_NOT_FOUND));
        ProfileWithCountFollowResponse user = profileClient.getUserProfile(playlist.getUserId()).getData();
        var response = convertFromPlaylistToPlaylistResponse(playlist);
        response.setUser(user);
        return response;
    }



    //done
    @Transactional
    public PlaylistResponse addPlaylist(AddPlaylistRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String privacy = request.getPrivacy();
        if(privacy==null||!privacy.toLowerCase().equals("private")&&!privacy.toLowerCase().equals("public"))
            throw new AppException(ErrorCode.INVALID_PRIVACY);
        if(privacy.toLowerCase().equals("private"))
            privacy = "PRIVATE";
        else privacy = "PUBLIC";

        if(userId==null){
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }

        Playlist playlist = playlistMapper.toPlaylist(request);
        playlist.setPrivacy(privacy);
        if(request.getTrackIds()!=null&&request.getTrackIds().size()>0){
            List<PlaylistTrack>playlistTracks = playlistTrackMapper.toPlaylistTracksFromTrackIds(request.getTrackIds());
            AtomicInteger index = new AtomicInteger(0);
            playlistTracks.forEach(track->{
                track.setPlaylist(playlist);
                track.setAddedAt(LocalDateTime.now().plusNanos(index.getAndIncrement() * 1000));
            });
            playlist.setPlaylistTracks(playlistTracks);
            
        }

        playlist.setUserId(userId);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        PlaylistResponse playlistResponse = convertFromPlaylistToPlaylistResponse(savedPlaylist);
        this.kafkaService.sendPlaylistToSearchService(savedPlaylist);
        ProfileWithCountFollowResponse user = profileClient.getUserProfile(playlist.getUserId()).getData();
        playlistResponse.setUser(user);
        return playlistResponse;
    }

    //done
    @Transactional
    public PlaylistResponse updatePlaylistInfo(MultipartFile playlistCoverImage,UpdatePlaylistInfoRequest request){

        Playlist playlist = playlistRepository.findById(request.getId())
                                                .orElseThrow(()->new AppException(ErrorCode.PLAYLIST_NOT_FOUND));
        playlistMapper.updatePlaylistFromRequest(request, playlist);
        if(playlistCoverImage!=null){
            if(playlist.getImagePath()!=null&&playlist.getImagePath().length()>0){
                this.fileClient.deleteCoverImage(playlist.getImagePath());
            }
            ApiResponse<AddCoverFileResponse> addPlaylistCoverResponse = fileClient.addCover(playlistCoverImage); 
            playlist.setImagePath(addPlaylistCoverResponse.getData().getCoverName());
        }

        List<PlaylistTrack>updatedTracks = playlistTrackMapper.toPlaylistTracksFromTrackIds(request.getTrackIds());
        updatedTracks.forEach(track->{
            track.setPlaylist(playlist);
        });
        playlist.getPlaylistTracks().removeIf(track ->
                updatedTracks.stream().noneMatch(updated -> updated.getTrackId() != null && updated.getTrackId().equals(track.getTrackId()))
        );
        AtomicInteger index = new AtomicInteger(0);
        
        for (PlaylistTrack updatedTrack : updatedTracks) {
            boolean exists = playlist.getPlaylistTracks().stream()
                    .anyMatch(track -> track.getTrackId() != null && track.getTrackId().equals(updatedTrack.getTrackId()));
            if (!exists) {
                updatedTrack.setAddedAt(LocalDateTime.now().plusNanos(index.getAndIncrement() * 1000));
                playlist.getPlaylistTracks().add(updatedTrack);
            }
        }
        if(request.getGenreId()!=null){
            playlist.setGenreId(request.getGenreId());
        }
        playlist.getPlaylistTags().clear();
        List<PlaylistTag> playlistTags = request.getTagIds().stream().map(id->{
            var tag = new PlaylistTag();
            tag.setTagId(id);
            tag.setPlaylist(playlist);
            return tag;
        }).toList();
        playlist.getPlaylistTags().addAll(playlistTags);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        PlaylistResponse playlistResponse =convertFromPlaylistToPlaylistResponse(savedPlaylist);
        // PlaylistResponse playlistResponse = this.playlistMapper.toPlaylistResponse(savedPlaylist);
        this.kafkaService.sendUpdatedPlaylistToSearchService(savedPlaylist);
        ProfileWithCountFollowResponse user = profileClient.getUserProfile(playlist.getUserId()).getData();
        playlistResponse.setUser(user);
        return playlistResponse;
    }

    @Transactional
    public void deletePlaylistById(String trackId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId==null){
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
        Playlist playlist = this.playlistRepository.findById(trackId)
                                                    .orElseThrow(()->new AppException(ErrorCode.PLAYLIST_NOT_FOUND));

        if(!playlist.getUserId().equals(userId)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if(playlist.getImagePath()!=null&&playlist.getImagePath().length()>0){
            this.fileClient.deleteCoverImage(playlist.getImagePath());
        }
        this.playlistRepository.delete(playlist);
        this.kafkaService.deletePlaylistFromSearchService(playlist.getId());
    }

    @Override
    public List<PlaylistResponse> getPlaylistsByGenre(String genreId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggingUserId = (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
                ? null : authentication.getName();

        List<String> trackIds = new ArrayList<>();
        List<String> tagIds = new ArrayList<>();
        List<String> genreIds = new ArrayList<>();
        Map<String, TrackResponse> idToTrackResponse = new HashMap<>();
        Map<String, TagResponse> idToTagResponse = new HashMap<>();
        Map<String, GenreResponse> idToGenreResponse = new HashMap<>();
        Map<String,ProfileWithCountFollowResponse> idToUserMapping = new HashMap<>();

        List<Playlist> playlists =  playlistRepository.getPlaylistByGenreId(genreId);

        List<String> likedPlaylistIds = (loggingUserId == null) ? null
                : likedPlaylistRepository.findAllByUserId(loggingUserId).stream()
                        .map(likedPlaylist -> likedPlaylist.getPlaylist().getId()).toList();

        List<String> likedTrackIds = (loggingUserId == null) ? null
                : likedTrackRepository.findAllByUserId(loggingUserId).stream()
                        .map(track -> track.getTrackId()).toList();

        playlists.forEach(pl -> {
            pl.getPlaylistTracks().forEach(tr -> {
                if (!trackIds.contains(tr.getTrackId())) trackIds.add(tr.getTrackId());
            });
            pl.getPlaylistTags().forEach(tg -> {
                if (!tagIds.contains(tg.getTagId())) tagIds.add(tg.getTagId());
            });
            if (!genreIds.contains(pl.getGenreId())) genreIds.add(pl.getGenreId());
        });

        List<String> userIds = playlists.stream().map(p->p.getUserId()).distinct().toList();

        List<ProfileWithCountFollowResponse> users = profileClient.getUserProfileByIds(userIds).getData();
        users.forEach(user->idToUserMapping.put(user.getUserId(),user));

        musicClient.getTrackByIds(trackIds).getData().forEach(track -> idToTrackResponse.putIfAbsent(track.getId(), track));
        musicClient.getTagsByIds(tagIds).getData().forEach(tag -> idToTagResponse.putIfAbsent(tag.getId(), tag));
        musicClient.getGenresByIds(tagIds).getData().forEach(genre -> idToGenreResponse.putIfAbsent(genre.getId(), genre));

        List<PlaylistResponse> playlistResponses = playlists.stream().map(playlist -> {
            PlaylistResponse playlistResponse = playlistMapper.toPlaylistResponse(playlist);
            playlistResponse.setUser(idToUserMapping.get(playlist.getUserId()));
            playlistResponse.setIsLiked(likedPlaylistIds != null && likedPlaylistIds.contains(playlist.getId()));
            playlistResponse.setPlaylistTags(playlist.getPlaylistTags().stream()
                    .map(tag -> idToTagResponse.get(tag.getTagId())).collect(Collectors.toList()));
            playlistResponse.setPlaylistTracks(playlist.getPlaylistTracks().stream().map(track -> {
                TrackResponse trackResponse = idToTrackResponse.get(track.getTrackId());
                if (likedTrackIds != null && likedTrackIds.contains(track.getTrackId())) {
                    trackResponse.setIsLiked(true);
                }
                return trackResponse;
            }).collect(Collectors.toList()));
            if (playlist.getGenreId() != null) {
                playlistResponse.setGenre(idToGenreResponse.get(playlist.getGenreId()));
            }
            return playlistResponse;
        }).collect(Collectors.toList());
        return playlistResponses;
    }
}
