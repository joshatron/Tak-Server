package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.response.User;

import java.sql.Connection;

public class AdminDAOSqlite implements AdminDAO {

    private Connection conn;

    public AdminDAOSqlite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean isAuthenticated(Auth auth) throws GameServerException {
        return false;
    }

    @Override
    public void updatePassword(String newPass) throws GameServerException {

    }

    @Override
    public void banUser(String userId) throws GameServerException {

    }

    @Override
    public void unbanUser(String userId) throws GameServerException {

    }

    @Override
    public User[] searchBannedUsers(String usernameSearch) throws GameServerException {
        return new User[0];
    }

    @Override
    public User[] getBannedUsers() throws GameServerException {
        return new User[0];
    }
}
