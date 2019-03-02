package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.AdminDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;
import io.joshatron.tak.server.response.User;

public class AccountUtils {

    private AccountDAO accountDAO;
    private AdminDAO adminDAO;

    public AccountUtils(AccountDAO accountDAO, AdminDAO adminDAO) {
        this.accountDAO = accountDAO;
        this.adminDAO = adminDAO;
    }

    public boolean isAuthenticated(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);

        String id = accountDAO.getUserFromUsername(auth.getUsername()).getUserId();
        return accountDAO.isAuthenticated(auth) && !adminDAO.isUserBanned(id);
    }

    public void registerUser(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(accountDAO.usernameExists(auth.getUsername())) {
            throw new GameServerException(ErrorCode.USERNAME_TAKEN);
        }

        accountDAO.addUser(auth, IdUtils.USER_LENGTH);
    }

    public void updatePassword(Auth auth, Text change) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateText(change);
        Validator.validatePassword(change.getText());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        Auth testAuth = new Auth(auth.getUsername(), change.getText());
        if(accountDAO.isAuthenticated(testAuth)) {
            throw new GameServerException(ErrorCode.SAME_PASSWORD);
        }

        accountDAO.updatePassword(auth.getUsername(), change.getText());
    }

    public void updateUsername(Auth auth, Text change) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateText(change);
        Validator.validateUsername(change.getText());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(auth.getUsername().equals(change.getText())) {
            throw new GameServerException(ErrorCode.SAME_USERNAME);
        }
        if(!auth.getUsername().equalsIgnoreCase(change.getText()) &&  accountDAO.usernameExists(change.getText())) {
            throw new GameServerException(ErrorCode.USERNAME_TAKEN);
        }

        accountDAO.updateUsername(auth.getUsername(), change.getText());
    }

    public User getUserFromId(String id) throws GameServerException {
        Validator.validateId(id, IdUtils.USER_LENGTH);

        return accountDAO.getUserFromId(id);
    }

    public User getUserFromUsername(String username) throws GameServerException {
        Validator.validateUsername(username);

        return accountDAO.getUserFromUsername(username);
    }
}
