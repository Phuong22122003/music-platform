package com.MusicPlatForm.user_library_service.exception;

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
    PLAYLIST_NOT_FOUND(124, "Playlist not found", HttpStatus.NOT_FOUND),
    ALBUM_TITLE_REQUIRED(126, "Album title is required", HttpStatus.BAD_REQUEST),
    ALBUM_LINK_REQUIRED(127, "Album link is required", HttpStatus.BAD_REQUEST),
    ALBUM_GENRE_ID_REQUIRED(128, "Genre Id is required", HttpStatus.BAD_REQUEST),
    ALBUM_TYPE_REQUIRED(129, "Album type is required", HttpStatus.BAD_REQUEST),
    ALBUM_PRIVACY_REQUIRED(130, "Album privacy is required", HttpStatus.BAD_REQUEST),
    ALBUM_MAIN_ARTIST_REQUIRED(132, "Main artists is required", HttpStatus.BAD_REQUEST),
    ALBUM_PRIVACY_INVALID(131, "Album privacy must be either 'private' or 'public'", HttpStatus.BAD_REQUEST),
    ALBUM_NOT_FOUND(125, "Album not found", HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND(146, "Tag not found", HttpStatus.NOT_FOUND),
    ALBUM_ALREADY_LIKED(133, "Album already liked", HttpStatus.NOT_FOUND),
    ALBUM_NOT_LIKED(134, "Album not liked", HttpStatus.NOT_FOUND),
    TRACK_NOT_FOUND(123, "Track not found", HttpStatus.NOT_FOUND),
    ALBUM_LINK_EXISTED(135, "Album link existed", HttpStatus.BAD_REQUEST),
    ALREADY_LIKED(136, "Already liked", HttpStatus.BAD_REQUEST),
    ALREADY_UNLIKED(137, "Already unliked", HttpStatus.BAD_REQUEST),
    TRACK_IN_PLAYLIST_NOT_FOUND(138,"Track in playlist not found",HttpStatus.NOT_FOUND),
    INVALID_PRIVACY(139,"Privacy must be either 'private' or 'public'",HttpStatus.NOT_FOUND)
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
