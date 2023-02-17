package com.shoplist.api.shoplistapireciever.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    @Bean
    public NamedParameterJdbcTemplate shoplistJdbcTemplate(DataSource postgresqlDataSource) {
        return new NamedParameterJdbcTemplate(postgresqlDataSource);
    }
}
