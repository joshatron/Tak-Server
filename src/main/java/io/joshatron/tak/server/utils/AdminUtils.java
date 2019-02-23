package io.joshatron.tak.server.utils;

import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.AdminDAO;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;

public class AdminUtils {

    private AdminDAO adminDAO;
    private AccountDAO accountDAO;

    public AdminUtils(AdminDAO adminDAO, AccountDAO accountDAO) {
        this.adminDAO = adminDAO;
        this.accountDAO = accountDAO;
    }

    public String initializeAccount() {
        return null;
    }

    public void changePassword(Auth auth, Text passChange) {
    }

    public String resetUserPassword(Auth auth, Text userToChange) {
        return null;
    }

    public void banUser(Auth auth, Text userToBan) {
    }

    public void unbanUser(Auth auth, Text userToUnban) {
    }
}
