package com.example.identity_service.service;

import com.example.identity_service.dto.request.RoleRequest;
import com.example.identity_service.dto.response.RoleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {

    public RoleResponse create(RoleRequest request) ;

    public List<RoleResponse> getAll() ;

    public void delete(String name) ;
}
