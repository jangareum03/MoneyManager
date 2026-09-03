package com.moneymanager.support;

import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * <p>
 * 패키지이름    : com.moneymanager.support<br>
 * 파일이름       : IntegrationTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 14<br>
 * 설명              : 통합 테스트에 공통으로 필요한 기능을 제공하는 추상 클래스
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
 * 		 	  <td>26. 8. 14</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public abstract class IntegrationTest {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected LedgerRepository ledgerRepository;

	protected static Path tempDir = createTempDir();

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("file.image.ledger", () -> tempDir.toString());
	}

	@BeforeEach
	void prepareTestEnvironment() throws IOException {
		cleanTempDir();
	}

	protected Cookie accessTokenCookie(String username) {
		String token = jwtTokenProvider.createAccessToken(username);

		return new Cookie("accessToken", token);
	}


	//===== 보조 메서드 =====
	private static Path createTempDir() {
		try{
			return Files.createTempDirectory("ledger-test-");
		}catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void cleanTempDir() throws IOException {
		try (Stream<Path> paths = Files.walk(tempDir)) {
			paths.sorted(Comparator.reverseOrder())
					.filter(path -> !path.equals(tempDir))
					.forEach(path -> {
						try {
							Files.delete(path);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
		}
	}


	//==== 유틸 메서드 =====
	protected void insertMember(Member member) {
		jdbcTemplate.update(
				"""
						INSERT INTO member(id, type, username, password, name, birthdate, nickname, email)
							VALUES (?, ?, ?, ?, ?, ?, ?, ?)
						""",
				member.getId(),
				member.getType().getValue(),
				member.getUsername(),
				member.getPassword(),
				member.getName(),
				member.getBirthdate(),
				member.getNickname(),
				member.getEmail()
		);

		jdbcTemplate.update(
				"""
						INSERT INTO member_info(id, gender)
							VALUES (?, ?)
						""",
				member.getInfo().getId(),
				member.getInfo().getGender().getValue()
		);
	}

}