package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.response.User;

public interface AdminDAO {

    boolean isAuthenticated(Auth auth) throws GameServerException;
    void updatePassword(String newPass) throws GameServerException;
    void banUser(String userId) throws GameServerException;
    void unbanUser(String userId) throws GameServerException;
    User[] searchBannedUsers(String usernameSearch) throws GameServerException;
    User[] getBannedUsers() throws GameServerException;
}
