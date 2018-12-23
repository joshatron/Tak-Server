package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.*;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.response.User;
import io.joshatron.tak.server.utils.IdUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAOSqlite implements AccountDAO {

    private Connection conn;

    public AccountDAOSqlite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean isAuthenticated(Auth auth) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getAuth = "SELECT username, auth " +
                "FROM users " +
                "WHERE username = ?;";

        try {
            stmt = conn.prepareStatement(getAuth);
            stmt.setString(1, auth.getUsername());
            rs = stmt.executeQuery();

            return (rs.next() && auth.getUsername().equals(rs.getString("username")) &&
                    BCrypt.checkpw(auth.getPassword(), rs.getString("auth")));
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public void addUser(Auth auth, int idLength) throws GameServerException {
        PreparedStatement stmt = null;

        //insert new user if it isn't
        String insertUser = "INSERT INTO  users (username, auth, id) " +
                "VALUES (?,?,?);";

        try {
            stmt = conn.prepareStatement(insertUser);
            stmt.setString(1, auth.getUsername());
            stmt.setString(2, BCrypt.hashpw(auth.getPassword(), BCrypt.gensalt()));
            stmt.setString(3, IdUtils.generateId(idLength));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void updatePassword(String username, String password) throws GameServerException {
        PreparedStatement stmt = null;

        String changePass = "UPDATE users " +
                "SET auth = ? " +
                "WHERE username = ?;";

        try {
            stmt = conn.prepareStatement(changePass);
            stmt.setString(1, BCrypt.hashpw(password, BCrypt.gensalt()));
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void updateUsername(String oldUsername, String newUsername) throws GameServerException {
        PreparedStatement stmt = null;

        String changeUser = "UPDATE users " +
                "SET username = ? " +
                "WHERE username = ?;";

        try {
            stmt = conn.prepareStatement(changeUser);
            stmt.setString(1, newUsername);
            stmt.setString(2, oldUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public boolean userExists(String userId) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkUsername = "SELECT username " +
                "FROM users " +
                "WHERE id = ?;";

        try {
            stmt = conn.prepareStatement(checkUsername);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public boolean usernameExists(String username) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkUsername = "SELECT username " +
                "FROM users " +
                "WHERE username = ?;";

        try {
            stmt = conn.prepareStatement(checkUsername);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public User getUserFromId(String id) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkUsername = "SELECT id, username " +
                "FROM users " +
                "WHERE id = ?;";

        try {
            stmt = conn.prepareStatement(checkUsername);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("id"));
            }
            else {
                throw new GameServerException(ErrorCode.USER_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public User getUserFromUsername(String username) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkUsername = "SELECT id, username " +
                "FROM users " +
                "WHERE username = ?;";

        try {
            stmt = conn.prepareStatement(checkUsername);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("id"));
            }
            else {
                throw new GameServerException(ErrorCode.USER_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
