package com.MusicPlatForm.user_library_service.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.user_library_service.dto.response.client.GenreResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.ProfileWithCountFollowResponse;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.entity.History;
import com.MusicPlatForm.user_library_service.httpclient.MusicClient;
import com.MusicPlatForm.user_library_service.httpclient.ProfileClient;
import com.MusicPlatForm.user_library_service.repository.HistoryRepository;
import com.MusicPlatForm.user_library_service.service.iface.RecommendedServiceInterface;


@Service
  
public class RecommendedService implements RecommendedServiceInterface{

    @Value("${track.related.limit}")
    private int relatedTrackLimit;// trong 1 related track thì lấy bao nhiêu track

    @Value("${history.top-recentlyplayed.limit}")
    private int recentlyPlayedLimit;// số lượng track vừa nghe gần đây

    @Value("${history.size-of-list}")
    private int listSize;// số lượng related track đề xuất


    private MusicClient musicClient;
    private ProfileClient profileClient;
    private HistoryRepository historyRepository;
    private LikedTrackService likedTrackService;
    public RecommendedService(MusicClient musicClient,
        HistoryRepository historyRepository,
        LikedTrackService likedTrackService,
        ProfileClient profileClient
    ){
        this.profileClient = profileClient;
        this.musicClient = musicClient;
        this.historyRepository = historyRepository;
        this.likedTrackService = likedTrackService;
    }
    @Override
    public List<TrackResponse> getMixedForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<String> likedTrackIds = likedTrackService.getLikedTrack().stream().map(l->l.getTrackId()).toList();
        List<History> histories = this.historyRepository.findTopRecentlyPlayed(recentlyPlayedLimit,userId);
        Collections.shuffle(histories);
        List<History> list = histories.subList(0, Math.min(histories.size(), listSize));
        List<String> topIds = list.stream()
                                        .map(History::getTrackId)
                                        .distinct()
                                        .toList();

        List<TrackResponse> mixFor = musicClient.getTrackByIds(topIds).getData();
        mixFor.forEach(m->m.setIsLiked(likedTrackIds.contains(m.getId())));
        return mixFor;
    }

    

    @Override
    public Map<String, List<TrackResponse>> getGroupedTrackByGenres() {
        List<GenreResponse> genres = musicClient.getGenres().getData();
        Map<String, List<TrackResponse>> tracksGroupedByGenre = new HashMap<>();
        List<String> likedTrackIds = likedTrackService.getLikedTrack().stream().map(l->l.getTrackId()).toList();
        List<TrackResponse> trackList;
        for(var genre:genres){
            trackList = musicClient.getTracksByGenre(genre.getId(), listSize).getData();
            trackList.forEach(t->{
                t.setIsLiked(likedTrackIds.contains(t.getId()));
            });
            if(trackList.size()>0){
                tracksGroupedByGenre.put(genre.getName(), trackList);
            }
        }
        return tracksGroupedByGenre;
    }




    @Override
    public List<TrackResponse> getRelatedTrack(String trackId) {
        TrackResponse track = musicClient.getTrackById(trackId).getData();
        List<TrackResponse> trackResponses = null;
        if(track.getGenre()==null){
            trackResponses = likedTrackService.getAllLikedTracks();
        }
        else{
            trackResponses= musicClient.getTracksByGenre(track.getGenre().getId(), relatedTrackLimit).getData();
        }
        if(trackResponses.size()==0){
            trackResponses = musicClient.getRandomTracks(relatedTrackLimit).getData();
        }
        // trackResponses.removeIf(tr->tr.getId()==trackId);
        return trackResponses;
    }
    @Override
    public List<TrackResponse> getMoreOfWhatYouLike() {
        List<TrackResponse> likedTracks = likedTrackService.getAllLikedTracks();
        Collections.shuffle(likedTracks);
        // likedTracks.removeIf(t->t.getGenre()==null);
        return likedTracks;
    }
    
    public List<ProfileWithCountFollowResponse> getArtirstsShouldKnow(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<ProfileWithCountFollowResponse> artirsts =null;
        // đã đăng nhập
        if(authentication.isAuthenticated()){
            List<TrackResponse> tracks = likedTrackService.getAllLikedTracks();
            List<String> artirtsIds = tracks.stream().map(a->a.getUserId()).distinct().toList();    
            artirsts = this.profileClient.getUserProfileByIds(artirtsIds).getData();
            artirsts.removeIf(a->a.getUserId().equals(authentication.getName()));
        }
        // đã đăng nhập nhưng chưa thích gì hoặc thích mỗi bài hát của mình hoặc chưa đăng nhập 
        if(artirsts==null||artirsts.size()==0){
            artirsts =this.profileClient.getTopFollowedUser(listSize).getData();
        }
        // xóa đi chính user đó nếu dc đề xuất
        if(authentication.isAuthenticated()){
            artirsts.removeIf(a->a.getUserId().equals(authentication.getName()));
        }
        return artirsts;
    }
    @Override
    public List<TrackResponse> getTrendingTracksByGenre(String genreId) {
        var trackResponses= musicClient.getTracksByGenre(genreId, relatedTrackLimit).getData();
        Set<String> likedTrackIds = likedTrackService.getLikedTrack().stream().map(l->l.getTrackId()).collect(Collectors.toSet());
        trackResponses.forEach(t-> t.setIsLiked(likedTrackIds.contains(t.getId())));
        return trackResponses;
    }

}
