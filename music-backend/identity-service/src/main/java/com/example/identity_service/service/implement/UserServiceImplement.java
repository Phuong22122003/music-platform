package com.example.identity_service.service.implement;

import com.example.identity_service.dto.request.ProfileCreationRequest;
import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.UserMapper;
import com.example.identity_service.mapper.UserProfileMapper;
import com.example.identity_service.repository.ProfileClient;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImplement implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ProfileClient profileClient;
    UserProfileMapper userProfileMapper;
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User newUser = userMapper.toUser(request);
        if(request.getRoles()!=null){
            HashSet<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoles()));
            newUser.setRoles(roles);
        }
        if (newUser.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode((newUser.getPassword())));
        }
        String userId = userRepository.save(newUser).getId();
        ProfileCreationRequest profileCreationRequest = userProfileMapper.toProfileCreationRequest(request);
        LocalDateTime now = LocalDateTime.now();
        profileCreationRequest.setCreatedAt(now);
        profileCreationRequest.setUpdateAt(now);
        profileCreationRequest.setUserId(userId);
        profileClient.createProfile(profileCreationRequest);
        UserResponse result = userMapper.toUserResponse(newUser);
        return result;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In getUsers method");

        // Lấy Authentication object từ SecurityContext
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // In ra principal và authorities
        log.info("Principal: {}", authentication.getPrincipal());
        log.info("Authorities:");
        authentication.getAuthorities().forEach(authority -> log.info(authority.getAuthority()));

        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @PostAuthorize("returnObject.getUsername() == authentication.name")
    public UserResponse getUser(String id) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current username: {}", authentication.getName());
        var res = userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
        log.info("Current res: {}", res.getUsername());
        return res;
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException((ErrorCode.USER_NOT_EXISTED)));
        return  userMapper.toUserResponse(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

}
