package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.From;
import io.joshatron.tak.server.request.Read;
import io.joshatron.tak.server.response.Message;
import io.joshatron.tak.server.response.SocialNotifications;
import io.joshatron.tak.server.response.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class SocialDAOSqlite implements SocialDAO {

    private Connection conn;

    public SocialDAOSqlite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean friendRequestExists(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkRequest = "SELECT requester, acceptor " +
                "FROM friend_requests " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        try {
            stmt = conn.prepareStatement(checkRequest);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.setString(3, other);
            stmt.setString(4, requester);
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
    public boolean areFriends(String user1, String user2) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkFriends = "SELECT requester, acceptor " +
                "FROM friends " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        try {
            stmt = conn.prepareStatement(checkFriends);
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);
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
    public boolean isBlocked(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkBlocked = "SELECT requester " +
                "FROM blocked " +
                "WHERE (requester = ? AND blocked = ?);";

        try {
            stmt = conn.prepareStatement(checkBlocked);
            stmt.setString(1, requester);
            stmt.setString(2, other);
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
    public void createFriendRequest(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;

        String insertRequest = "INSERT INTO friend_requests (requester, acceptor) " +
                "VALUES (?,?);";

        try {
            stmt = conn.prepareStatement(insertRequest);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void deleteFriendRequest(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;

        String deleteRequest = "DELETE FROM friend_requests " +
                "WHERE requester = ? AND acceptor = ?;";

        try {
            stmt = conn.prepareStatement(deleteRequest);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void makeFriends(String user1, String user2) throws GameServerException {
        PreparedStatement stmt = null;

        String insertFriend = "INSERT INTO friends (requester, acceptor) " +
                "VALUES (?,?);";

        try {
            stmt = conn.prepareStatement(insertFriend);
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void unfriend(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;

        String deleteFriend = "DELETE FROM friends " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        try {
            stmt = conn.prepareStatement(deleteFriend);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.setString(3, other);
            stmt.setString(4, requester);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void block(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;

        String insertBlock = "INSERT INTO blocked (requester, blocked) " +
                "VALUES (?,?);";

        try {
            stmt = conn.prepareStatement(insertBlock);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void unblock(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;

        String deleteBlock = "DELETE FROM blocked " +
                "WHERE requester = ? AND blocked = ?;";

        try {
            stmt = conn.prepareStatement(deleteBlock);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void sendMessage(String requester, String other, String text) throws GameServerException {
        PreparedStatement stmt = null;

        String insertRequest = "INSERT INTO messages (sender, recipient, message, time, opened) " +
                "VALUES (?,?,?,?,0);";

        try {
            stmt = conn.prepareStatement(insertRequest);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.setString(3, text);
            stmt.setLong(4, Instant.now().toEpochMilli());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void markMessageRead(String id) throws GameServerException {
        PreparedStatement stmt = null;

        String markRead = "UPDATE messages " +
                "SET read = 1 " +
                "WHERE id = ?;";

        try {
            stmt = conn.prepareStatement(markRead);
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void markAllRead(String user) throws GameServerException {
        PreparedStatement stmt = null;

        String markRead = "UPDATE messages " +
                "SET read = 1 " +
                "WHERE recipient = ?;";

        try {
            stmt = conn.prepareStatement(markRead);
            stmt.setString(1, user);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public User[] getIncomingFriendRequests(String user) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getIncoming = "SELECT users.username as username, requester " +
                "FROM friend_requests " +
                "LEFT OUTER JOIN users on friend_requests.requester = users.id " +
                "WHERE acceptor = ?;";

        try {
            stmt = conn.prepareStatement(getIncoming);
            stmt.setString(1, user);
            rs = stmt.executeQuery();

            ArrayList<User> users = new ArrayList<>();
            while(rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("requester")));
            }

            return users.toArray(new User[users.size()]);
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public User[] getOutgoingFriendRequests(String user) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getOutgoing = "SELECT users.username as username, acceptor " +
                "FROM friend_requests " +
                "LEFT OUTER JOIN users on friend_requests.acceptor = users.id " +
                "WHERE requester = ?;";

        try {
            stmt = conn.prepareStatement(getOutgoing);
            stmt.setString(1, user);
            rs = stmt.executeQuery();

            ArrayList<User> users = new ArrayList<>();
            while(rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("acceptor")));
            }

            return users.toArray(new User[users.size()]);
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public User[] getFriends(String user) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getIncoming = "SELECT users.username as username, requester " +
                "FROM friends " +
                "LEFT OUTER JOIN users on friends.requester = users.id " +
                "WHERE acceptor = ?;";
        String getOutgoing = "SELECT users.username as username, acceptor " +
                "FROM friends " +
                "LEFT OUTER JOIN users on friends.acceptor = users.id " +
                "WHERE requester = ?;";

        try {
            ArrayList<User> users = new ArrayList<>();

            stmt = conn.prepareStatement(getIncoming);
            stmt.setString(1, user);
            rs = stmt.executeQuery();

            while(rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("requester")));
            }
            rs.close();

            stmt = conn.prepareStatement(getOutgoing);
            stmt.setString(1, user);
            rs = stmt.executeQuery();

            while(rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("acceptor")));
            }

            return users.toArray(new User[users.size()]);
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public User[] getBlocked(String user) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String getIncoming = "SELECT users.username as username, requester " +
                "FROM blocked " +
                "LEFT OUTER JOIN users on blocked.requester = users.id " +
                "WHERE blocked = ?;";
        String getOutgoing = "SELECT users.username as username, blocked " +
                "FROM blocked " +
                "LEFT OUTER JOIN users on friends.blocked = users.id " +
                "WHERE requester = ?;";

        try {
            ArrayList<User> users = new ArrayList<>();

            stmt = conn.prepareStatement(getIncoming);
            stmt.setString(1, user);
            rs = stmt.executeQuery();

            while(rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("requester")));
            }
            rs.close();

            stmt = conn.prepareStatement(getOutgoing);
            stmt.setString(1, user);
            rs = stmt.executeQuery();

            while(rs.next()) {
                users.add(new User(rs.getString("username"), rs.getString("acceptor")));
            }

            return users.toArray(new User[users.size()]);
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public Message[] listMessages(String userId, String[] users, Date start, Date end, Read read, From from) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        StringBuilder getMessage = new StringBuilder();
        getMessage.append("SELECT id, sender, recipient, message, time, opened ");
        getMessage.append("FROM messages ");
        getMessage.append("WHERE id = ?");

        if(users != null && users.length > 0) {
            getMessage.append(" AND (");
            boolean first = true;
            for (int i = 0; i < users.length; i++) {
                if(first) {
                    getMessage.append("sender = ?");
                    first = false;
                }
                else {
                    getMessage.append(" OR sender = ?");
                }
            }
            getMessage.append(")");
        }

        if(start != null) {
            getMessage.append(" AND time > ?");
        }

        if(end != null) {
            getMessage.append(" AND time < ?");
        }

        if(read != Read.BOTH) {
            if(read == Read.READ) {
                getMessage.append(" AND read = 1");
            }
            else {
                getMessage.append(" AND read = 0");
            }
        }

        getMessage.append(";");

        try {
            ArrayList<Message> messages = new ArrayList<>();

            stmt = conn.prepareStatement(getMessage.toString());
            stmt.setString(1, userId);
            int i = 2;
            if(users != null && users.length > 0) {
                for(String user : users) {
                    stmt.setString(i, user);
                    i++;
                }
            }
            if(start != null) {
                stmt.setLong(i, start.getTime());
                i++;
            }
            if(end != null) {
                stmt.setLong(i, end.getTime());
                i++;
            }
            rs = stmt.executeQuery();

            while(rs.next()) {
                messages.add(new Message(rs.getString("sender"), rs.getString("recipient"), new Date(rs.getLong("time")),
                        rs.getString("message"), rs.getString("id"), (rs.getInt("opened") != 0)));
            }

            return messages.toArray(new Message[messages.size()]);
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public Message getMessage(String messageId) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String checkUsername = "SELECT id, sender, recipient, message, time, opened " +
                "FROM messages " +
                "WHERE id = ?;";

        try {
            stmt = conn.prepareStatement(checkUsername);
            stmt.setString(1, messageId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Message(rs.getString("sender"), rs.getString("recipient"), new Date(rs.getLong("time")),
                                   rs.getString("message"), rs.getString("id"), (rs.getInt("opened") != 0));
            }
            else {
                throw new GameServerException(ErrorCode.MESSAGE_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }

    @Override
    public SocialNotifications getSocialNotifications(String userId) throws GameServerException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String countRequests = "SELECT COUNT(*) AS total " +
                "FROM friend_requests " +
                "WHERE acceptor = ?;";
        String countMessages = "SELECT COUNT(*) AS total " +
                "FROM messages " +
                "WHERE recipient = ? AND opened = 0;";

        try {
            stmt = conn.prepareStatement(countRequests);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();
            int requests = rs.getInt("total");
            rs.close();

            stmt = conn.prepareStatement(countRequests);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();
            int messages = rs.getInt("total");

            return new SocialNotifications(requests, messages);
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
            SqliteManager.closeResultSet(rs);
        }
    }
}
