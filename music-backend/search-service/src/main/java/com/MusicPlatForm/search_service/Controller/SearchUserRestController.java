package com.MusicPlatForm.search_service.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MusicPlatForm.search_service.Dto.ApiResponse;
import com.MusicPlatForm.search_service.Dto.Request.UserRequest;
import com.MusicPlatForm.search_service.Entity.User;
import com.MusicPlatForm.search_service.Service.UserSearchService;

@RestController
@RequestMapping("/users")
public class SearchUserRestController {
    @Autowired
    private UserSearchService userSearchService;
    

    @KafkaListener(topics = "add_user_to_search", groupId = "search_group")
    public void addUserToSearch(UserRequest userRequest) {
        userSearchService.save(userRequest);
    }
    @KafkaListener(topics = "update_user_to_search", groupId = "search_group")
    public void updateUserToSearch(UserRequest userRequest) {
        userSearchService.save(userRequest);
    }

    @KafkaListener(topics = "delete_user_from_search", groupId = "search_group")
    public void deleteUserFromSearch(String userId) {
        userSearchService.deleteUserByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> addUser(@RequestBody UserRequest user){
        User savedUser = userSearchService.save(user);
        return ResponseEntity.ok().body(
            ApiResponse.<User>builder().code(200).data(savedUser).message("Add user successfully").build()
        );
    }
    @GetMapping("")
    public ResponseEntity<?> searchUsers(@RequestParam(name = "q") String query){
        return ResponseEntity.ok().body(
            ApiResponse.<List<String>>builder().code(200).data(this.userSearchService.searchUsers(query)).message("Successfully").build()
        );
    }
    @GetMapping("/all")
    public List<User> getAllUsers(){
        return userSearchService.getAllUsers();
    }
}
