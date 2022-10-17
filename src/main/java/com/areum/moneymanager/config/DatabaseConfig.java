package com.areum.moneymanager.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource( oracleHikariConfig() );
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.oracle")
    public HikariConfig oracleHikariConfig() {
        return new HikariConfig();
    }

}
