package com.devteria.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(101, "Invalid key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED(102, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(103, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(104, "You dont have permission", HttpStatus.FORBIDDEN),
    PROFILE_NOT_FOUND(107, "Profile not found", HttpStatus.NOT_FOUND),
    NOT_FOLLOWING(145, "You don't follow this user!", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWING(144, "Already following!", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWING_YOURSELF(144, "Already following!", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(112, "Email is existed", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private  HttpStatusCode statusCode;

}
