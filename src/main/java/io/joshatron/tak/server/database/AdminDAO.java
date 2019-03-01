package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;

public interface AdminDAO {

    boolean isInitialized() throws GameServerException;
    boolean isAuthenticated(Auth auth) throws GameServerException;
    boolean isUserBanned(String userId) throws GameServerException;
    void updatePassword(String newPass) throws GameServerException;
    void banUser(String userId) throws GameServerException;
    void unbanUser(String userId) throws GameServerException;
}
