package com.moneymanager.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;



/**
 * <p>
 * 패키지이름    : com.areum.moneymanager.global.config<br>
 * 파일이름       : DatabaseConfig<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2022-10-17<br>
 * 역할             : 데이터베이스와 관련된 설정
 * </p>
 */
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
