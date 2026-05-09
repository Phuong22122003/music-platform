package com.devteria.profile.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.devteria.profile.dto.kafka.UserRequest;
import com.devteria.profile.entity.UserProfile;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaService {
    KafkaTemplate<String,Object> kafkaTemplate;
    public void addUserToSearchService(UserProfile userpProfile){
        UserRequest userRequest =UserRequest.builder()
                                    .userId(userpProfile.getUserId())
                                    .fullname(userpProfile.getLastName()+" "+userpProfile.getFirstName())
                                    .displayName(userpProfile.getDisplayName())
                                    .build();
        this.kafkaTemplate.send("add_user_to_search",userRequest);
    }
    public void updateUserToSearchService(UserProfile userpProfile){
        UserRequest userRequest =UserRequest.builder()
                                    .userId(userpProfile.getUserId())
                                    .fullname(userpProfile.getLastName()+""+userpProfile.getLastName())
                                    .displayName(userpProfile.getDisplayName())
                                    .build();
        this.kafkaTemplate.send("update_user_to_search",userRequest);
    }
    public void deleteUserFromSearch(String userId){
        this.kafkaTemplate.send("delete_user_from_search",userId);
    }
}
