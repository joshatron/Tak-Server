package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;

public class Validator {

    private Validator() {
        throw new IllegalStateException("This is a utility class");
    }

    public static void validateAuth(Auth auth) throws GameServerException {
        if(auth == null) {
            throw new GameServerException(ErrorCode.EMPTY_AUTH);
        }
        validateUsername(auth.getUsername());
        validatePassword(auth.getPassword());
    }

    public static void validateText(Text text) throws GameServerException {
        if(text == null || text.getText() == null) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }
    }

    public static void validateUsername(String username) throws GameServerException {
        if(username == null || username.length() == 0) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }

        if(username.matches("^.*[^a-zA-Z0-9 ].*$")) {
            throw new GameServerException(ErrorCode.ALPHANUMERIC_ONLY);
        }
    }

    public static void validatePassword(String password) throws GameServerException {
        if(password == null || password.length() == 0) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }
    }

    public static void validateUserId(String id) throws GameServerException {
        if(id == null || id.length() == 0) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }

        if(id.length() != AccountUtils.ID_LENGTH) {
            throw new GameServerException(ErrorCode.INVALID_LENGTH);
        }

        if(id.matches("^.*[^A-Z0-9 ].*$")) {
            throw new GameServerException(ErrorCode.ALPHANUMERIC_ONLY);
        }
    }
}
