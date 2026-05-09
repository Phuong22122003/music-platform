package com.MusicPlatForm.user_library_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.CommentStatisticResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.DailyLike;
import com.MusicPlatForm.user_library_service.dto.response.statistic.DailyPlay;
import com.MusicPlatForm.user_library_service.dto.response.statistic.LikeResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.PlayResponse;
import com.MusicPlatForm.user_library_service.dto.response.statistic.TopTrack;
import com.MusicPlatForm.user_library_service.dto.response.statistic.UserStatistic;
import com.MusicPlatForm.user_library_service.entity.History;
import com.MusicPlatForm.user_library_service.entity.LikedTrack;
import com.MusicPlatForm.user_library_service.httpclient.CommentClient;
import com.MusicPlatForm.user_library_service.httpclient.MusicClient;
import com.MusicPlatForm.user_library_service.repository.HistoryRepository;
import com.MusicPlatForm.user_library_service.repository.LikedTrackRepository;
import com.MusicPlatForm.user_library_service.service.iface.StatisticServiceInterface;

@Service
public class StatisticViewService implements StatisticServiceInterface {

    private HistoryRepository historyRepository;
    private LikedTrackRepository likedTrackRepository;
    private MusicClient musicClient;
    private CommentClient commentClient;
    public StatisticViewService(HistoryRepository historyRepository,
        MusicClient musicClient,
        LikedTrackRepository likedTrackRepository,
        CommentClient commentClient){
        this.historyRepository = historyRepository;
        this.musicClient = musicClient;
        this.likedTrackRepository = likedTrackRepository;
        this.commentClient = commentClient;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public PlayResponse getPlays(LocalDate fromDate, LocalDate toDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<TrackResponse> tracks = musicClient.getTracksByUserId(userId).getData();
        List<String> trackIds = tracks.stream().map(TrackResponse::getId).toList();

        List<History> histories ;
        if(fromDate==null) histories = historyRepository.findAllHistoryOfTracksByTrackIds(trackIds);
        else histories = historyRepository.findHistoryOfTracksFromDateToDateByTrackIds(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX), trackIds);
        Map<LocalDate, DailyPlay> groupPlayByDate = new HashMap<>();
        Map<String, Integer> countListenerIds = new HashMap<>();

        for (History history : histories) {
            LocalDate date = history.getListenedAt().toLocalDate();
            String trackId = history.getTrackId();
            int count = history.getCount();
            String listenerId = history.getUserId();

            // Cập nhật hoặc tạo mới DailyPlay cho ngày
            DailyPlay dailyPlay = groupPlayByDate.computeIfAbsent(date, d -> {
                DailyPlay dp = new DailyPlay();
                dp.setDate(d);
                dp.setPlayCount(0);
                dp.setDetailPlay(new HashMap<>());
                return dp;
            });

            // Cộng dồn số lần nghe tổng của ngày
            dailyPlay.setPlayCount(dailyPlay.getPlayCount() + count);

            // Cập nhật số lần nghe track trong ngày
            dailyPlay.getDetailPlay().merge(trackId, count, Integer::sum);

            // Cập nhật số lần xuất hiện của listener
            countListenerIds.merge(listenerId, 1, Integer::sum);
        }

        List<DailyPlay> dailyPlays = groupPlayByDate.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .toList();

        // Sắp xếp top user giảm dần theo số lần xuất hiện
        List<UserStatistic> topUserIds = countListenerIds.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(u->{
                return new UserStatistic(u.getKey(),u.getValue());
            })
            .toList();

        PlayResponse playResponse = new PlayResponse();
        playResponse.setDailyPlays(dailyPlays);
        playResponse.setTopListenerIds(topUserIds);

        return playResponse;
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public LikeResponse getLiked(LocalDate fromDate, LocalDate toDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<TrackResponse> tracks = musicClient.getTracksByUserId(userId).getData();
        List<String> trackIds = tracks.stream().map(TrackResponse::getId).toList();
        List<LikedTrack> likedTracks ;
        if(fromDate==null) 
        likedTracks = likedTrackRepository.findAllLikedTrackByTrackIds(trackIds);
        else likedTracks = this.likedTrackRepository.findLikedTrackFromDateToDateByTrackIds(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX), trackIds);
        Map<LocalDate, DailyLike> groupLikedByDate = new HashMap<>();
        Map<String, Integer> userLikedCounts = new HashMap<>();
        for(var likedTrack:likedTracks){
            LocalDate date = likedTrack.getLikedAt().toLocalDate();
            String trackId = likedTrack.getTrackId();
            DailyLike dailyLike = groupLikedByDate.computeIfAbsent(date, d -> {
                DailyLike dl = new DailyLike();
                dl.setDate(d);
                dl.setLikedCount(0);
                dl.setDetailLiked(new HashMap<>());
                return dl;
            });
            dailyLike.setLikedCount(dailyLike.getLikedCount()+1);
            dailyLike.getDetailLiked().merge(trackId, 1, Integer::sum);
            userLikedCounts.merge(likedTrack.getUserId(), 1, Integer::sum);   
        }
        List<DailyLike> dailyLikes = groupLikedByDate.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(l->l.getValue())
            .toList();
        List<UserStatistic> userStatistics = userLikedCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(u->{
                return new UserStatistic(u.getKey(),u.getValue());
            }).toList();
        LikeResponse likeResponse = new LikeResponse();
        likeResponse.setDailyLikes(dailyLikes);
        likeResponse.setWhoLiked(userStatistics);
        return likeResponse;
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public CommentStatisticResponse getComments(LocalDate fromDate, LocalDate toDate) {
        return commentClient.getCommentStatistic(fromDate, toDate).getData();
    }
   

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public PlayResponse getAllPlays(LocalDate fromDate, LocalDate toDate) {
        List<History> histories = historyRepository.findAllHistoryFromDateToDate(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));
        Map<LocalDate, DailyPlay> groupPlayByDate = new HashMap<>();
        Map<String, Integer> countListenerIds = new HashMap<>();

        for (History history : histories) {
            LocalDate date = history.getListenedAt().toLocalDate();
            String trackId = history.getTrackId();
            int count = history.getCount();
            String listenerId = history.getUserId();

            // Cập nhật hoặc tạo mới DailyPlay cho ngày
            DailyPlay dailyPlay = groupPlayByDate.computeIfAbsent(date, d -> {
                DailyPlay dp = new DailyPlay();
                dp.setDate(d);
                dp.setPlayCount(0);
                dp.setDetailPlay(new HashMap<>());
                return dp;
            });

            // Cộng dồn số lần nghe tổng của ngày
            dailyPlay.setPlayCount(dailyPlay.getPlayCount() + count);

            // Cập nhật số lần nghe track trong ngày
            dailyPlay.getDetailPlay().merge(trackId, count, Integer::sum);

            // Cập nhật số lần xuất hiện của listener
            countListenerIds.merge(listenerId, 1, Integer::sum);
        }

        List<DailyPlay> dailyPlays = groupPlayByDate.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .toList();

        // Sắp xếp top user giảm dần theo số lần xuất hiện
        List<UserStatistic> topUserIds = countListenerIds.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(u->{
                return new UserStatistic(u.getKey(),u.getValue());
            })
            .toList();

        PlayResponse playResponse = new PlayResponse();
        playResponse.setDailyPlays(dailyPlays);
        playResponse.setTopListenerIds(topUserIds);

        return playResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public LikeResponse getAllLiked(LocalDate fromDate, LocalDate toDate) {
        List<LikedTrack> likedTracks ;
        if(fromDate!=null)likedTracks = this.likedTrackRepository.findAllLikedTrackFromDateToDate(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));
        else likedTracks = this.likedTrackRepository.findAllLikedTrack();
        Map<LocalDate, DailyLike> groupLikedByDate = new HashMap<>();
        Map<String, Integer> userLikedCounts = new HashMap<>();
        for(var likedTrack:likedTracks){
            LocalDate date = likedTrack.getLikedAt().toLocalDate();
            String trackId = likedTrack.getTrackId();
            DailyLike dailyLike = groupLikedByDate.computeIfAbsent(date, d -> {
                DailyLike dl = new DailyLike();
                dl.setDate(d);
                dl.setLikedCount(0);
                dl.setDetailLiked(new HashMap<>());
                return dl;
            });
            dailyLike.setLikedCount(dailyLike.getLikedCount()+1);
            dailyLike.getDetailLiked().merge(trackId, 1, Integer::sum);
            userLikedCounts.merge(likedTrack.getUserId(), 1, Integer::sum);   
        }
        List<DailyLike> dailyLikes = groupLikedByDate.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(l->l.getValue())
            .toList();
        List<UserStatistic> userStatistics = userLikedCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(u->{
                return new UserStatistic(u.getKey(),u.getValue());
            }).toList();
        LikeResponse likeResponse = new LikeResponse();
        likeResponse.setDailyLikes(dailyLikes);
        likeResponse.setWhoLiked(userStatistics);
        return likeResponse;
    }

