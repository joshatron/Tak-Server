package io.joshatron.tak.server.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    //Bad Request
    INVALID_AUTH(HttpStatus.BAD_REQUEST),
    INVALID_LENGTH(HttpStatus.BAD_REQUEST),
    EMPTY_AUTH(HttpStatus.BAD_REQUEST),
    EMPTY_FIELD(HttpStatus.BAD_REQUEST),
    ALPHANUMERIC_ONLY(HttpStatus.BAD_REQUEST),
    INVALID_FORMATTING(HttpStatus.BAD_REQUEST),
    TOO_MANY_ARGUMENTS(HttpStatus.BAD_REQUEST),

    //Forbidden
    USERNAME_TAKEN(HttpStatus.FORBIDDEN),
    SAME_USERNAME(HttpStatus.FORBIDDEN),
    SAME_PASSWORD(HttpStatus.FORBIDDEN),
    BLOCKED(HttpStatus.FORBIDDEN),

    //Bad Auth
    INCORRECT_AUTH(HttpStatus.UNAUTHORIZED),

    //Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),

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
