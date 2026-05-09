package com.MusicPlatForm.admin_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(9998, "Invalid key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "You dont have permission", HttpStatus.FORBIDDEN),
    PROFILE_NOT_FOUND(1006, "Profile not found", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(1007, "Email is existed", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWING(1008, "Already following!", HttpStatus.BAD_REQUEST),
    NOT_FOLLOWING(1009, "You don't follow this user!", HttpStatus.BAD_REQUEST),
    NOT_FOUND(404, "Not found", HttpStatus.NOT_FOUND),
    PLAYLIST_NOT_FOUND(404, "Playlist not found", HttpStatus.NOT_FOUND),
    ALBUM_TITLE_REQUIRED(1010, "Album title is required", HttpStatus.BAD_REQUEST),
    ALBUM_LINK_REQUIRED(1011, "Album link is required", HttpStatus.BAD_REQUEST),
    ALBUM_GENRE_ID_REQUIRED(1012, "Genre Id is required", HttpStatus.BAD_REQUEST),
    ALBUM_TYPE_REQUIRED(1013, "Album type is required", HttpStatus.BAD_REQUEST),
    ALBUM_PRIVACY_REQUIRED(1014, "Album privacy is required", HttpStatus.BAD_REQUEST),
    ALBUM_MAIN_ARTIST_REQUIRED(1015, "Main artists is required", HttpStatus.BAD_REQUEST),
    ALBUM_PRIVACY_INVALID(1016, "Album privacy must be either 'private' or 'public'", HttpStatus.BAD_REQUEST),
    ALBUM_NOT_FOUND(1017, "Album not found", HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND(1018, "Tag not found", HttpStatus.NOT_FOUND),
    ALBUM_ALREADY_LIKED(1019, "Album already liked", HttpStatus.NOT_FOUND),
    ALBUM_NOT_LIKED(1020, "Album not liked", HttpStatus.NOT_FOUND),
    TRACK_N0T_FOUND(1021, "Track not found", HttpStatus.NOT_FOUND),
    ALREADY_LIKED(1022, "Already liked", HttpStatus.BAD_REQUEST),
    ALBUM_LINK_EXISTED(1023, "Album link existed", HttpStatus.BAD_REQUEST)
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
