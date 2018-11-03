package io.joshatron.tak.server.validation;

import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;
import io.joshatron.tak.server.utils.AccountUtils;

public class AccountValidator {

    private AccountValidator() {
        throw new IllegalStateException("This is a utility class");
    }

    public static void validateAuth(Auth auth) throws GameServerException {
        if(auth == null) {
            throw new GameServerException(ErrorCode.EMPTY_AUTH);
        }
        validateUsername(auth.getUsername());
        validatePassword(auth.getPassword());
    }

    public static void validatePassChange(Auth auth, Text passChange) throws GameServerException {
        if(passChange == null) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }
        validateAuth(auth);
        validatePassword(passChange.getText());
    }

    public static void validateUserChange(Auth auth, Text userChange) throws GameServerException {
        if(userChange == null) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }
        validateAuth(auth);
        validateUsername(userChange.getText());
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
