package com.moneymanager.global.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.config<br>
 *  * 파일이름       : DatabaseConfig<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 10. 17<br>
 *  * 설명              : 데이터베이스 설정과 관련된 작업하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
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
