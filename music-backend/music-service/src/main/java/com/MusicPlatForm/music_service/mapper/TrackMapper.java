package com.MusicPlatForm.music_service.mapper;

import com.MusicPlatForm.music_service.dto.request.UpdateTrackRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.MusicPlatForm.music_service.dto.reponse.TrackResponse;
import com.MusicPlatForm.music_service.dto.request.TrackRequest;
import com.MusicPlatForm.music_service.entity.Track;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "coverImageName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "countPlay", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trackTags", ignore=true) // Map tags thành trackTags
    Track toTrackFromTrackRequest(TrackRequest trackDto);

    @Mapping(target  = "genre", ignore = true )
    @Mapping(target  = "tags", ignore = true )
    TrackResponse toTrackResponseFromTrack(Track track);
    void updateTrack(@MappingTarget Track track, UpdateTrackRequest request);
}
