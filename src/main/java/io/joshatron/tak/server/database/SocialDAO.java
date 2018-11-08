package io.joshatron.tak.server.database;

import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.response.Message;
import io.joshatron.tak.server.response.SocialNotifications;
import io.joshatron.tak.server.response.User;

import java.util.Date;

public interface SocialDAO {

    boolean friendRequestExists(String requester, String other) throws GameServerException;
    boolean areFriends(String user1, String user2) throws GameServerException;
    boolean isBlocked(String requester, String other) throws GameServerException;
    void createGameRequest(String requester, String other) throws GameServerException;
    void deleteGameRequest(String requester, String other) throws GameServerException;
    void makeFriends(String user1, String user2) throws GameServerException;
    void unfriend(String requester, String other) throws GameServerException;
    void block(String requester, String other) throws GameServerException;
    void unblock(String requester, String other) throws GameServerException;
    void sendMessage(String requester, String other, String text) throws GameServerException;
    void markMessageRead(String id) throws GameServerException;
    void markAllRead(String user) throws GameServerException;
    User[] getIncomingFriendRequests(String user) throws GameServerException;
    User[] getOutgoingFriendRequests(String user) throws GameServerException;
    User[] getFriends(String user) throws GameServerException;
    User[] getBlocked(String user) throws GameServerException;
    Message[] listMessage(String username, String[] users, Date start, Date end, boolean read) throws GameServerException;
    Message getMessage(String messageId) throws GameServerException;
    SocialNotifications getSocialNotifications(String username) throws GameServerException;
}
