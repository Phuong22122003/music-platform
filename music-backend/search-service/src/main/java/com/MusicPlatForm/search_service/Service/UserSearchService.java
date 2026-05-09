package com.MusicPlatForm.search_service.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MusicPlatForm.search_service.Dto.Request.UserRequest;
import com.MusicPlatForm.search_service.Entity.User;
import com.MusicPlatForm.search_service.Mapper.UserResponseMapper;
import com.MusicPlatForm.search_service.Repository.UserRepository;

@Service
public class UserSearchService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserResponseMapper userResponseMapper;

    public UserSearchService(UserRepository userRepository){
        this.userRepository = userRepository;
    }   
    public List<String> searchUsers(String query){
        List<User> users  =  this.userRepository.findUsers(query);
        return userResponseMapper.toUserIds(users);
    }
    public List<User> getAllUsers(){
        return (List<User>) this.userRepository.findAll();
    }

    public User save(UserRequest userRequest){
        User user = new User();
        user.setDisplayName(userRequest.getDisplayName());
        user.setFullname(userRequest.getFullname());
        user.setUserId(userRequest.getUserId());
        User savedUser = this.userRepository.save(user);
        return savedUser;
    }
    public void update(UserRequest userRequest){
        User user = this.userRepository.findUserByUserId(userRequest.getUserId());
        if(user==null) return ;
        user.setFullname(user.getFullname());
        user.setDisplayName(user.getDisplayName());
        this.userRepository.save(user);
    }

    public void deleteUserByUserId(String userId) {
        userRepository.deleteByUserId(userId);
    }
}
