package io.joshatron.tak.server.config;

import io.joshatron.tak.server.database.AccountDAOSqlite;
import io.joshatron.tak.server.database.DatabaseManager;
import io.joshatron.tak.server.database.GameDAOSqlite;
import io.joshatron.tak.server.database.SocialDAOSqlite;
import io.joshatron.tak.server.exceptions.ServerErrorException;
import io.joshatron.tak.server.utils.AccountUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class ApplicationConfig {

    @Bean
    public AccountUtils accountUtils() throws ServerErrorException {
        return new AccountUtils(accountDAOSqlite());
    }

    @Bean
    public GameDAOSqlite gameDAOSqlite() throws ServerErrorException {
        return new GameDAOSqlite(accountDAOSqlite(), socialDAOSqlite(), connection());
    }

    @Bean
    public SocialDAOSqlite socialDAOSqlite() throws ServerErrorException {
        return new SocialDAOSqlite(accountDAOSqlite(), connection());
    }

    @Bean
    public AccountDAOSqlite accountDAOSqlite() throws ServerErrorException {
        return new AccountDAOSqlite(connection());
    }

    @Bean
    public Connection connection() throws ServerErrorException {
        return DatabaseManager.getConnection();
    }
}
