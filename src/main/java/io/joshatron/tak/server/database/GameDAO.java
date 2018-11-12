package io.joshatron.tak.server.database;

import io.joshatron.tak.server.request.GameRequest;
import io.joshatron.tak.server.response.GameInfo;
import io.joshatron.tak.server.response.GameNotifications;
import io.joshatron.tak.server.response.RequestInfo;

public interface GameDAO {
    void createGameRequest(String requester, String other, GameRequest gameRequest);
    void deleteGameRequest(String requester, String other);
    void createRandomGameRequest(String user, int size);
    void deleteRandomGameRequest(String user);
    void resolveRandomGameRequests();
    void startGame(String requester, String other);
    boolean playingGame(String requester, String other);
    boolean gameRequestExists(String requester, String other);
    boolean randomGameRequestExists(String user);
    boolean gameExists(String gameId);
    boolean userAuthorizedForGame(String user, String gameId);
    RequestInfo[] getIncomingGameRequests(String user);
    RequestInfo[] getOutgoingGameRequests(String user);
    int getOutgoingRandomRequestSize(String user);
    GameInfo getGameInfo(String gameId);
    GameNotifications getGameNotifications(String userId);
}
