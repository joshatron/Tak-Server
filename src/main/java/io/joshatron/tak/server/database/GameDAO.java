package io.joshatron.tak.server.database;

import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.server.response.GameInfo;
import io.joshatron.tak.server.response.GameNotifications;
import io.joshatron.tak.server.response.RequestInfo;

public interface GameDAO {
    void createGameRequest(String requester, String other, int size, Player requesterColor, Player first);
    void deleteGameRequest(String requester, String other);
    void createRandomGameRequest(String user, int size);
    void deleteRandomGameRequest(String user);
    void resolveRandomGameRequests();
    void startGame(String requester, String other);
    void addTurn(String gameId, String text);
    void finishGame(String gameId, Player winner);
    boolean playingGame(String requester, String other);
    boolean gameRequestExists(String requester, String other);
    boolean randomGameRequestExists(String user);
    boolean gameExists(String gameId);
    boolean userAuthorizedForGame(String user, String gameId);
    boolean isYourTurn(String userId, String gameId);
    RequestInfo[] getIncomingGameRequests(String user);
    RequestInfo[] getOutgoingGameRequests(String user);
    int getOutgoingRandomRequestSize(String user);
    GameInfo getGameInfo(String gameId);
    GameNotifications getGameNotifications(String userId);
}
