package com.moneymanager.support;

import com.moneymanager.global.security.jwt.JwtTokenProvider;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberFixture;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

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
public abstract class IntegrationTest {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected LedgerRepository ledgerRepository;

	@TempDir
	static Path tempDir;

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("file.root", () -> tempDir.toString());
	}

	protected Cookie accessTokenCookie(String username) {
		String token = jwtTokenProvider.createAccessToken(username);

		return new Cookie("accessToken", token);
	}

	protected Member saveMember() {
		Member member = MemberFixture.member(passwordEncoder).build();

		boolean exits = memberRepository.findById(member.getId()) != null;

		if (!exits) {
			memberRepository.save(member);
		}

		return member;
	}

	protected Member saveOtherMember() {
		Member member = MemberFixture.member(passwordEncoder)
				.id(MemberTestData.OTHER_MEMBER_ID)
				.username(MemberTestData.OTHER_USERNAME)
				.build();

		memberRepository.save(member);

		return member;
	}

	protected Path getTempDir() {
		return tempDir;
	}

	protected void deleteTempDir(Path dir) throws IOException {
		if(Files.exists(dir)) {
			try(Stream<Path> paths = Files.walk(dir)) {
				paths.sorted(Comparator.reverseOrder())
						.forEach(path -> {
							try{
								Files.deleteIfExists(path);
							}catch (IOException e) {
								throw new UncheckedIOException(e);
							}
						});
			}
		}
	}

}