package com.example.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(101, "Invalid key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED(102, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(103, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(104, "You dont have permission", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(105, "Token is invalid or expire", HttpStatus.BAD_REQUEST),
    USER_ID_REQUIRED(106, "User Id is required!", HttpStatus.BAD_REQUEST),
    USER_EXISTED(108, "User existed!", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(109, "User not existed", HttpStatus.NOT_FOUND),
    USERNAME_MIN(110, "Username must be at least {min} character", HttpStatus.BAD_REQUEST),
    DISPLAY_NAME_REQUIRED(111, "Display name required", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(112, "Email existed!", HttpStatus.CONFLICT),
    EMAIL_REQUIRED(113, "Email is required!", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(114, "Email is invalid", HttpStatus.BAD_REQUEST),
    OTP_INVALID(114, "OTP is invalid", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(115, "Password is wrong!", HttpStatus.BAD_REQUEST),
    PASSWORD_MIN(116, "Password must be at least {min} character", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_REQUIRED(117, "Old password is required!", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_REQUIRED(118, "New password is required!", HttpStatus.BAD_REQUEST),
    CONFIRM_NEW_PASSWORD_REQUIRED(119, "Confirm new password is required!", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_NOT_MATCH(120, "New password not match!", HttpStatus.BAD_REQUEST),
    DOB_INVALID(121, "Day of birth must be at least {min}", HttpStatus.BAD_REQUEST),
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
