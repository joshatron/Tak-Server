package io.joshatron.tak.server.database;

import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Complete;
import io.joshatron.tak.server.request.Pending;
import io.joshatron.tak.server.response.GameInfo;
import io.joshatron.tak.server.response.GameNotifications;
import io.joshatron.tak.server.response.RequestInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    }

    @Override
    public void createRandomGameRequest(String user, int size) throws GameServerException {

    }

    @Override
    public void deleteRandomGameRequest(String user) throws GameServerException {

    }

    @Override
    public void resolveRandomGameRequests() throws GameServerException {

    }

    @Override
    public void startGame(String requester, String other) throws GameServerException {

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
    public boolean requestGame(GameRequest request) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(request.getAuth())) {
            return false;
        }
        //if user blocked, return false
        if(socialDAO.isBlocked(request.getAuth().getUsername(), request.getOpponent())) {
            return false;
        }

        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet checkSet = null;

        //check that request doesn't already exist
        String checkExists = "SELECT requester, acceptor " +
                "FROM game_requests " +
                "WHERE (requester = ? AND acceptor = ?) OR (requester = ? AND acceptor = ?);";

        //Create game request
        String addRequest = "INSERT INTO game_requests (requester,acceptor,size,white,first) " +
                "VALUES (?,?,?,?,?);";

        try {
            checkStmt = conn.prepareStatement(checkExists);
            checkStmt.setInt(1, accountDAO.idFromUsername(request.getAuth().getUsername()));
            checkStmt.setInt(2, accountDAO.idFromUsername(request.getOpponent()));
            checkStmt.setInt(3, accountDAO.idFromUsername(request.getOpponent()));
            checkStmt.setInt(4, accountDAO.idFromUsername(request.getAuth().getUsername()));
            checkSet = checkStmt.executeQuery();

            if (checkSet.next()) {
                return false;
            }

            insertStmt = conn.prepareStatement(addRequest);
            insertStmt.setInt(1, accountDAO.idFromUsername(request.getAuth().getUsername()));
            insertStmt.setInt(2, accountDAO.idFromUsername(request.getOpponent()));
            insertStmt.setInt(3, request.getSize());
            if (request.getColor().toLowerCase().equals("white")) {
                insertStmt.setInt(4, 1);
            } else {
                insertStmt.setInt(4, 0);
            }
            if (request.getFirst().toLowerCase().equals("true")) {
                insertStmt.setInt(5, 1);
            } else {
                insertStmt.setInt(5, 0);
            }
            insertStmt.executeUpdate();

            return false;
        }
        finally {
            if(checkStmt != null) {
                checkStmt.close();
            }
            if(insertStmt != null) {
                insertStmt.close();
            }
            if(checkSet != null) {
                checkSet.close();
            }
        }
    }

    @Override
    public boolean requestRandomGame(RandomGame request) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(request.getAuth())) {
            return false;
        }
        //check if any requests match and if they do create game
        //if nothing matched create request
        return false;
    }

    @Override
    public boolean respondToGame(GameResponse response) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(response.getAuth())) {
            return false;
        }
        //get request and verify everything correct
        //delete request
        //if accept, create game
        return false;
    }

    @Override
    public boolean playTurn(PlayTurn turn) throws Exception {
        //if no auth, return false
        if(!accountDAO.isAuthenticated(turn.getAuth())) {
            return false;
        }
        //verify it is that player's turn
        //verify the move is legal
        //add turn
        return false;
    }

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
