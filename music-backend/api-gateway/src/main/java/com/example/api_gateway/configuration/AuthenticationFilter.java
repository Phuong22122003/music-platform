package com.example.api_gateway.configuration;

import com.example.api_gateway.dto.ApiResponse;
import com.example.api_gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {
    @NonFinal
    @Value("${app.api-prefix}")
    private String API_PREFIX;
    IdentityService identityService;
    ObjectMapper objectMapper;
    String[] publicEndPoints = {
        "/identity/oauth2/.*",
        "/identity/authenticate/.*",
        "/identity/users/registration",
        "/profile/follows/.*", 
        "/profile/users/.*",
        "/user-library/album/.*", 
        "/music-service/.*", 
        "/file-service/.*",
        "/search-service/.*",
        "/user-library/v3/api-docs", 
        "/swagger-ui/index.html",
        "/swagger-ui/.*",
        "/v3/api-docs/.*",
        "/notification-service/.*",
        "/identity/login/oauth2/code/google"
        };
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (isPublicEndPoint(exchange.getRequest())) {
            return chain.filter(exchange);
        }
        if (CollectionUtils.isEmpty(authHeader)) {
            return  unauthenticated(exchange.getResponse());
        }

        String token = authHeader.getFirst().replace("Bearer ", "");
        return identityService.introspect(token).flatMap(introspectResponseApiResponse -> {
            if (introspectResponseApiResponse.getData().isAuthenticated()) {
                return chain.filter(exchange);
            } else {
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(throwable -> {
            log.info(throwable.getMessage());
            return unauthenticated(exchange.getResponse());
        });

    }

    private boolean isPublicEndPoint(ServerHttpRequest request) {
        log.info(request.getURI().getPath());
        return Arrays.stream(publicEndPoints).anyMatch(ep -> request.getURI().getPath().matches(API_PREFIX + ep));
    }

    @Override
    public int getOrder() {
        return -1;
    }
    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthorized")
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
