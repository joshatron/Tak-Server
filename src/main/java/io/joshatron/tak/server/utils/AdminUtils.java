package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.AdminDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;
import io.joshatron.tak.server.response.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminUtils {

    @Autowired
    private AdminDAO adminDAO;
    @Autowired
    private AccountDAO accountDAO;

    @Value("${admin.initial-password}")
    private String initialPassword;

    public String initializeAccount() throws GameServerException {
        if(adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_INITIALIZED);
        }

        String pass = initialPassword != null && !initialPassword.isEmpty() ? initialPassword : IdUtils.generateId();

        adminDAO.updatePassword(pass);

        return pass;
    }

    public void changePassword(Auth auth, Text passChange) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateText(passChange);
        Validator.validatePassword(passChange.getText());
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        adminDAO.updatePassword(passChange.getText());
    }

    public String resetUserPassword(Auth auth, String userToChange) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(userToChange);
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(userToChange)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }

        String newPass = IdUtils.generateId();
        accountDAO.updatePassword(accountDAO.getUserFromId(userToChange).getUsername(), newPass);

        return newPass;
    }

    public void banUser(Auth auth, String userToBan) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(userToBan);
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(accountDAO.getUserFromId(userToBan).getState() == State.BANNED) {
            throw new GameServerException(ErrorCode.ALREADY_BANNED);
        }

        accountDAO.updateState(userToBan, State.BANNED);
    }

    public void unbanUser(Auth auth, String userToSet) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(userToSet);
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(userToSet)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(accountDAO.getUserFromId(userToSet).getState() != State.BANNED) {
            throw new GameServerException(ErrorCode.USER_NOT_BANNED);
        }

        accountDAO.updateState(userToSet, State.NORMAL);
    }

    public void unlockUser(Auth auth, String userToSet) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(userToSet);
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        if(!accountDAO.userExists(userToSet)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(accountDAO.getUserFromId(userToSet).getState() != State.LOCKED) {
            throw new GameServerException(ErrorCode.USER_NOT_LOCKED);
        }

        accountDAO.updateState(userToSet, State.NORMAL);
    }
}
