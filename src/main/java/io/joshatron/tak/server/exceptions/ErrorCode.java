package io.joshatron.tak.server.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    //Bad Request
    INVALID_AUTH(HttpStatus.BAD_REQUEST),
    INVALID_LENGTH(HttpStatus.BAD_REQUEST),
    INVALID_FORMATTING(HttpStatus.BAD_REQUEST),
    INVALID_DATE(HttpStatus.BAD_REQUEST),
    EMPTY_AUTH(HttpStatus.BAD_REQUEST),
    EMPTY_FIELD(HttpStatus.BAD_REQUEST),
    ALPHANUMERIC_ONLY(HttpStatus.BAD_REQUEST),
    TOO_MANY_ARGUMENTS(HttpStatus.BAD_REQUEST),
    ILLEGAL_SIZE(HttpStatus.BAD_REQUEST),
    ILLEGAL_COLOR(HttpStatus.BAD_REQUEST),
    ILLEGAL_MOVE(HttpStatus.BAD_REQUEST),
    MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST),

    //Forbidden
    USERNAME_TAKEN(HttpStatus.FORBIDDEN),
    SAME_USERNAME(HttpStatus.FORBIDDEN),
    SAME_PASSWORD(HttpStatus.FORBIDDEN),
    BLOCKED(HttpStatus.FORBIDDEN),
    ALREADY_FRIENDS(HttpStatus.FORBIDDEN),
    ALREADY_REQUESTING(HttpStatus.FORBIDDEN),
    ALREADY_BLOCKED(HttpStatus.FORBIDDEN),
    GAME_EXISTS(HttpStatus.FORBIDDEN),
    GAME_REQUEST_EXISTS(HttpStatus.FORBIDDEN),
    CANT_ACCESS_GAME(HttpStatus.FORBIDDEN),
    NOT_YOUR_TURN(HttpStatus.FORBIDDEN),
    REQUESTING_SELF(HttpStatus.FORBIDDEN),
    BLOCKING_SELF(HttpStatus.FORBIDDEN),
    NOT_YOUR_MESSAGE(HttpStatus.FORBIDDEN),

    //Bad Auth
    INCORRECT_AUTH(HttpStatus.UNAUTHORIZED),

    //Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND),
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND),
    NOT_BLOCKED(HttpStatus.NOT_FOUND),

    //Internal Error
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);



    private HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
