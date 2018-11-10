package io.joshatron.tak.server.database;

import io.joshatron.tak.server.request.GameRequest;

public interface GameDAO {
    void createGameRequest(String requester, String other, GameRequest gameRequest);
    boolean playingGame(String requester, String other);
    boolean gameRequestExists(String requester, String other);
}
