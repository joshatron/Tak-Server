package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.AdminDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;

public class AdminUtils {

    private AdminDAO adminDAO;
    private AccountDAO accountDAO;

    public AdminUtils(AdminDAO adminDAO, AccountDAO accountDAO) {
        this.adminDAO = adminDAO;
        this.accountDAO = accountDAO;
    }

    public String initializeAccount() throws GameServerException {
        if(adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_INITIALIZED);
        }

        return IdUtils.generateId(30);
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

    public String resetUserPassword(Auth auth, Text userToChange) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateText(userToChange);
        Validator.validateId(userToChange.getText(), AccountUtils.USER_ID_LENGTH);
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        String newPass = IdUtils.generateId(30);
        accountDAO.updatePassword(accountDAO.getUserFromId(userToChange.getText()).getUsername(), newPass);

        return newPass;
    }

    public void banUser(Auth auth, Text userToBan) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateText(userToBan);
        Validator.validateId(userToBan.getText(), AccountUtils.USER_ID_LENGTH);
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        adminDAO.banUser(userToBan.getText());
    }

    public void unbanUser(Auth auth, Text userToUnban) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateText(userToUnban);
        Validator.validateId(userToUnban.getText(), AccountUtils.USER_ID_LENGTH);
        if(!adminDAO.isInitialized()) {
            throw new GameServerException(ErrorCode.ADMIN_PASSWORD_NOT_INITIALIZED);
        }
        if(!adminDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }

        adminDAO.unbanUser(userToUnban.getText());
    }
}
