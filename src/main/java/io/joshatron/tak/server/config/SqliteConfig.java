package io.joshatron.tak.server.config;

import io.joshatron.tak.server.database.*;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.utils.AccountUtils;
import io.joshatron.tak.server.utils.AdminUtils;
import io.joshatron.tak.server.utils.GameUtils;
import io.joshatron.tak.server.utils.SocialUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class SqliteConfig {

    @Bean
    public GameUtils gameUtils() throws GameServerException {
        return new GameUtils(gameDAOSqlite(), socialDAOSqlite(), accountDAOSqlite());
    }

    @Bean
    public SocialUtils socialUtils() throws GameServerException {
        return new SocialUtils(socialDAOSqlite(), accountDAOSqlite());
    }

    @Bean
    public AccountUtils accountUtils() throws GameServerException {
        return new AccountUtils(accountDAOSqlite(), adminDAOSqlite());
    }

    @Bean
    public AdminUtils adminUtils() throws GameServerException {
        return new AdminUtils(adminDAOSqlite(), accountDAOSqlite());
    }

    @Bean
    public GameDAOSqlite gameDAOSqlite() throws GameServerException {
        return new GameDAOSqlite(connection());
    }

    @Bean
    public SocialDAOSqlite socialDAOSqlite() throws GameServerException {
        return new SocialDAOSqlite(connection());
    }

    @Bean
    public AccountDAOSqlite accountDAOSqlite() throws GameServerException {
        return new AccountDAOSqlite(connection());
    }

    @Bean
    public AdminDAOSqlite adminDAOSqlite() throws GameServerException {
        return new AdminDAOSqlite(connection());
    }

    @Bean
    public Connection connection() throws GameServerException {
        return SqliteManager.getConnection();
    }
}
