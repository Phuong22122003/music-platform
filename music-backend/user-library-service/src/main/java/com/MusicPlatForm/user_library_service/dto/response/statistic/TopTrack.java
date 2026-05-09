package com.MusicPlatForm.user_library_service.dto.response.statistic;

import com.MusicPlatForm.user_library_service.dto.response.client.TrackResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopTrack {
    TrackResponse track;
    Integer playCount;
}
