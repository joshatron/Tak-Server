package io.joshatron.tak.server.database;

import java.sql.Connection;

public class AdminDAOSqlite implements AdminDAO {

    private Connection conn;

    public AdminDAOSqlite(Connection conn) {
        this.conn = conn;
    }
}
