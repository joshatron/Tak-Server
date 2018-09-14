package io.joshatron.tak.server.database;

import io.joshatron.tak.server.request.*;
import io.joshatron.tak.server.response.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SocialDAOSqlite implements SocialDAO {

    private AccountDAOSqlite accountDAO;
    private Connection conn;

    public SocialDAOSqlite() {
        accountDAO = new AccountDAOSqlite();
        conn = DatabaseManager.getConnection();
    }

    public SocialDAOSqlite(AccountDAOSqlite accountDAO, Connection conn) {
        this.accountDAO = accountDAO;
        this.conn = conn;
    }

    @Override
    public boolean createFriendRequest(FriendRequest request) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(request.getAuth())) {
            return false;
        }
        //if user blocked, return false
        if(isBlocked(request.getAuth().getUsername(), request.getFriend())) {
            return false;
        }

        //if friends already exists, return false
        String checkFriends = "SELECT requester, acceptor " +
                "FROM friends " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        PreparedStatement checkFriendsStmt = conn.prepareStatement(checkFriends);
        checkFriendsStmt.setInt(1, accountDAO.idFromUsername(request.getAuth().getUsername()));
        checkFriendsStmt.setInt(2, accountDAO.idFromUsername(request.getFriend()));
        checkFriendsStmt.setInt(3, accountDAO.idFromUsername(request.getFriend()));
        checkFriendsStmt.setInt(4, accountDAO.idFromUsername(request.getAuth().getUsername()));
        ResultSet rs = checkFriendsStmt.executeQuery();

        if(rs.next()) {
            return false;
        }

        //if request already exists, return false
        String checkRequest = "SELECT requester, acceptor " +
                "FROM friend_requests " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        PreparedStatement checkStmt = conn.prepareStatement(checkRequest);
        checkStmt.setInt(1, accountDAO.idFromUsername(request.getAuth().getUsername()));
        checkStmt.setInt(2, accountDAO.idFromUsername(request.getFriend()));
        checkStmt.setInt(3, accountDAO.idFromUsername(request.getFriend()));
        checkStmt.setInt(4, accountDAO.idFromUsername(request.getAuth().getUsername()));
        rs = checkStmt.executeQuery();

        if(rs.next()) {
            return false;
        }

        //add request
        String insertRequest = "INSERT INTO friend_requests (requester, acceptor) " +
                "VALUES (?,?);";

        PreparedStatement insertStmt = conn.prepareStatement(insertRequest);
        insertStmt.setInt(1, accountDAO.idFromUsername(request.getAuth().getUsername()));
        insertStmt.setInt(2, accountDAO.idFromUsername(request.getFriend()));
        insertStmt.executeUpdate();

        return true;
    }

    @Override
    public boolean deleteFriendRequest(CancelFriendRequest request) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(request.getAuth())) {
            return false;
        }

        //if request does not exist, return false
        String checkRequest = "SELECT requester, acceptor " +
                "FROM friend_requests " +
                "WHERE requester = ? AND acceptor = ?;";

        PreparedStatement checkStmt = conn.prepareStatement(checkRequest);
        checkStmt.setInt(1, accountDAO.idFromUsername(request.getAuth().getUsername()));
        checkStmt.setInt(2, accountDAO.idFromUsername(request.getAcceptor()));
        ResultSet rs = checkStmt.executeQuery();

        if(!rs.next()) {
            return false;
        }

        //delete request
        String deleteRequest = "DELETE FROM friend_requests " +
                "WHERE requester = ? AND acceptor = ?;";

        PreparedStatement deleteStmt = conn.prepareStatement(deleteRequest);
        deleteStmt.setInt(1, accountDAO.idFromUsername(request.getAuth().getUsername()));
        deleteStmt.setInt(2, accountDAO.idFromUsername(request.getAcceptor()));
        deleteStmt.executeUpdate();

        return true;
    }

    @Override
    public boolean respondToFriendRequest(FriendResponse response) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(response.getAuth())) {
            return false;
        }

        //if request doesn't exists, return false
        String checkRequest = "SELECT requester, acceptor " +
                "FROM friend_requests " +
                "WHERE requester = ? AND acceptor = ?;";

        PreparedStatement checkStmt = conn.prepareStatement(checkRequest);
        checkStmt.setInt(1, accountDAO.idFromUsername(response.getFriend()));
        checkStmt.setInt(2, accountDAO.idFromUsername(response.getAuth().getUsername()));
        ResultSet rs = checkStmt.executeQuery();

        if(!rs.next()) {
            return false;
        }

        //delete request and if accept add friends
        String deleteRequest = "DELETE FROM friend_requests " +
                "WHERE requester = ? AND acceptor = ?;";

        PreparedStatement deleteStmt = conn.prepareStatement(deleteRequest);
        deleteStmt.setInt(1, accountDAO.idFromUsername(response.getFriend()));
        deleteStmt.setInt(2, accountDAO.idFromUsername(response.getAuth().getUsername()));
        deleteStmt.executeUpdate();

        if(response.getResponse().toLowerCase().equals("accept")) {
            String insertFriend = "INSERT INTO friends (requester, acceptor) " +
                    "VALUES (?,?);";

            PreparedStatement insertStmt = conn.prepareStatement(insertFriend);
            insertStmt.setInt(1, accountDAO.idFromUsername(response.getFriend()));
            insertStmt.setInt(2, accountDAO.idFromUsername(response.getAuth().getUsername()));
            insertStmt.executeUpdate();
        }

        return true;
    }

    @Override
    public boolean blockUser(Block block) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(block.getAuth())) {
            return false;
        }

        //delete any requests and existing friendships
        String deleteRequest = "DELETE FROM friend_requests " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        PreparedStatement deleteRequestStmt = conn.prepareStatement(deleteRequest);
        deleteRequestStmt.setInt(1, accountDAO.idFromUsername(block.getAuth().getUsername()));
        deleteRequestStmt.setInt(2, accountDAO.idFromUsername(block.getBlock()));
        deleteRequestStmt.setInt(3, accountDAO.idFromUsername(block.getBlock()));
        deleteRequestStmt.setInt(4, accountDAO.idFromUsername(block.getAuth().getUsername()));
        deleteRequestStmt.executeUpdate();

        String deleteFriend = "DELETE FROM friends " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        PreparedStatement deleteStmt = conn.prepareStatement(deleteFriend);
        deleteStmt.setInt(1, accountDAO.idFromUsername(block.getAuth().getUsername()));
        deleteStmt.setInt(2, accountDAO.idFromUsername(block.getBlock()));
        deleteStmt.setInt(3, accountDAO.idFromUsername(block.getBlock()));
        deleteStmt.setInt(4, accountDAO.idFromUsername(block.getAuth().getUsername()));
        deleteStmt.executeUpdate();

        //add block
        String insertBlock = "INSERT INTO blocked (requester, blocked) " +
                "VALUES (?,?);";

        PreparedStatement insertStmt = conn.prepareStatement(insertBlock);
        insertStmt.setInt(1, accountDAO.idFromUsername(block.getAuth().getUsername()));
        insertStmt.setInt(2, accountDAO.idFromUsername(block.getBlock()));
        insertStmt.executeUpdate();

        return false;
    }

    @Override
    public boolean unblockUser(Unblock unblock) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(unblock.getAuth())) {
            return false;
        }

        //if block doesn't exist, return false
        if(!isBlocked(unblock.getAuth().getUsername(), unblock.getUnblock())) {
            return false;
        }

        //remove block
        String deleteBlock = "DELETE FROM blocked " +
                "WHERE requester = ? AND blocked = ?;";

        PreparedStatement deleteStmt = conn.prepareStatement(deleteBlock);
        deleteStmt.setInt(1, accountDAO.idFromUsername(unblock.getAuth().getUsername()));
        deleteStmt.setInt(2, accountDAO.idFromUsername(unblock.getUnblock()));
        deleteStmt.executeUpdate();

        return true;
    }

    @Override
    public boolean sendMessage(SendMessage sendMessage) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(sendMessage.getAuth())) {
            return false;
        }
        //if blocked, return false
        if(isBlocked(sendMessage.getAuth().getUsername(), sendMessage.getRecipient())) {
            return false;
        }

        //Add message
        LocalDate date = LocalDate.now();
        String insertRequest = "INSERT INTO messages (from, to, message, time, opened) " +
                "VALUES (?,?,?,?,0);";

        PreparedStatement insertStmt = conn.prepareStatement(insertRequest);
        insertStmt.setInt(1, accountDAO.idFromUsername(sendMessage.getAuth().getUsername()));
        insertStmt.setInt(2, accountDAO.idFromUsername(sendMessage.getRecipient()));
        insertStmt.setString(3, sendMessage.getMessage());
        insertStmt.setString(4, date.toString());
        insertStmt.executeUpdate();

        return false;
    }

    @Override
    public boolean isBlocked(String requester, String other) throws SQLException {
        //if request already exists, return false
        String checkRequest = "SELECT requester, blocked " +
                "FROM blocked " +
                "WHERE requester = ? AND blocked = ?;";

        PreparedStatement checkStmt = conn.prepareStatement(checkRequest);
        checkStmt.setInt(1, accountDAO.idFromUsername(requester));
        checkStmt.setInt(2, accountDAO.idFromUsername(other));
        ResultSet rs = checkStmt.executeQuery();

        if(rs.next()) {
            return true;
        }

        return false;
    }

    @Override
    public String[] listFriends(Auth auth) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(auth)) {
            return null;
        }
        return new String[0];
    }

    @Override
    public String[] listBlocked(Auth auth) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(auth)) {
            return null;
        }
        return new String[0];
    }

    @Override
    public String[] listIncomingFriendRequests(Auth auth) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(auth)) {
            return null;
        }
        return new String[0];
    }

    @Override
    public String[] listOutgoingFriendRequests(Auth auth) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(auth)) {
            return null;
        }
        return new String[0];
    }

    @Override
    public Message[] listMessages(ReadMessages readMessages) throws SQLException {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(readMessages.getAuth())) {
            return null;
        }
        return new Message[0];
    }
}
