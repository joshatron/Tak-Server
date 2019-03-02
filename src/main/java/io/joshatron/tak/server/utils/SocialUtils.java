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
        Validator.validateId(other, IdUtils.USER_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(socialDAO.isBlocked(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.BLOCKED);
        }
        if(socialDAO.isBlocked(other, user.getUserId())) {
            throw new GameServerException(ErrorCode.BLOCKING);
        }
        if(socialDAO.friendRequestExists(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.ALREADY_REQUESTING);
        }
        if(socialDAO.areFriends(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.ALREADY_FRIENDS);
        }
        if(user.getUserId().equalsIgnoreCase(other)) {
            throw new GameServerException(ErrorCode.REQUESTING_SELF);
        }

        socialDAO.createFriendRequest(user.getUserId(), other);
    }

    public void deleteFriendRequest(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, IdUtils.USER_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
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
        Validator.validateId(other, IdUtils.USER_LENGTH);
        Validator.validateText(answer);
        Answer response = Validator.validateAnswer(answer.getText());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
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
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return socialDAO.getIncomingFriendRequests(user.getUserId());
    }

    public User[] listOutgoingFriendRequests(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return socialDAO.getOutgoingFriendRequests(user.getUserId());
    }

    public void unfriend(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, IdUtils.USER_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!socialDAO.areFriends(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.FRIEND_NOT_FOUND);
        }

        socialDAO.unfriend(user.getUserId(), other);
    }

    public void blockUser(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, IdUtils.USER_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(socialDAO.isBlocked(other, user.getUserId())) {
            throw new GameServerException(ErrorCode.ALREADY_BLOCKED);
        }
        if(user.getUserId().equalsIgnoreCase(other)) {
            throw new GameServerException(ErrorCode.BLOCKING_SELF);
        }

        socialDAO.block(user.getUserId(), other);
        if(socialDAO.areFriends(user.getUserId(), other)) {
            socialDAO.unfriend(user.getUserId(), other);
        }
        if(socialDAO.friendRequestExists(user.getUserId(), other)) {
            socialDAO.deleteFriendRequest(user.getUserId(), other);
        }
        if(socialDAO.friendRequestExists(other, user.getUserId())) {
            socialDAO.deleteFriendRequest(other, user.getUserId());
        }
    }

    public void unblockUser(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, IdUtils.USER_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
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
        Validator.validateId(other, IdUtils.USER_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }

        return socialDAO.isBlocked(user.getUserId(), other);
    }

    public User[] listFriends(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return socialDAO.getFriends(user.getUserId());
    }

    public User[] listBlocked(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return socialDAO.getBlocking(user.getUserId());
    }

    public void sendMessage(Auth auth, String other, Text sendMessage) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, IdUtils.USER_LENGTH);
        Validator.validateText(sendMessage);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(socialDAO.isBlocked(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.BLOCKED);
        }
        if(sendMessage.getText().length() == 0) {
            throw new GameServerException(ErrorCode.EMPTY_FIELD);
        }
        if(sendMessage.getText().length() > 5000) {
            throw new GameServerException(ErrorCode.MESSAGE_TOO_LONG);
        }

        socialDAO.sendMessage(user.getUserId(), other, sendMessage.getText());
    }

    public Message[] listMessages(Auth auth, String senders, Long startTime, Long endTime, String read, String from) throws GameServerException {
        Validator.validateAuth(auth);
        Date start = null;
        if(startTime != null) {
            start = new Date(startTime.longValue());
        }
        Date end = null;
        if(endTime != null) {
            end = new Date(endTime.longValue());
        }
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        Read rd = Validator.validateRead(read);
        String[] users = null;
        if(senders != null && senders.length() > 0) {
            users = senders.split(",");
            for (String u : users) {
                Validator.validateId(u, IdUtils.USER_LENGTH);
            }
        }
        if(start != null && end != null && start.after(end)) {
            throw new GameServerException(ErrorCode.INVALID_DATE);
        }
        From frm = Validator.validateFrom(from);

        return socialDAO.listMessages(user.getUserId(), users, start, end, rd, frm);
    }

    public void markMessagesRead(Auth auth, MarkRead markRead) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateMarkRead(markRead);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        boolean notFound = false;
        boolean forbidden = false;
        if(markRead.getIds() != null) {
            for(String id : markRead.getIds()) {
                try {
                    Message message = socialDAO.getMessage(id);
                    if(!message.getRecipient().equalsIgnoreCase(user.getUserId())) {
                        forbidden = true;
                    }
                    else {
                        socialDAO.markMessageRead(id);
                    }
                }
                catch (GameServerException e) {
                    if(e.getCode() == ErrorCode.MESSAGE_NOT_FOUND) {
                        notFound = true;
                    }
                    else {
                        throw e;
                    }
                }
            }
        }

        if(markRead.getSenders() != null) {
            for(String sender : markRead.getSenders()) {
                if(!accountDAO.userExists(sender)) {
                    notFound = true;
                }
                else {
                    socialDAO.markMessagesFromSenderRead(user.getUserId(), sender);
                }
            }
        }

        if(markRead.getIds() == null && markRead.getSenders() == null) {
            socialDAO.markAllRead(user.getUserId());
        }

        if(forbidden) {
            throw new GameServerException(ErrorCode.NOT_YOUR_MESSAGE);
        }
        if(notFound) {
            throw new GameServerException(ErrorCode.MESSAGE_NOT_FOUND);
        }
    }

    public SocialNotifications getNotifications(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return socialDAO.getSocialNotifications(user.getUserId());
    }
}
