package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;
import io.joshatron.tak.server.response.User;
import io.joshatron.tak.server.validation.AccountValidator;

public class AccountUtils {

    public static final int ID_LENGTH = 15;

    private AccountDAO accountDAO;

    public AccountUtils(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public boolean isAuthenticated(Auth auth) throws GameServerException {
        AccountValidator.validateAuth(auth);

        return accountDAO.isAuthenticated(auth);
    }

    public void registerUser(Auth auth) throws GameServerException {
        AccountValidator.validateAuth(auth);
        if(accountDAO.userExists(auth.getUsername())) {
            throw new GameServerException(ErrorCode.USERNAME_TAKEN);
        }

        accountDAO.addUser(auth, ID_LENGTH);
    }

    public void updatePassword(Auth auth, Text change) throws GameServerException {
        AccountValidator.validatePassChange(auth, change);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(auth.getPassword().equals(change.getText())) {
            throw new GameServerException(ErrorCode.SAME_PASSWORD);
        }

        accountDAO.updatePassword(auth.getUsername(), change.getText());
    }

    public void updateUsername(Auth auth, Text change) throws GameServerException {
        AccountValidator.validateUserChange(auth, change);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(auth.getUsername().equals(change.getText())) {
            throw new GameServerException(ErrorCode.SAME_USERNAME);
        }

        accountDAO.updateUsername(auth.getUsername(), change.getText());
    }

    public User getUserFromId(String id) throws GameServerException {
        AccountValidator.validateUserId(id);

        return accountDAO.getUserFromId(id);
    }

    public User getUserFromUsername(String username) throws GameServerException {
        AccountValidator.validateUsername(username);

        return accountDAO.getUserFromUsername(username);
    }
}
