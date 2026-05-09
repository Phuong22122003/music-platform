package com.MusicPlatForm.search_service.Mapper;


import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.MusicPlatForm.search_service.Dto.Request.TrackRequest;
import com.MusicPlatForm.search_service.Entity.Track;

@Mapper(componentModel = "spring")
public interface TrackResponseMapper {
    default String mapTrackToTrackId(Track track) {
        return track.getTrackId();
    }

    List<String> toTrackIds(List<Track> tracks);
    
   
}
