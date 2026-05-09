package com.MusicPlatForm.comment_service.dto.response;

import java.time.LocalDate;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyComment {
    LocalDate date;
    Integer commentCount;   
    Map<String, Integer> detailComment; 
}
