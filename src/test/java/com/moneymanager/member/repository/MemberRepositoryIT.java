package com.moneymanager.member.repository;

import com.moneymanager.member.domain.dto.MemberAuth;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberRepositoryIT {

	@Autowired
	private MemberRepository target;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Member member;

	@BeforeEach
	void setUp() {
		member = MemberFixture.member(passwordEncoder).build();

		target.save(member);
	}

	@Nested
	@DisplayName("회원을 저장할 때")
	class SaveMemberTest {

		@BeforeEach
		void setUp() {
			jdbcTemplate.update(
					"DELETE FROM member WHERE id =? ",
					MemberTestData.MEMBER_ID
			);
		}

		@Nested
		@DisplayName("성공")
		class Success {

			@Test
			@DisplayName("사용자가 입력한 정보로 저장된다.")
			void savesUser_whenUserInfoIsGiven() {
				//when
				target.save(member);

				//then
				Map<String, Object> saved = jdbcTemplate.queryForMap(
						"""
									SELECT id, type, status, role, username, password, name, birthdate, nickname, email, created_at, deleted_at
									FROM member
									WHERE id = ?
								""",
						member.getId()
				);

				//then: 입력한 정보가 저장된다.
				assertThat(saved.get("id")).isEqualTo(member.getId());
				assertThat(saved.get("type")).isEqualTo(member.getType().getValue());
				assertThat(saved.get("username")).isEqualTo(member.getUsername());
				assertThat(saved.get("name")).isEqualTo(member.getName());
				assertThat(saved.get("birthdate")).isEqualTo(member.getBirthdate());
				assertThat(saved.get("nickname")).isEqualTo(member.getNickname());
				assertThat(saved.get("email")).isEqualTo(member.getEmail());
				assertThat(saved.get("password")).isEqualTo(member.getPassword());
			}

			@Test
			@DisplayName("상세정보도 사용자가 입력한 정보로 저장된다.")
			void savesUserDetail_whenUserDetailIsGiven() {
				//when
				target.save(member);

				//then
				Map<String, Object> saved = jdbcTemplate.queryForMap(
						"""
									SELECT id, gender, profile, point, consecutive_days, image_limit, login_at, failure_count
									FROM member_info
									WHERE id = ?
								""",
						member.getId()
				);

				MemberInfo memberInfo = member.getInfo();

				//then: 입력한 정보가 저장된다.
				assertThat(saved.get("id")).isEqualTo(memberInfo.getId());
				assertThat(saved.get("gender")).isEqualTo(memberInfo.getGender().getValue());
			}

			@Test
			@DisplayName("미입력 정보는 기본값으로 저장된다.")
			void savesDefaultValues_whenInputsAreOmitted() {
				//when
				target.save(member);

				//then: 회원정보 일부가 기본 정보로 저장된다.
				Map<String, Object> savedMember = jdbcTemplate.queryForMap(
						"""
									SELECT id, type, status, role, username, password, name, birthdate, nickname, email, created_at, deleted_at
									FROM member
									WHERE id = ?
								""",
						member.getId()
				);

				assertThat(savedMember.get("status")).isEqualTo(member.getStatus().getValue());
				assertThat(savedMember.get("role")).isEqualTo(member.getRole());
				assertThat(savedMember.get("created_at")).isNotNull();
				assertThat(savedMember.get("deleted_at")).isNull();

				//then: 회원 상세정보 일부가 기본 정보로 저장된다.
				Map<String, Object> savedMemberInfo = jdbcTemplate.queryForMap(
						"""
									SELECT id, gender, profile, point, consecutive_days, image_limit, login_at, failure_count
									FROM member_info
									WHERE id = ?
								""",
						member.getId()
				);

				MemberInfo memberInfo = member.getInfo();

				assertThat(savedMemberInfo.get("profile")).isEqualTo(memberInfo.getProfile());
				assertThat(savedMemberInfo.get("point")).isEqualTo(BigDecimal.ZERO);
				assertThat(savedMemberInfo.get("consecutive_days")).isEqualTo(BigDecimal.ZERO);
				assertThat(savedMemberInfo.get("image_limit")).isEqualTo(BigDecimal.ONE);
				assertThat(savedMemberInfo.get("failure_count")).isEqualTo(BigDecimal.ZERO);
				assertThat(savedMemberInfo.get("login_at")).isNotNull();
			}

		}

		@Nested
		@DisplayName("실패")
		class Failure {

			@Test
			@DisplayName("필수 정보가 없으면 저장에 실패한다.")
			void throwsDataIntegrityViolationException_whenRequiredFieldIsMissing() {
				//given
				Member member = MemberFixture.member()
						.username(null)
						.build();

				//when & then
				assertThatThrownBy(() -> target.save(member))
						;
			}

			@Test
			@DisplayName("기존에 있는 회원번호는 저장에 실패한다.")
			void throwsDataIntegrityViolationException_whenMemberNumberAlreadyExists() {
				//given: "UCt01001"를 가진 회원번호가 저장되어 있다.
				Member member = MemberFixture.member()
						.id(MemberTestData.MEMBER_ID)
						.build();

				target.save(member);

				//when & then: "UCt01001"를 가진 회원번호를 다시 저장한다.
				assertThatThrownBy(() -> target.save(member))
						;
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
					MemberTestData.MEMBER_ID
			);
		}

		@Nested
		@DisplayName("성공")
		class Success {
		
			@Test
			@DisplayName("회원이 존재하면 회원인증 정보를 반환한다.")
			void returnsUserAuthInfo_whenUserExists() {
				//when
				Optional<MemberAuth> result = target.findAuthByUsername(MemberTestData.USERNAME);
				
				//then
				assertThat(result.isPresent()).isTrue();

				MemberAuth memberAuth = result.get();
				assertThat(memberAuth.getUsername()).isEqualTo(MemberTestData.USERNAME);
				assertThat(memberAuth.getLoginFailCount()).isEqualTo(1);
				assertThat(memberAuth.getDeletedDate()).isNull();
			}

			@Test
			@DisplayName("탈퇴한 회원이면 탈퇴일이 포함되어 반환된다.")
			void returnsAuthInfoWithWithdrawnDate_whenUserIsWithdrawn() {
				//given: 저장된 회원에 탈퇴일을 변경한다.
				jdbcTemplate.update(
						"UPDATE member SET deleted_at = SYSDATE WHERE username = ?",
						MemberTestData.USERNAME
				);
				
				//when
				Optional<MemberAuth> result = target.findAuthByUsername(MemberTestData.USERNAME);
				
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
	class FindImageUploadCountTest {

		@BeforeEach
		void setUp() {
			jdbcTemplate.update(
					"UPDATE member_info SET image_limit = 2 WHERE id = ?",
					MemberTestData.MEMBER_ID
			);
		}

		@Nested
		@DisplayName("성공")
		class Success {
		
			@Test
			@DisplayName("회원이 존재하면 개수가 반환된다.")
			void returnsCount_whenUserExists() {
				//when
				Integer result = target.findImageUploadLimitByMemberId(MemberTestData.MEMBER_ID);
				
				//then
				assertThat(result).isEqualTo(2);
			}

			@Test
			@DisplayName("회원이 존재하지 않으면 예외가 발생한다.")
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

}