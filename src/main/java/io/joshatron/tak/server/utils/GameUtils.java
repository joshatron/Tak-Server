package io.joshatron.tak.server.utils;

import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.GameDAO;
import io.joshatron.tak.server.database.SocialDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.*;
import io.joshatron.tak.server.response.GameInfo;
import io.joshatron.tak.server.response.GameNotifications;
import io.joshatron.tak.server.response.RequestInfo;
import io.joshatron.tak.server.response.User;

import java.util.Date;

public class GameUtils {

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
        Validator.validateGameRequest(gameRequest);
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

        gameDAO.createGameRequest(user.getUserId(), other, gameRequest);
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
            gameDAO.startGame(id, user.getUserId());
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
        return null;
    }

    public Turn[] getTurns(Auth auth, String gameId) throws GameServerException {
        return null;
    }

    public void playTurn(Auth auth, String gameId, Text turn) throws GameServerException {
    }

    public GameNotifications getNotifications(Auth auth) {
        return null;
    }

    public GameInfo[] findGames(Auth auth, String opponents, Date start, Date end, boolean complete, boolean pending, String sizes, String winner, String color) {
        return null;
    }
}
