package com.MusicPlatForm.music_service.service.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.MusicPlatForm.music_service.dto.reponse.*;
import com.MusicPlatForm.music_service.dto.request.UpdateTrackRequest;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.MusicPlatForm.music_service.dto.request.TrackRequest;
import com.MusicPlatForm.music_service.entity.Genre;
import com.MusicPlatForm.music_service.entity.Tag;
import com.MusicPlatForm.music_service.entity.Track;
import com.MusicPlatForm.music_service.entity.TrackTag;
import com.MusicPlatForm.music_service.exception.AppException;
import com.MusicPlatForm.music_service.exception.ErrorCode;
import com.MusicPlatForm.music_service.httpclient.FileClient;
import com.MusicPlatForm.music_service.httpclient.LikedTrackClient;
import com.MusicPlatForm.music_service.httpclient.ProfileClient;
import com.MusicPlatForm.music_service.mapper.GenreMapper;
import com.MusicPlatForm.music_service.mapper.TagMapper;
import com.MusicPlatForm.music_service.mapper.TrackMapper;
import com.MusicPlatForm.music_service.repository.*;
import com.MusicPlatForm.music_service.service.iface.TrackServiceInterface;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TrackService implements TrackServiceInterface{
    FileClient fileClient;
    TrackMapper trackMapper;
    TagMapper tagMapper;
    GenreMapper genreMapper;
    TagRepository tagRepository;
    TrackRepository trackRepository;
    GenreRepository genreRepository;
    ProfileClient profileClient;
    LikedTrackClient likedTrackClient;
    private KafkaTemplate<String,Object> kafkaTemplate;
    private void sendTrackToSearchService(Track track){
        try {
            com.MusicPlatForm.music_service.dto.kafka_request.TrackRequest request = new com.MusicPlatForm.music_service.dto.kafka_request.TrackRequest();
            request.setTrackId(track.getId());
            request.setDescription(track.getDescription());
            request.setName(track.getTitle());
            kafkaTemplate.send("add_track_to_search", request);            
        } catch (Exception e) {
            // ignore
        }
    }
    private void deleteTrackInKafka(String trackId){
        try {
            kafkaTemplate.send("delete_track_from_search", trackId);
            
        } catch (Exception e) {
            // ignore
        }
    }
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public TrackResponse uploadTrack(MultipartFile coverImage, MultipartFile trackAudio,TrackRequest trackRequest) {
        ApiResponse<AddCoverFileResponse> uploadTrackCoverResponse = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            uploadTrackCoverResponse = fileClient.addCover(coverImage);
        }
        ApiResponse<AddTrackFileResponse> uploadTrackMp3Response = fileClient.addTrack(trackAudio);
        Track track = trackMapper.toTrackFromTrackRequest(trackRequest);
        
        if (uploadTrackCoverResponse != null) {
                track.setCoverImageName(uploadTrackCoverResponse.getData().getCoverName());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        track.setUserId(userId);
        track.setCreatedAt(LocalDateTime.now());
        track.setCountPlay(0);
        track.setDuration(uploadTrackMp3Response.getData().getDuration());
        track.setFileName(uploadTrackMp3Response.getData().getTrack());

        List<Tag> tags = this.tagRepository.findAllById(trackRequest.getTagIds());
        List<TrackTag> trackTags = tags.stream()
                                    .map(tag-> new TrackTag(tag,track))
                                    .collect(Collectors.toList());
        track.setTrackTags(trackTags);
        
        Genre genre = this.genreRepository.findById(trackRequest.getGenreId()).orElse(null);
        track.setGenre(genre);
        Track savedTrack =  trackRepository.save(track);
        this.sendTrackToSearchService(savedTrack);
        TrackResponse trackResponse = this.trackMapper.toTrackResponseFromTrack(savedTrack);
        trackResponse.setTags(this.tagMapper.toTagResponsesFromTags(tags));
        trackResponse.setGenre(this.genreMapper.toGenreResponseFromGenre(genre));
        var userProfile = profileClient.getUserProfile(userId).getData();
        trackResponse.setUser(userProfile);
        return trackResponse;
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public List<TrackResponse> uploadTracks(List<MultipartFile> trackFiles, List<TrackRequest> trackRequests){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<TrackResponse> trackResponses = new ArrayList<>();
        ApiResponse<List<AddTrackFileResponse>> addTrackFilesResponse = fileClient.addTracks(trackFiles);
        int index = 0;
        for(TrackRequest trackRequest: trackRequests){
            Track track = trackMapper.toTrackFromTrackRequest(trackRequest);
            track.setCreatedAt(LocalDateTime.now());
            track.setCountPlay(0);
            track.setDuration(addTrackFilesResponse.getData().get(index).getDuration());
            track.setFileName(addTrackFilesResponse.getData().get(index).getTrack());
            track.setUserId(userId);
            List<Tag> tags = this.tagRepository.findAllById(trackRequest.getTagIds());
            List<TrackTag> trackTags = tags.stream()
                                        .map(tag-> new TrackTag(tag,track))
                                        .collect(Collectors.toList());
            track.setTrackTags(trackTags);
            
            Genre genre = this.genreRepository.findById(trackRequest.getGenreId()).orElse(null);
            track.setGenre(genre);
            Track savedTrack =  trackRepository.save(track);
    
            TrackResponse trackResponse = this.trackMapper.toTrackResponseFromTrack(savedTrack);
            trackResponse.setTags(this.tagMapper.toTagResponsesFromTags(tags));
            trackResponse.setGenre(this.genreMapper.toGenreResponseFromGenre(genre));
            trackResponses.add(trackResponse);
            index++;
        }
        return trackResponses;
    }

    @Override
    public TrackResponse getTrackById(String id) {

        Track track = this.trackRepository.findById(id).orElseThrow();
        
        TrackResponse trackResponse = trackMapper.toTrackResponseFromTrack(track);

        List<Tag> tags = track.getTrackTags().stream().map(trackTag->trackTag.getTag()).toList();
        
        trackResponse.setTags(tagMapper.toTagResponsesFromTags(tags));
        trackResponse.setGenre(genreMapper.toGenreResponseFromGenre(track.getGenre()));

        var userProfile = profileClient.getUserProfile(track.getUserId()).getData();
        trackResponse.setUser(userProfile);

        return trackResponse;
    }

    @Override
    public List<TrackResponse> getTracksByIds(List<String> ids) {
        List<Track> tracks = this.trackRepository.findAllById(ids);
        Map<String,Track> map = new HashMap<>();
        tracks.forEach(t->map.put(t.getId(),t));
        tracks = new LinkedList<>();
        for(String id:ids){
            tracks.add(map.get(id));
        }
        List<String> likedIds =null;
        try {
           likedIds = this.likedTrackClient.filterLikedTrackId(ids).getData(); 
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        Map<String,ProfileWithCountFollowResponse> idToUser = new HashMap<>();
        List<TrackResponse> trackResponses = new ArrayList<>();
        List<String> userIds = new ArrayList<>(); 
        tracks.forEach(t->
        {
            if(!userIds.contains(t.getUserId())){
                userIds.add(t.getUserId());
            }
        }
        );
        

        List<ProfileWithCountFollowResponse> users = profileClient.getUserProfileByIds(userIds).getData();
        users.forEach(u->{
            idToUser.put(u.getUserId(), u);
        });

        for(var track: tracks){
            ProfileWithCountFollowResponse user = idToUser.get(track.getUserId());
            TrackResponse trackResponse = trackMapper.toTrackResponseFromTrack(track);
            List<Tag> tags = track.getTrackTags().stream().map(trackTag->trackTag.getTag()).toList();
            trackResponse.setTags(tagMapper.toTagResponsesFromTags(tags));
            trackResponse.setGenre(genreMapper.toGenreResponseFromGenre(track.getGenre()));
            trackResponse.setUser(user);
            trackResponses.add(trackResponse);
            if(likedIds!=null&&likedIds.contains(track.getId())){
                trackResponse.setLiked(true);
            }
            else{
                trackResponse.setLiked(false);
            }
        }
        return trackResponses;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void deleteTrack(String trackId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Track track = trackRepository.findById(trackId).orElseThrow(()->new AppException(ErrorCode.TRACK_NOT_FOUND));
        if(!track.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);
        trackRepository.delete(track);
        this.deleteTrackInKafka(trackId);
        try {
            fileClient.deleteAudio(track.getFileName());
            if(track.getCoverImageName()!=null)
            fileClient.deleteCover(track.getCoverImageName());
        } catch (Exception e){}
    }

    @Override
    public List<TrackResponse> getTracksByGenre(String genreId,int limit) {
        List<Track> tracks= trackRepository.findRandomTracksByGenre(genreId,limit);
        Map<String,ProfileWithCountFollowResponse> idToUser = new HashMap<>();
        List<String> userIds = new ArrayList<>(); 
        List<TrackResponse> trackResponses = new ArrayList<>();

        tracks.forEach(t->
            {
                if(!userIds.contains(t.getUserId())){
                    userIds.add(t.getUserId());
                }
            }
        );
        List<ProfileWithCountFollowResponse> users = profileClient.getUserProfileByIds(userIds).getData();
        users.forEach(u->{
            idToUser.put(u.getUserId(), u);
        });
        
        for(Track track:tracks){
            ProfileWithCountFollowResponse user = idToUser.get(track.getUserId());
            TrackResponse trackResponse = trackMapper.toTrackResponseFromTrack(track);
            List<Tag> tags = track.getTrackTags().stream().map(trackTag->trackTag.getTag()).toList();
            trackResponse.setTags(tagMapper.toTagResponsesFromTags(tags));
            trackResponse.setGenre(genreMapper.toGenreResponseFromGenre(track.getGenre()));
            trackResponse.setUser(user);
            trackResponses.add(trackResponse);
        }
        return trackResponses;
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public TrackResponse updateTrack(String trackId, UpdateTrackRequest request, MultipartFile imageFile, MultipartFile trackFile) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new AppException(ErrorCode.TRACK_NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(!track.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);

        List<TagResponse> tagResponses = new ArrayList<>();
        GenreResponse genreResponse = null;

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags =  tagRepository.findAllById(request.getTagIds());
            List<TrackTag> trackTags = tags.stream()
                    .map(tag -> new TrackTag(tag, track))
                    .toList();
            if (!trackTags.isEmpty()) {
                track.getTrackTags().clear();
                track.getTrackTags().addAll(trackTags);
                tagResponses = tagMapper.toTagResponsesFromTags(tags);
            }
        }

        if (request.getGenreId() != null && !request.getGenreId().isEmpty()) {
            Genre genre = genreRepository.findById(request.getGenreId())
                    .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));
            track.setGenre(genre);
            genreResponse = genreMapper.toGenreResponseFromGenre(genre);
        }

        trackMapper.updateTrack(track, request);

        if (imageFile != null) {
            ApiResponse<AddCoverFileResponse> response = fileClient.addCover(imageFile);
            track.setCoverImageName(response.getData().getCoverName());
        }

        if (trackFile != null) {
            ApiResponse<AddTrackFileResponse> fileResponse = fileClient.addTrack(trackFile);
            track.setFileName(fileResponse.getData().getTrack());
        }

        TrackResponse response = trackMapper.toTrackResponseFromTrack(trackRepository.save(track));

        if (!tagResponses.isEmpty()) {
            response.setTags(tagResponses);
        }
        if (genreResponse != null) {
            response.setGenre(genreResponse);
        }

        ProfileWithCountFollowResponse user = profileClient.getUserProfile(userId).getData();
        response.setUser(user);

        return response;
    }

    @Override
    public List<TrackResponse> getTracksByUserId(String userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(userId==null || userId==""){
            userId = authentication.getName();
        }
        List<String> likedIds =null;

        List<Track> tracks = this.trackRepository.findTrackByUserId(userId);
        List<String> ids = tracks.stream().map(t->t.getId()).toList();
        try {
           likedIds = this.likedTrackClient.filterLikedTrackId(ids).getData(); 
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        List<TrackResponse> trackResponses = new ArrayList<>();
        ProfileWithCountFollowResponse user = profileClient.getUserProfile(userId).getData();
        for(Track track:tracks){
            TrackResponse trackResponse = trackMapper.toTrackResponseFromTrack(track);
            List<Tag> tags = track.getTrackTags().stream().map(trackTag->trackTag.getTag()).toList();
            trackResponse.setTags(tagMapper.toTagResponsesFromTags(tags));
            trackResponse.setGenre(genreMapper.toGenreResponseFromGenre(track.getGenre()));
            trackResponse.setUser(user);
            trackResponses.add(trackResponse);
            if(likedIds!=null&&likedIds.contains(track.getId())){
                trackResponse.setLiked(true);
            }
        }
        return trackResponses;
    }
    @Override
    public List<TrackResponse> getRandomTracks(int limit) {
        List<Track> tracks = this.trackRepository.findRandomTracks(limit);
        List<TrackResponse> trackResponses = new ArrayList<>();
        Map<String,ProfileWithCountFollowResponse> idToUser = new HashMap<>();
        List<String> userIds = new ArrayList<>(); 
       
        tracks.forEach(t->
            {
                if(!userIds.contains(t.getUserId())){
                    userIds.add(t.getUserId());
                }
            }
        );
        List<ProfileWithCountFollowResponse> users = profileClient.getUserProfileByIds(userIds).getData();
       
        users.forEach(u->{
            idToUser.put(u.getUserId(), u);
        });


        for(Track track:tracks){
            ProfileWithCountFollowResponse user = idToUser.get(track.getUserId());
            TrackResponse trackResponse = trackMapper.toTrackResponseFromTrack(track);
            List<Tag> tags = track.getTrackTags().stream().map(trackTag->trackTag.getTag()).toList();
            trackResponse.setTags(tagMapper.toTagResponsesFromTags(tags));
            trackResponse.setGenre(genreMapper.toGenreResponseFromGenre(track.getGenre()));
            trackResponse.setUser(user);
            trackResponses.add(trackResponse);
        }
        return trackResponses;
    }
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updatePlayCountByTrackId(String trackId) {
        Track track = trackRepository.findById(trackId)
            .orElseThrow(()->new AppException(ErrorCode.TRACK_NOT_FOUND));
        track.setCountPlay(track.getCountPlay()+1);
        trackRepository.save(track);
        return;
    }
}
