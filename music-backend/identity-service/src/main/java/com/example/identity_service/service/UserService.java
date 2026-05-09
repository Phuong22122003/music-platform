package com.example.identity_service.service;

import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.response.UserResponse;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface UserService {
    @Transactional
    public UserResponse createUser(UserCreationRequest request) ;


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() ;
    @PostAuthorize("returnObject.getUsername() == authentication.name")
    public UserResponse getUser(String id) ;
    public UserResponse getMyInfo();

    public void deleteUser(String id) ;

}
