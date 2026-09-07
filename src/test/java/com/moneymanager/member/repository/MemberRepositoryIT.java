package com.moneymanager.member.repository;

import com.moneymanager.member.domain.dto.MemberAuth;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.repository<br>
 * 파일이름       : MemberRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : MemberRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberRepositoryIT extends IntegrationTest {

	@Autowired
	private MemberRepository target;

	@Nested
	@DisplayName("회원을 저장할 때")
	class Insert {

		Member member = MemberTestFixture.builder()
				.withMemberInfo(MemberInfoTestFixture.builder())
				.buildWithEncodePassword((passwordEncoder.encode("password123")));

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("회원 기본 정보를 저장한다.")
			void savesMember_whenMemberIsGiven() {
				//when
				target.insert(member);

				//then
				Map<String, Object> saved = jdbcTemplate.queryForMap(
						"""
									SELECT id, member_number, type, username, password, name, birthdate, nickname, email
									FROM member
									WHERE id = ?
								""",
						member.getId()
				);

				assertThat(saved.get("id")).isEqualTo(member.getId());
				assertThat(saved.get("member_number")).isEqualTo(member.getMemberNumber());
				assertThat(saved.get("type")).isEqualTo(member.getType().getValue());
				assertThat(saved.get("username")).isEqualTo(member.getUsername());
				assertThat(saved.get("password")).isEqualTo(member.getPassword());
				assertThat(saved.get("name")).isEqualTo(member.getName());
				assertThat(saved.get("birthdate")).isEqualTo(member.getBirthdate());
				assertThat(saved.get("nickname")).isEqualTo(member.getNickname());
				assertThat(saved.get("email")).isEqualTo(member.getEmail());
			}

			@Test
			@DisplayName("회원 상세 정보를 저장한다.")
			void savesMemberInfo_whenMemberIsGiven() {
				//given
				target.insert(member);

				//when
				target.insert(member.getInfo());

				//then
				Map<String, Object> saved = jdbcTemplate.queryForMap(
						"""
									SELECT member_id, gender
									FROM member_info
									WHERE member_id = ?
								""",
						member.getId()
				);

				assertThat(saved.get("member_id")).isEqualTo(member.getInfo().getId());
				assertThat(saved.get("gender")).isEqualTo(member.getInfo().getGender().getValue());
			}

			@Test
			@DisplayName("입력하지 않은 정보는 기본값으로 저장된다.")
			void insertsUserWithDefaultValues_whenOptionalFieldsAreNull() {
				//when
				target.insert(member);
				target.insert(member.getInfo());

				//then
				Map<String, Object> saved = jdbcTemplate.queryForMap(
						"""
									SELECT status, role, created_at, deleted_at, profile, point, consecutive_days, image_limit, login_at, failure_count
									FROM member m JOIN member_info mi
										ON m.id = mi.member_id
									WHERE m.id = ?
								""",
						member.getId()
				);

				assertThat(saved.get("status")).isEqualTo("A");
				assertThat(saved.get("role")).isEqualTo("ROLE_USER");
				assertThat(saved.get("point")).isEqualTo(BigDecimal.ZERO);
				assertThat(saved.get("consecutive_days")).isEqualTo(BigDecimal.ZERO);
				assertThat(saved.get("image_limit")).isEqualTo(BigDecimal.ONE);
				assertThat(saved.get("failure_count")).isEqualTo(BigDecimal.ZERO);

				assertThat(saved.get("profile")).isNull();
				assertThat(saved.get("login_at")).isNull();
				assertThat(saved.get("deleted_at")).isNull();

				assertThat(saved.get("created_at")).isNotNull();
			}

			@Test
			@DisplayName("회원 상세정보의 id가 일치한다.")
			void findsMemberInfo_whenIdMatches() {
				//when
				target.insert(member);
				target.insert(member.getInfo());

				//then
				Map<String, Object> saved = jdbcTemplate.queryForMap(
						"""
									SELECT m.id
										FROM member m JOIN member_info mi
											ON m.id = mi.member_id
										WHERE m.id = ?
								""",
						member.getId()
				);

				//then: 입력한 정보가 저장된다.
				assertThat(saved.get("id")).isEqualTo(member.getId());
				assertThat(saved.get("id")).isEqualTo(member.getInfo().getId());
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("필수 정보가 없으면 DataIntegrityViolationException 예외가 발생한다.")
			void throwsDataIntegrityViolationException_whenRequiredFieldIsMissing() {
				//given
				Member member = MemberTestFixture.builder()
						.username(null)
						.buildWithEncodePassword((passwordEncoder.encode("password123")));

				//when & then
				assertThatThrownBy(() -> target.insert(member))
						.isInstanceOf(DataIntegrityViolationException.class);
			}

			@Test
			@DisplayName("기존 회원번호를 가진 회원을 저장하면  DataIntegrityViolationException 예외가 발생한다.")
			void throwsDataIntegrityViolationException_whenMemberNumberAlreadyExists() {
				//given: "UCt01001"를 가진 회원번호가 저장되어 있다.
				Member member = MemberTestFixture.builder().buildWithEncodePassword((passwordEncoder.encode("password123")));
				target.insert(member);

				//when & then: "UCt01001"를 가진 회원번호를 다시 저장한다.
				assertThatThrownBy(() -> target.insert(member))
						.isInstanceOf(DataIntegrityViolationException.class);
			}

		}

	}


	@Nested
	@DisplayName("회원 인증 조회할 때")
	class FindAuthMemberTest {

		@BeforeEach
		void setUp() {
			jdbcTemplate.update(
					"UPDATE member_info SET failure_count = 1 WHERE id = ?",
					MemberTestData.DEFAULT_ID
			);
		}

		@Nested
		@DisplayName("성공")
		class Success {
		
			@Test
			@DisplayName("회원이 존재하면 회원인증 정보를 반환한다.")
			void returnsUserAuthInfo_whenUserExists() {
				//when
				Optional<MemberAuth> result = target.findAuthByUsername(MemberTestData.DEFAULT_USERNAME);
				
				//then
				assertThat(result.isPresent()).isTrue();

				MemberAuth memberAuth = result.get();
				assertThat(memberAuth.getUsername()).isEqualTo(MemberTestData.DEFAULT_USERNAME);
				assertThat(memberAuth.getLoginFailCount()).isEqualTo(1);
				assertThat(memberAuth.getDeletedDate()).isNull();
			}

			@Test
			@DisplayName("탈퇴한 회원이면 탈퇴일이 포함되어 반환된다.")
			void returnsAuthInfoWithWithdrawnDate_whenUserIsWithdrawn() {
				//given: 저장된 회원에 탈퇴일을 변경한다.
				jdbcTemplate.update(
						"UPDATE member SET deleted_at = SYSDATE WHERE username = ?",
						MemberTestData.DEFAULT_USERNAME
				);
				
				//when
				Optional<MemberAuth> result = target.findAuthByUsername(MemberTestData.DEFAULT_USERNAME);
				
				//then: 탈퇴일이 반환된다.
				MemberAuth memberAuth = result.get();

				assertThat(memberAuth.getDeletedDate()).isNotNull();
			}

			@Test
			@DisplayName("회원이 존재하지 않으면 empty를 반환한다.")
			void returnsEmpty_whenUserDoesNotExist() {
				//when
				Optional<MemberAuth> result = target.findAuthByUsername("nonexistent");

				//then
				assertThat(result.isPresent()).isFalse();
			}
			
		}

	}


	@Nested
	@DisplayName("업로드 가능 개수를 조회할 때")
	class FindImageUploadCount {

		@BeforeEach
		void setUp() {
			insertMember(MemberTestFixture.builder().buildWithEncodePassword((passwordEncoder.encode("password123"))));

			jdbcTemplate.update(
					"UPDATE member_info SET image_limit = 2 WHERE id = ?",
					MemberTestData.DEFAULT_ID
			);
		}

		@Nested
		@DisplayName("성공")
		class Success {
		
			@Test
			@DisplayName("회원이 존재하면 개수를 반환한다.")
			void returnsCount_whenUserExists() {
				//when
				Integer result = target.findImageUploadLimitByMemberId(MemberTestData.DEFAULT_ID);
				
				//then
				assertThat(result).isEqualTo(2);
			}

			@Test
			@DisplayName("회원이 존재하지 않으면 예외를 발생시킨다.")
			void throwsInternalException_whenUserDoesNotExist() {
				//when & then
				ApplicationExceptionAssert.assertThatApplicationException(
						catchThrowable(() -> target.findImageUploadLimitByMemberId("nonexistent"))
				)
						
						.hasErrorCode(DATA_NOT_FOUND)
						.hasWork("등록 가능한 이미지 개수 조회")
						.hasCauseMessage("존재하지 않은 회원")
						.hasTarget(MemberInfo.class)
						.hasValue("memberId", "nonexistent");
			}
			
		}

	}


	@Nested
	@DisplayName("아이디 존재여부를 조회할 때")
	class ExistsUsername {

		@BeforeEach
		void setUp() {
			insertMember(MemberTestFixture.builder().buildWithEncodePassword((passwordEncoder.encode("password123"))));
		}

		@Test
		@DisplayName("아이디가 있으면 true를 반환한다.")
		void returnsTrue_whenIdExists() {
			//given
			String username = MemberTestData.DEFAULT_USERNAME;
			
			//when
			boolean result = target.existsByUsername(username);

			//then
			assertThat(result).isTrue();
		}
		
		@Test
		@DisplayName("아이디가 없으면 false을 반환한다.")
		void returnsFalse_whenIdDoesNotExist() {
			//given
			String username = "nonexistent";

			//when
			boolean result = target.existsByUsername(username);

			//then
			assertThat(result).isFalse();
		}

	}


	@Nested
	@DisplayName("닉네임 존재여부를 조회할 때")
	class ExistsNickname {

		@BeforeEach
		void setUp() {
			insertMember(MemberTestFixture.builder().buildWithEncodePassword((passwordEncoder.encode("password123"))));
		}

		@Test
		@DisplayName("닉네임이 있으면 true를 반환한다.")
		void returnsTrue_whenNicknameExists() {
			//given
			String nickname = MemberTestData.DEFAULT_NICKNAME;

			//when
			boolean result = target.existsByNickname(nickname);

			//then
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("닉네임이 없으면 false을 반환한다.")
		void returnsFalse_whenNicknameDoesNotExist() {
			//given
			String nickname = "nonexistent";

			//when
			boolean result = target.existsByNickname(nickname);

			//then
			assertThat(result).isFalse();
		}

	}


	@Nested
	@DisplayName("이메일 존재여부를 조회할 때")
	class ExistsEmail {

		@BeforeEach
		void setUp() {
			insertMember(MemberTestFixture.builder().buildWithEncodePassword((passwordEncoder.encode("password123"))));
		}

		@Test
		@DisplayName("이메일이 있으면 true를 반환한다.")
		void returnsTrue_whenEmailExists() {
			//given
			String email = MemberTestData.DEFAULT_EMAIL;

			//when
			boolean result = target.existsByEmail(email);

			//then
			assertThat(result).isTrue();
		}

		@Test
		@DisplayName("이메일이 없으면 false을 반환한다.")
		void returnsFalse_whenEmailDoesNotExist() {
			//given
			String email = "nonexistent";

			//when
			boolean result = target.existsByEmail(email);

			//then
			assertThat(result).isFalse();
		}

	}

}