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

    public String resetUserPassword(Auth auth, Text userToChange) {
        return null;
    }

    public void banUser(Auth auth, Text userToBan) {
    }

    public void unbanUser(Auth auth, Text userToUnban) {
    }
}
