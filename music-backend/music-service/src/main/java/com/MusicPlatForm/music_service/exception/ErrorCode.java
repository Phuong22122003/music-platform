package com.MusicPlatForm.music_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_KEY(101, "Invalid key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED(102, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(103, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(104, "You dont have permission", HttpStatus.FORBIDDEN),
    PROFILE_NOT_FOUND(107, "Profile not found", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(112, "Email is existed", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWING(144, "Already following!", HttpStatus.BAD_REQUEST),
    NOT_FOLLOWING(145, "You don't follow this user!", HttpStatus.BAD_REQUEST),
    NOT_FOUND(404, "Not found", HttpStatus.NOT_FOUND),
    PLAYLIST_NOT_FOUND(124, "Playlist not found", HttpStatus.NOT_FOUND),
    TRACK_NOT_FOUND(123, "Track not found", HttpStatus.NOT_FOUND),
    ALBUM_TITLE_REQUIRED(126, "Album title is required", HttpStatus.BAD_REQUEST),
    ALBUM_LINK_REQUIRED(127, "Album link is required", HttpStatus.BAD_REQUEST),
    ALBUM_GENRE_ID_REQUIRED(128, "Genre Id is required", HttpStatus.BAD_REQUEST),
    ALBUM_TYPE_REQUIRED(129, "Album type is required", HttpStatus.BAD_REQUEST),
    ALBUM_PRIVACY_REQUIRED(130, "Album privacy is required", HttpStatus.BAD_REQUEST),
    ALBUM_MAIN_ARTIST_REQUIRED(132, "Main artists is required", HttpStatus.BAD_REQUEST),
    ALBUM_PRIVACY_INVALID(131, "Album privacy must be either 'private' or 'public'", HttpStatus.BAD_REQUEST),
    ALBUM_NOT_FOUND(125, "Album not found", HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND(146, "Tag not found", HttpStatus.NOT_FOUND),
    GENRE_NOT_FOUND(148, "Genre not found", HttpStatus.NOT_FOUND),
    ALBUM_ALREADY_LIKED(133, "Album already liked", HttpStatus.NOT_FOUND),
    ALBUM_NOT_LIKED(134, "Album not liked", HttpStatus.NOT_FOUND),
    // TRACK_N0T_FOUND(1021, "Track not found", HttpStatus.NOT_FOUND),
    // ALREADY_LIKED(1022, "Already liked", HttpStatus.BAD_REQUEST),
    // COMMENT_NOT_FOUND(1023,"Comment not found", HttpStatus.NOT_FOUND),
    TAG_EXISTED(147, "Tag existed", HttpStatus.BAD_REQUEST),
    GENRE_EXISTED(149, "Genre existed", HttpStatus.BAD_REQUEST)
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
