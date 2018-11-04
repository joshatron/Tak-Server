package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;
import io.joshatron.tak.server.response.User;

public class AccountUtils {

    public static final int USER_ID_LENGTH = 15;

    private AccountDAO accountDAO;

    public AccountUtils(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public boolean isAuthenticated(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);

        return accountDAO.isAuthenticated(auth);
    }

    public void registerUser(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(accountDAO.userExists(auth.getUsername())) {
            throw new GameServerException(ErrorCode.USERNAME_TAKEN);
        }

        accountDAO.addUser(auth, USER_ID_LENGTH);
    }

    public void updatePassword(Auth auth, Text change) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateText(change);
        Validator.validatePassword(change.getText());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(auth.getPassword().equals(change.getText())) {
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

        accountDAO.updateUsername(auth.getUsername(), change.getText());
    }

    public User getUserFromId(String id) throws GameServerException {
        Validator.validateId(id, USER_ID_LENGTH);

        return accountDAO.getUserFromId(id);
    }

    public User getUserFromUsername(String username) throws GameServerException {
        Validator.validateUsername(username);

        return accountDAO.getUserFromUsername(username);
    }
}
