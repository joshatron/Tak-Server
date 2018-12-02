package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;

import java.sql.*;

public class SqliteManager {

    private SqliteManager() {
        throw new IllegalStateException("This is a utility class");
    }

    public static Connection getConnection() throws GameServerException {
        String database = "server.db";

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + database);
            initializeDatabase(conn);

            return conn;
        }
        catch(SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        }
    }

    private static void initializeDatabase(Connection conn) throws GameServerException {
        String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id text PRIMARY KEY COLLATE NOCASE," +
                "username text UNIQUE NOT NULL COLLATE NOCASE," +
                "auth text NOT NULL);";

        String friendTable = "CREATE TABLE IF NOT EXISTS friends (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "requester text NOT NULL," +
                "acceptor text NOT NULL);";

        String blockedTable = "CREATE TABLE IF NOT EXISTS blocked (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "requester text NOT NULL," +
                "blocked text NOT NULL);";

        String friendRequestsTable = "CREATE TABLE IF NOT EXISTS friend_requests (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "requester text NOT NULL," +
                "acceptor text NOT NULL);";

        String messagesTable = "CREATE TABLE IF NOT EXISTS messages(" +
                "id text PRIMARY KEY," +
                "sender text NOT NULL," +
                "recipient text NOT NULL," +
                "message text NOT NULL," +
                "time integer NOT NULL," +
                "opened integer NOT NULL);";

        String randomRequestsTable = "CREATE TABLE IF NOT EXISTS random_requests (" +
                "requester text PRIMARY KEY," +
                "size integer NOT NULL);";

        String gameRequestsTable = "CREATE TABLE IF NOT EXISTS game_requests (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "requester text NOT NULL," +
                "acceptor text NOT NULL," +
                "size integer NOT NULL," +
                "white text NOT NULL," +
                "first text NOT NULL);";

        String gamesTable = "CREATE TABLE IF NOT EXISTS games (" +
                //Game ID
                "id text PRIMARY KEY," +
                //User ID of white player
                "white text NOT NULL," +
                //User ID of black player
                "black text NOT NULL," +
                //Board size
                "size integer NOT NULL," +
                //The color who goes first, either WHITE or BLACK
                "first text NOT NULL," +
                //The color who's turn it is, either WHITE or BLACK
                "current text NOT NULL," +
                //How many turns have happened
                "turns integer NOT NULL," +
                //The timestamp of when the game started
                "start integer NOT NULL," +
                //The timestamp of when the game ended, 0 if not done
                "end integer," +
                //Whether the game is done or not, 0 for not done, 1 for done
                "done integer NOT NULL);";

        String turnsTable = "CREATE TABLE IF NOT EXISTS turns (" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "game_id text NOT NULL," +
                "turn_order integer NOT NULL," +
                "turn text NOT NULL);";

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(userTable);
            stmt.executeUpdate(friendTable);
            stmt.executeUpdate(blockedTable);
            stmt.executeUpdate(friendRequestsTable);
            stmt.executeUpdate(messagesTable);
            stmt.executeUpdate(randomRequestsTable);
            stmt.executeUpdate(gameRequestsTable);
            stmt.executeUpdate(gamesTable);
            stmt.executeUpdate(turnsTable);
            stmt.close();

        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        }
    }

    public static void closeStatement(PreparedStatement stmt) throws GameServerException {
        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new GameServerException(ErrorCode.DATABASE_ERROR);
            }
        }
    }

    public static void closeResultSet(ResultSet rs) throws GameServerException {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new GameServerException(ErrorCode.DATABASE_ERROR);
            }
        }
    }
}
