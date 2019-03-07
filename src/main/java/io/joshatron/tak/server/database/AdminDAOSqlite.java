package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAOSqlite implements AdminDAO {

    private Connection conn;
    private static final String ADMIN_PASS_FIELD = "admin-pass";

    public AdminDAOSqlite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean isInitialized() throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getAuth = "SELECT * " +
                "FROM config " +
                "WHERE field = \"" + ADMIN_PASS_FIELD + "\";";

        try {
            stmt = conn.prepareStatement(getAuth);
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
    public boolean isAuthenticated(Auth auth) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getAuth = "SELECT value " +
                "FROM config " +
                "WHERE field = \"" + ADMIN_PASS_FIELD + "\";";

        try {
            stmt = conn.prepareStatement(getAuth);
            rs = stmt.executeQuery();

            return (rs.next() && auth.getUsername().equals(rs.getString("admin")) &&
                    BCrypt.checkpw(auth.getPassword(), rs.getString("value")));
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public void updatePassword(String newPass) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getAuth = "SELECT * " +
                "FROM config " +
                "WHERE field = \"" + ADMIN_PASS_FIELD + "\";";

        String addAuth = "Update config " +
                "SET value = ? " +
                "WHERE field = \"" + ADMIN_PASS_FIELD + "\";";

        try {
            stmt = conn.prepareStatement(getAuth);
            rs = stmt.executeQuery();

            if(!rs.next()) {
                addAuth = "INSERT into config (field, value) " +
                        "VALUES (\"" + ADMIN_PASS_FIELD + "\", ?);";
            }

            stmt.close();

            stmt = conn.prepareStatement(addAuth);
            stmt.setString(1, newPass);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }
}
