package com.MusicPlatForm.notification_service.configuration;

import java.security.Principal;
import java.util.Map;

import com.MusicPlatForm.notification_service.model.User;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

// chỉ cho phép user đã đăng mới vào dc
public class UserHandShake extends DefaultHandshakeHandler{
    private CustomJwtDecoder jwtDecoder = new CustomJwtDecoder();
    @PreAuthorize("isAuthenticated()")
    @Override
    protected Principal determineUser(ServerHttpRequest request,WebSocketHandler wsHandler,Map<String, Object> attributes){
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        String token = servletRequest.getServletRequest().getParameter("token");
        String userId = jwtDecoder.decode(token).getSubject();
        return new User(userId);
    }
}