    @Override
    public List<TopTrack> getUserTopTracks(String userId,LocalDate fromDate, LocalDate toDate) {
        List<TrackResponse> tracks = musicClient.getTracksByUserId(userId).getData();
        List<String> trackIds = tracks.stream().map(TrackResponse::getId).toList();
        List<History> histories ;
        if(fromDate!=null)histories = historyRepository.findHistoryOfTracksFromDateToDateByTrackIds(fromDate.atStartOfDay(),toDate.atTime(LocalTime.MAX), trackIds);
        else histories = historyRepository.findAllByUserIdOrderByListenedAtDesc(userId);
        Map<String, Integer> topTracksCount = new HashMap<>();
        Map<String,TrackResponse> searchTrackHelper = new HashMap<>();
        // tính số lược nghe
        for(var h: histories){
            topTracksCount.merge(h.getTrackId(), 1, Integer::sum);
        }
        // tạo search track để thuận lời cho tìm track
        for(var track:tracks){
            searchTrackHelper.put(track.getId(), track);
        }

        // xóa những track không có người xem
        topTracksCount.entrySet().removeIf(t->t.getValue()==0);
        
        List<TopTrack> topTracks = topTracksCount.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(t->{
            TrackResponse track = searchTrackHelper.get(t.getKey());
            return new TopTrack(track,t.getValue());
        }).toList();
        return topTracks;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<TopTrack> getTopTracks(LocalDate fromDate, LocalDate toDate) {
        List<History> histories = historyRepository.findAllHistoryFromDateToDate(fromDate.atStartOfDay(),toDate.atTime(LocalTime.MAX));
        Map<String, Integer> topTracksCount = new HashMap<>();
        Map<String,TrackResponse> searchTrackHelper = new HashMap<>();
        for(var h: histories){
            topTracksCount.merge(h.getTrackId(), 1, Integer::sum);
        }
        // xóa track ko có người xem
        topTracksCount.entrySet().removeIf(t->t.getValue()==0);
        // lấy ids của những track có người xem
        List<String> trackIds =topTracksCount.entrySet().stream().map(t->t.getKey()).toList();
        List<TrackResponse> tracks = musicClient.getTrackByIds(trackIds).getData();
        // tạo dữ liệu cho search
        for(var track:tracks){
            searchTrackHelper.put(track.getId(), track);
        }
        List<TopTrack> topTracks = topTracksCount.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(t->{
            TrackResponse track = searchTrackHelper.get(t.getKey());
            return new TopTrack(track,t.getValue());
        }).toList();
        return topTracks;
    }
}



