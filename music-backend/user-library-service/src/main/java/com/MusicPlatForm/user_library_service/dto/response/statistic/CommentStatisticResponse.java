package com.MusicPlatForm.user_library_service.dto.response.statistic;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentStatisticResponse {
    List<DailyComment> dailyComments;
    List<UserStatistic> whoCommented;
}
