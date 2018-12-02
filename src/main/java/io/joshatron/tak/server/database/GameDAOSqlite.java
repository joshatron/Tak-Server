package io.joshatron.tak.server.database;

import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Complete;
import io.joshatron.tak.server.request.Pending;
import io.joshatron.tak.server.response.GameInfo;
import io.joshatron.tak.server.response.GameNotifications;
import io.joshatron.tak.server.response.RequestInfo;
import io.joshatron.tak.server.utils.GameUtils;
import io.joshatron.tak.server.utils.IdUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

public class GameDAOSqlite implements GameDAO {

    Connection conn;

    public GameDAOSqlite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void createGameRequest(String requester, String other, int size, Player requesterColor, Player first) throws GameServerException {
        PreparedStatement stmt = null;

        String insertRequest = "INSERT INTO game_requests (requester, acceptor, size, white, first) " +
                "VALUES (?,?,?,?,?);";

        try {
            stmt = conn.prepareStatement(insertRequest);
            stmt.setString(1, requester);
            stmt.setString(2, other);
            stmt.setInt(3, size);
            if(requesterColor == Player.WHITE) {
                stmt.setString(4, requester);
                if(first == Player.WHITE) {
                    stmt.setString(5, requester);
                }
                else {
                    stmt.setString(5, other);
                }
            }
            else {
                stmt.setString(4, other);
                if(first == Player.WHITE) {
                    stmt.setString(5, other);
                }
                else {
                    stmt.setString(5, requester);
                }
            }
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void deleteGameRequest(String requester, String other) throws GameServerException {
        PreparedStatement stmt = null;

        String deleteRequest = "DELETE FROM game_requests " +
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
    public void createRandomGameRequest(String user, int size) throws GameServerException {
        PreparedStatement stmt = null;

        String insertRequest = "INSERT INTO random_requests (requester, size) " +
                "VALUES (?,?);";

        try {
            stmt = conn.prepareStatement(insertRequest);
            stmt.setString(1, user);
            stmt.setInt(2, size);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void deleteRandomGameRequest(String user) throws GameServerException {
        PreparedStatement stmt = null;

        String deleteRequest = "DELETE FROM random_requests " +
                "WHERE requester = ?;";

        try {
            stmt = conn.prepareStatement(deleteRequest);
            stmt.setString(1, user);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void resolveRandomGameRequests() throws GameServerException {

    }

    @Override
    public void startGame(String requester, String other, int size, Player requesterColor, Player first) throws GameServerException {
        PreparedStatement stmt = null;

        String insertRequest = "INSERT INTO games (id, white, black, size, first, current, turns, start, end, done) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?);";

        try {
            stmt = conn.prepareStatement(insertRequest);
            stmt.setString(1, IdUtils.generateId(GameUtils.GAME_ID_LENGTH));
            if(requesterColor == Player.WHITE) {
                stmt.setString(2, requester);
                stmt.setString(3, other);
            }
            else {
                stmt.setString(2, other);
                stmt.setString(3, requester);
            }
            stmt.setInt(4, size);
            stmt.setString(5, first.name());
            stmt.setString(6, first.name());
            stmt.setInt(7, 0);
            stmt.setLong(8, Instant.now().toEpochMilli());
            stmt.setInt(9, 0);
            stmt.setInt(10, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new GameServerException(ErrorCode.DATABASE_ERROR);
        } finally {
            SqliteManager.closeStatement(stmt);
        }
    }

    @Override
    public void addTurn(String gameId, String text) throws GameServerException {

    }

    @Override
    public void finishGame(String gameId, Player winner) throws GameServerException {

    }

    @Override
    public boolean playingGame(String requester, String other) throws GameServerException {
        return false;
    }

    @Override
    public boolean gameRequestExists(String requester, String other) throws GameServerException {
        return false;
    }

    @Override
    public boolean randomGameRequestExists(String user) throws GameServerException {
        return false;
    }

    @Override
    public boolean gameExists(String gameId) throws GameServerException {
        return false;
    }

    @Override
    public boolean userAuthorizedForGame(String user, String gameId) throws GameServerException {
        return false;
    }

    @Override
    public boolean isYourTurn(String userId, String gameId) throws GameServerException {
        return false;
    }

    @Override
    public RequestInfo getGameRequestInfo(String requester, String other) throws GameServerException {
        return null;
    }

    @Override
    public RequestInfo[] getIncomingGameRequests(String user) throws GameServerException {
        return new RequestInfo[0];
    }

    @Override
    public RequestInfo[] getOutgoingGameRequests(String user) throws GameServerException {
        return new RequestInfo[0];
    }

    @Override
    public int getOutgoingRandomRequestSize(String user) throws GameServerException {
        return 0;
    }

    @Override
    public GameInfo getGameInfo(String gameId) throws GameServerException {
        return null;
    }

    @Override
    public GameInfo[] listGames(String userId, String[] opponents, Date start, Date end, Complete complete, Pending pending, int[] sizes, Player winner, Player color) throws GameServerException {
        return new GameInfo[0];
    }

    @Override
    public GameNotifications getGameNotifications(String userId) throws GameServerException {
        return null;
    }

    /*
    THIS IS LEGACY CODE: DELETE AFTER CREATING NEW METHODS
    @Override
    public RequestInfo[] checkIncomingGames(Auth auth) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(auth)) {
            return null;
        }

        PreparedStatement requestStmt = null;
        ResultSet requestSet = null;

        //select all requests and convert to request infos
        String incoming = "SELECT users.username as username, size, white, first " +
                "FROM game_requests " +
                "LEFT OUTER JOIN users on game_requests.requester = users.id " +
                "WHERE acceptor = ?;";

        try {
            requestStmt = conn.prepareStatement(incoming);
            requestStmt.setInt(1, accountDAO.idFromUsername(auth.getUsername()));
            requestSet = requestStmt.executeQuery();

            ArrayList<RequestInfo> requests = new ArrayList<>();

            while (requestSet.next()) {
                String user = requestSet.getString("username");
                boolean white = requestSet.getInt("white") == 0;
                boolean first = requestSet.getInt("first") == 0;
                int size = requestSet.getInt("size");
                requests.add(new RequestInfo(user, white, first, size));
            }

            return requests.toArray(new RequestInfo[requests.size()]);
        }
        finally {
            if(requestStmt != null) {
                requestStmt.close();
            }
            if(requestSet != null) {
                requestSet.close();
            }
        }
    }

    @Override
    public RequestInfo[] checkOutgoingGames(Auth auth) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(auth)) {
            return null;
        }

        PreparedStatement requestStmt = null;
        ResultSet requestSet = null;

        //select all requests and convert to request infos
        String incoming = "SELECT users.username as username, size, white, first " +
                "FROM game_requests " +
                "LEFT OUTER JOIN users on game_requests.acceptor = users.id " +
                "WHERE requester = ?;";

        try {
            requestStmt = conn.prepareStatement(incoming);
            requestStmt.setInt(1, accountDAO.idFromUsername(auth.getUsername()));
            requestSet = requestStmt.executeQuery();

            ArrayList<RequestInfo> requests = new ArrayList<>();

            while (requestSet.next()) {
                String user = requestSet.getString("username");
                boolean white = requestSet.getInt("white") == 1;
                boolean first = requestSet.getInt("first") == 1;
                int size = requestSet.getInt("size");
                requests.add(new RequestInfo(user, white, first, size));
            }

            return requests.toArray(new RequestInfo[requests.size()]);
        }
        finally {
            if(requestStmt != null) {
                requestStmt.close();
            }
            if(requestSet != null) {
                requestSet.close();
            }
        }
    }

    @Override
    public int[] listCompletedGames(ListCompleted completed) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(completed.getAuth())) {
            return null;
        }

        PreparedStatement gameStmt = null;
        ResultSet gameSet = null;

        //find all completed games and get ids
        String games = "SELECT id " +
                "FROM games " +
                "WHERE done = 1 AND (white = ? or black = ?);";

        try {
            gameStmt = conn.prepareStatement(games);
            gameStmt.setInt(1, accountDAO.idFromUsername(completed.getAuth().getUsername()));
            gameStmt.setInt(2, accountDAO.idFromUsername(completed.getAuth().getUsername()));
            gameSet = gameStmt.executeQuery();

            ArrayList<Integer> ints = new ArrayList<>();

            while (gameSet.next()) {
                ints.add(gameSet.getInt("id"));
            }

            int[] toReturn = new int[ints.size()];

            for (int i = 0; i < toReturn.length; i++) {
                toReturn[i] = ints.get(i).intValue();
            }

            return toReturn;
        }
        finally {
            if(gameStmt != null) {
                gameStmt.close();
            }
            if(gameSet != null) {
                gameSet.close();
            }
        }
    }

    @Override
    public int[] listIncompleteGames(ListIncomplete incomplete) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(incomplete.getAuth())) {
            return null;
        }

        PreparedStatement gameStmt = null;
        ResultSet gameSet = null;

        //find all completed games and get ids
        String games = "SELECT id " +
                "FROM games " +
                "WHERE done = 0 AND (white = ? or black = ?);";

        try {
            gameStmt = conn.prepareStatement(games);
            gameStmt.setInt(1, accountDAO.idFromUsername(incomplete.getAuth().getUsername()));
            gameStmt.setInt(2, accountDAO.idFromUsername(incomplete.getAuth().getUsername()));
            gameSet = gameStmt.executeQuery();

            ArrayList<Integer> ints = new ArrayList<>();

            while (gameSet.next()) {
                ints.add(gameSet.getInt("id"));
            }

            int[] toReturn = new int[ints.size()];

            for (int i = 0; i < toReturn.length; i++) {
                toReturn[i] = ints.get(i).intValue();
            }

            return toReturn;
        }
        finally {
            if(gameStmt != null) {
                gameStmt.close();
            }
            if(gameSet != null) {
                gameSet.close();
            }
        }
    }

    @Override
    public GameInfo getGame(GetGame game) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(game.getAuth())) {
            return null;
        }
        //make sure user is a player
        if(!authorizedForGame(game.getAuth().getUsername(), game.getId())) {
            return null;
        }

        PreparedStatement gameStmt = null;
        PreparedStatement turnStmt = null;
        ResultSet gameSet = null;
        ResultSet turnSet = null;

        //get info on game
        GameInfo gameInfo = new GameInfo();
        String getGame = "SELECT white, black, size, first, start, end, done " +
                "FROM games " +
                "WHERE id = ?;";

        //get turns for game
        String getTurn = "SELECT turn_order, turn " +
                "FROM turns " +
                "WHERE game_id = ? " +
                "ORDER BY turn_order ASC;";

        try {
            gameStmt = conn.prepareStatement(getGame);
            gameStmt.setString(1, game.getId());
            gameSet = gameStmt.executeQuery();
            if (gameSet.next()) {
                String white = accountDAO.usernameFromId(gameSet.getInt("white"));
                String black = accountDAO.usernameFromId(gameSet.getInt("black"));
                int size = gameSet.getInt("size");
                String first = gameSet.getInt("first") == 0 ? "white" : "black";
                String start = gameSet.getString("start");
                String end = gameSet.getString("end");
                boolean done = gameSet.getInt("done") == 1;
                gameInfo = new GameInfo(white, black, size, first, start, end, done);
            }

            turnStmt = conn.prepareStatement(getTurn);
            turnStmt.setString(1, game.getId());
            turnSet = turnStmt.executeQuery();
            ArrayList<GameTurn> turns = new ArrayList<>();
            while (turnSet.next()) {
                turns.add(new GameTurn(turnSet.getString("turn"), turnSet.getInt("turn_order")));
            }

            gameInfo.setTurns(turns.toArray(new GameTurn[turns.size()]));

            return gameInfo;
        }
        finally {
            if(gameStmt != null) {
                gameStmt.close();
            }
            if(turnStmt != null) {
                turnStmt.close();
            }
            if(gameSet != null) {
                gameSet.close();
            }
            if(turnSet != null) {
                turnSet.close();
            }
        }
    }

    private boolean authorizedForGame(String username, String gameID) throws Exception {
        String getGame = "SELECT white, black " +
                "FROM games " +
                "WHERE id = ?;";

        PreparedStatement gameStmt = null;
        ResultSet gameSet = null;

        try {
            gameStmt = conn.prepareStatement(getGame);
            gameStmt.setString(1, gameID);
            gameSet = gameStmt.executeQuery();

            if (gameSet.next()) {
                int userID = accountDAO.idFromUsername(username);

                if (userID == gameSet.getInt("white") || userID == gameSet.getInt("black")) {
                    return true;
                }
            }

            return false;
        }
        finally {
            if(gameStmt != null) {
                gameStmt.close();
            }
            if(gameSet != null) {
                gameSet.close();
            }
        }
    }*/
}
