package io.joshatron.tak.server.config;

import io.joshatron.tak.server.database.*;
import io.joshatron.tak.server.exceptions.GameServerException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class SqliteConfig {

    @Bean
    public Connection connection() throws GameServerException {
        return SqliteManager.getConnection();
    }
}
