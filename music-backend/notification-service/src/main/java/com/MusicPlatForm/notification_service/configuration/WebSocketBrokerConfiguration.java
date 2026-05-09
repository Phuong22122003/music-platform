package com.MusicPlatForm.notification_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(@SuppressWarnings("null") MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/queue");
        registry.setApplicationDestinationPrefixes("/app");//=> prefix để send giống như RequestMapping đặt ở đầu class controller
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void registerStompEndpoints(@SuppressWarnings("null") StompEndpointRegistry registry){
        registry
                .addEndpoint("/ws-notification")
                .setHandshakeHandler(new UserHandShake())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration){
        registration.interceptors(new UserInterceptor());
    }
}