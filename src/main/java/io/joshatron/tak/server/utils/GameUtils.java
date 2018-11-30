package io.joshatron.tak.server.utils;

import io.joshatron.tak.engine.game.GameResult;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnUtils;
import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.GameDAO;
import io.joshatron.tak.server.database.SocialDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.*;
import io.joshatron.tak.server.response.*;

import java.util.ArrayList;
import java.util.Date;

public class GameUtils {

    public static final int GAME_ID_LENGTH = 25;

    private GameDAO gameDAO;
    private SocialDAO socialDAO;
    private AccountDAO accountDAO;

    public GameUtils(GameDAO gameDAO, SocialDAO socialDAO, AccountDAO accountDAO) {
        this.gameDAO = gameDAO;
        this.socialDAO = socialDAO;
        this.accountDAO = accountDAO;
    }

    public void requestGame(Auth auth, String other,GameRequest gameRequest) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        Validator.validateGameBoardSize(gameRequest.getSize());
        Player requesterColor = Validator.validatePlayer(gameRequest.getRequesterColor());
        Player first = Validator.validatePlayer(gameRequest.getFirst());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!socialDAO.areFriends(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.ALREADY_FRIENDS);
        }
        if(gameDAO.playingGame(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.GAME_EXISTS);
        }
        if(gameDAO.gameRequestExists(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.GAME_REQUEST_EXISTS);
        }

        gameDAO.createGameRequest(user.getUserId(), other, gameRequest.getSize(), requesterColor, first);
    }

    public void deleteRequest(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!gameDAO.gameRequestExists(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        gameDAO.deleteGameRequest(user.getUserId(), other);
    }

    public void respondToGame(Auth auth, String id, Text answer) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(id, AccountUtils.USER_ID_LENGTH);
        Validator.validateText(answer);
        Answer response = Validator.validateAnswer(answer.getText());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(id)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!gameDAO.gameRequestExists(id, user.getUserId())) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        if(response == Answer.ACCEPT) {
            RequestInfo info = gameDAO.getGameRequestInfo(id, user.getUserId());
            gameDAO.startGame(id, user.getUserId(), info.getSize(), info.getRequesterColor(), info.getFirst());
        }
        gameDAO.deleteGameRequest(id, user.getUserId());
    }

    public RequestInfo[] checkIncomingRequests(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return gameDAO.getIncomingGameRequests(user.getUserId());
    }

    public RequestInfo[] checkOutgoingRequests(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return gameDAO.getOutgoingGameRequests(user.getUserId());
    }

    public void requestRandomGame(Auth auth, int size) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateGameBoardSize(size);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(gameDAO.randomGameRequestExists(user.getUserId())) {
            throw new GameServerException(ErrorCode.GAME_REQUEST_EXISTS);
        }

        gameDAO.createRandomGameRequest(user.getUserId(), size);
        gameDAO.resolveRandomGameRequests();
    }

    public void deleteRandomRequest(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!gameDAO.randomGameRequestExists(user.getUserId())) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        gameDAO.deleteRandomGameRequest(user.getUserId());
    }

    public int checkRandomSize(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!gameDAO.randomGameRequestExists(user.getUserId())) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        return gameDAO.getOutgoingRandomRequestSize(user.getUserId());
    }

    public GameInfo getGameInfo(Auth auth, String gameId) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(gameId, GAME_ID_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!gameDAO.gameExists(gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.userAuthorizedForGame(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.CANT_ACCESS_GAME);
        }

        return gameDAO.getGameInfo(gameId);
    }

    public GameInfo[] findGames(Auth auth, String opponents, Date start, Date end, String complete, String pending, String sizes, String winner, String color) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        String[] users = null;
        if(opponents != null && opponents.length() > 0) {
            users = opponents.split(",");
            for (String u : users) {
                Validator.validateId(u, AccountUtils.USER_ID_LENGTH);
                if (!accountDAO.userExists(u)) {
                    throw new GameServerException(ErrorCode.USER_NOT_FOUND);
                }
            }
        }
        if(start != null && end != null && start.after(end)) {
            throw new GameServerException(ErrorCode.INVALID_DATE);
        }
        Complete cpt = Validator.validateComplete(complete);
        Pending pnd = Validator.validatePending(pending);
        int[] szs = null;
        if(sizes != null && sizes.length() > 0) {
            String[] all = sizes.split(",");
            szs = new int[all.length];
            for(int i = 0; i < all.length; i++) {
                int size = Integer.parseInt(all[i]);
                Validator.validateGameBoardSize(size);
                szs[i] = size;
            }
        }
        Player wnr = Validator.validatePlayer(winner);
        Player clr = Validator.validatePlayer(color);

        return gameDAO.listGames(user.getUserId(), users, start, end, cpt, pnd, szs, wnr, clr);
    }

    public String[] getPossibleTurns(Auth auth, String gameId) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(gameId, GAME_ID_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!gameDAO.gameExists(gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.userAuthorizedForGame(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.CANT_ACCESS_GAME);
        }

        GameState state = getStateFromId(gameId);

        ArrayList<Turn> possible = state.getPossibleTurns();
        String[] toReturn = new String[possible.size()];
        for(int i = 0; i < toReturn.length; i++) {
            toReturn[i] = possible.get(i).toString();
        }

        return toReturn;
    }

    public void playTurn(Auth auth, String gameId, Text turn) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(gameId, GAME_ID_LENGTH);
        Validator.validateText(turn);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!gameDAO.gameExists(gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.userAuthorizedForGame(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.CANT_ACCESS_GAME);
        }
        if(!gameDAO.isYourTurn(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.NOT_YOUR_TURN);
        }

        GameState state = getStateFromId(gameId);
        Turn proposed = TurnUtils.turnFromString(turn.getText());
        if(proposed == null) {
            throw new GameServerException(ErrorCode.INVALID_FORMATTING);
        }

        if(!state.executeTurn(proposed)) {
            throw new GameServerException(ErrorCode.ILLEGAL_MOVE);
        }

        gameDAO.addTurn(gameId, turn.getText());

        GameResult result = state.checkForWinner();
        if(result.isFinished()) {
            gameDAO.finishGame(gameId, result.getWinner());
        }
    }

    public GameNotifications getNotifications(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return gameDAO.getGameNotifications(user.getUserId());
    }

    private GameState getStateFromId(String gameId) throws GameServerException {
        GameInfo gameInfo = gameDAO.getGameInfo(gameId);

        Player player = gameInfo.getFirst();
        GameState state = new GameState(player, gameInfo.getSize());
        for(String turn : gameInfo.getTurns()) {
            Turn toPlay = TurnUtils.turnFromString(turn);
            state.executeTurn(toPlay);
        }

        return state;
    }
}
