package com.MusicPlatForm.admin_service.dto.client;

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
public class DailyLike {
    LocalDate date;
    Integer likedCount;   
    Map<String, Integer> detailLiked; 
}
