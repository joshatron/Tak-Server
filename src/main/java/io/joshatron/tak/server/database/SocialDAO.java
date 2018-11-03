package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.GameServerException;

public interface SocialDAO {

    boolean gameRequestExists(String requester, String other) throws GameServerException;
    void createGameRequest(String requester, String other) throws GameServerException;
    void deleteGameRequest(String requester, String other) throws GameServerException;
}
