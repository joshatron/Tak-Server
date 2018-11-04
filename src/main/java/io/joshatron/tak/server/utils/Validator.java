package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Answer;
import io.joshatron.tak.server.request.MarkRead;
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

    public static void validateId(String id, int length) throws GameServerException {
        if(id == null || id.length() == 0) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }

        if(id.length() != length) {
            throw new GameServerException(ErrorCode.INVALID_LENGTH);
        }

        if(id.matches("^.*[^A-Z0-9 ].*$")) {
            throw new GameServerException(ErrorCode.ALPHANUMERIC_ONLY);
        }
    }

    public static Answer validateAnswer(String response) throws GameServerException {
        if(response == null || response.length() == 0) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }

        if(response.equalsIgnoreCase("accept")) {
            return Answer.ACCEPT;
        }
        else if(response.equalsIgnoreCase("deny")) {
            return Answer.DENY;
        }

        throw new GameServerException(ErrorCode.INVALID_FORMATTING);
    }

    public static void validateMarkRead(MarkRead markRead) throws GameServerException {
        if(markRead == null) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }

        if(markRead.getStart() != null && markRead.getIds() != null) {
            throw new GameServerException(ErrorCode.TOO_MANY_ARGUMENTS);
        }

        if(markRead.getIds() != null) {
            for (String id : markRead.getIds()) {
                validateId(id, SocialUtils.MESSAGE_ID_LENGTH);
            }
        }
    }
}