 // @PreAuthorize("isAuthenticated()")
    // @Override
    // public CommentStatisticResponse getComments(LocalDate fromDate, LocalDate toDate) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String userId = authentication.getName();
    //     List<TrackResponse> tracks = musicClient.getTracksByUserId(userId).getData();
    //     List<String> trackIds = tracks.stream().map(TrackResponse::getId).toList();
    //     List<CommentResponse> comments = this.commentClient.getComments(fromDate, toDate, trackIds).getData();
    //     Map<LocalDate,DailyComment> groupCommentByDate= new HashMap<>();
    //     Map<String, Integer> userCommentCounts = new HashMap<>();
    //     for(var comment:comments){
    //         LocalDate date = comment.getCommentAt().toLocalDate();
    //         String trackId = comment.getTrackId();
    //         String commentUserId = comment.getUserId();
    //         DailyComment dailyComment = groupCommentByDate.computeIfAbsent(date, d -> {
    //             DailyComment dc = new DailyComment();
    //             dc.setDate(d);
    //             dc.setCommentCount(0);
    //             dc.setDetailComment(new HashMap<>());
    //             return dc;
    //         });
    //         dailyComment.setCommentCount(dailyComment.getCommentCount()+1);
    //         dailyComment.getDetailComment().merge(trackId, 1, Integer::sum);
    //         userCommentCounts.merge(commentUserId, null, Integer::sum);  
    //     }
    //     List<DailyComment> dailyComments = groupCommentByDate
    //         .entrySet()
    //         .stream()
    //         .sorted(Map.Entry.comparingByKey())
    //         .map(c->c.getValue())
    //         .toList();
    //     List<UserStatistic> userStatistics = userCommentCounts
    //         .entrySet()
    //         .stream()
    //         .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
    //         .map(u->{
    //             return new UserStatistic(u.getKey(),u.getValue());
    //         }).toList();
    //     CommentStatisticResponse commentStatisticResponse = new CommentStatisticResponse();
    //     commentStatisticResponse.setDailyComments(dailyComments);
    //     commentStatisticResponse.setWhoCommented(userStatistics);
    //     return commentStatisticResponse;
    // }