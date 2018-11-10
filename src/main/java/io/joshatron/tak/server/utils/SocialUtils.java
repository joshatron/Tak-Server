package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.SocialDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.*;
import io.joshatron.tak.server.response.Message;
import io.joshatron.tak.server.response.SocialNotifications;
import io.joshatron.tak.server.response.User;

import java.util.Date;

public class SocialUtils {

    public static final int MESSAGE_ID_LENGTH = 20;

    private SocialDAO socialDAO;
    private AccountDAO accountDAO;

    public SocialUtils(SocialDAO socialDAO, AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
        this.socialDAO = socialDAO;
    }

    public void createFriendRequest(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(socialDAO.areFriends(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.ALREADY_FRIENDS);
        }

        socialDAO.createFriendRequest(user.getUserId(), other);
    }

    public void deleteFriendRequest(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!socialDAO.friendRequestExists(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        socialDAO.deleteFriendRequest(user.getUserId(), other);
    }

    public void respondToFriendRequest(Auth auth, String other, Text answer) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        Validator.validateText(answer);
        Answer response = Validator.validateAnswer(answer.getText());
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!socialDAO.friendRequestExists(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        if(response == Answer.ACCEPT) {
            socialDAO.makeFriends(other, user.getUserId());
        }
        socialDAO.deleteFriendRequest(other, user.getUserId());
    }

    public User[] listIncomingFriendRequests(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        return socialDAO.getIncomingFriendRequests(user.getUserId());
    }

    public User[] listOutgoingFriendRequests(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        return socialDAO.getOutgoingFriendRequests(user.getUserId());
    }

    public void unfriend(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!socialDAO.areFriends(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.ALREADY_FRIENDS);
        }

        socialDAO.unfriend(user.getUserId(), other);
    }

    public void blockUser(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(socialDAO.isBlocked(other, user.getUserId())) {
            throw new GameServerException(ErrorCode.ALREADY_BLOCKED);
        }

        socialDAO.block(user.getUserId(), other);
        if(socialDAO.areFriends(user.getUserId(), other)) {
            socialDAO.unfriend(user.getUserId(), other);
        }
    }

    public void unblockUser(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!socialDAO.isBlocked(other, user.getUserId())) {
            throw new GameServerException(ErrorCode.NOT_BLOCKED);
        }

        socialDAO.unblock(user.getUserId(), other);
    }

    public boolean isBlocked(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }

        return socialDAO.isBlocked(user.getUserId(), other);
    }

    public User[] listFriends(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        return socialDAO.getFriends(user.getUserId());
    }

    public User[] listBlocked(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        return socialDAO.getBlocked(user.getUserId());
    }

    public void sendMessage(Auth auth, String other, Text sendMessage) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, AccountUtils.USER_ID_LENGTH);
        Validator.validateText(sendMessage);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(socialDAO.isBlocked(other, user.getUserId())) {
            throw new GameServerException(ErrorCode.BLOCKED);
        }

        socialDAO.sendMessage(user.getUserId(), other, sendMessage.getText());
    }

    public Message[] listMessages(Auth auth, String senders, Date start, Date end, String read) throws GameServerException {
        Validator.validateAuth(auth);
        Read r = Validator.validateRead(read);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        String[] users = null;
        if(senders != null && senders.length() > 0) {
            users = senders.split(",");
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

        return socialDAO.listMessages(user.getUserId(), users, start, end, r);
    }

    public void markMessagesRead(Auth auth, MarkRead markRead) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateMarkRead(markRead);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        if(markRead.getIds() != null) {
            for(String id : markRead.getIds()) {
                Validator.validateId(id, MESSAGE_ID_LENGTH);
                if(socialDAO.getMessage(id) != null) {
                    socialDAO.markMessageRead(id);
                }
                else {
                    throw new GameServerException(ErrorCode.MESSAGE_NOT_FOUND);
                }
            }
        }

        if(markRead.getStart() != null) {
            Message[] messages = socialDAO.listMessages(user.getUserId(), null, markRead.getStart(), null, Read.NOT_READ);
            for(Message message : messages) {
                socialDAO.markMessageRead(message.getId());
            }
        }

        if(markRead.getIds() == null && markRead.getStart() == null) {
            socialDAO.markAllRead(user.getUserId());
        }
    }

    public SocialNotifications getNotifications(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        return socialDAO.getSocialNotifications(user.getUserId());
    }
}
