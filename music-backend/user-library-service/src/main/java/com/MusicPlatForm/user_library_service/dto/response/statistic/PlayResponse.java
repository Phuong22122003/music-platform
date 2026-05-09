package com.MusicPlatForm.user_library_service.dto.response.statistic;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayResponse {
    List<DailyPlay> dailyPlays;
    List<UserStatistic> topListenerIds;
}
