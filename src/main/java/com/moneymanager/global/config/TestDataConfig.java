package com.moneymanager.global.config;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.config<br>
 * 파일이름       : TestDataConfig<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 16<br>
 * 설명              : 테스트 데이터 설정과 관련된 작업하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 8. 16.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Configuration
@Profile("test")
public class TestDataConfig {

	@Bean
	CommandLineRunner commandLineRunner(MemberRepository repository, PasswordEncoder passwordEncoder) {
		return args -> {
			String username = "test123";

			if(repository.findAuthByUsername(username).isPresent()) {
				return;
			}

			repository.save(Member.testMember(passwordEncoder));
		};
	}

}