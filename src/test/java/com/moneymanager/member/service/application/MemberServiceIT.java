package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.EmailVerificationRedisRepository;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : MemberService 클래스 기능을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberServiceIT extends IntegrationTest {

    @Autowired
    MemberService target;

    @Autowired
    EmailVerificationRedisRepository redisRepository;


    @Nested
    @DisplayName("회원가입 진행할 때")
    class ProcessSignUp {

        MemberSignUpRequest request;

        @BeforeEach
        void setUp() {
            String email = MemberTestData.DEFAULT_EMAIL;
            String token = "test-verification-token";

            redisRepository.saveToken(email, token);

            request = MemberSignUpRequest.of(
                    MemberTestData.DEFAULT_USERNAME,
                    MemberTestData.DEFAULT_PASSWORD,
                    MemberTestData.DEFAULT_NAME,
                    MemberTestData.DEFAULT_BIRTHDATE,
                    MemberTestData.DEFAULT_NICKNAME,
                    email,
                    token,
                    MemberTestData.DEFAULT_GENDER.getValue()
            );
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("정상적인 회원 정보면 DB에 저장된다.")
            void createsMember_whenRequestIsValid() {
                //when
                target.processSignUp(request);

                //then
                Integer total = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM member",
                        Integer.class
                );

                assertThat(total).isEqualTo(1);
            }

            @Test
            @DisplayName("회원가입에 성공하면 토큰을 삭제한다.")
            void deletesToken_whenSignUpSucceeds() {
                //when
                target.processSignUp(request);

                //then
                assertThat(redisRepository.getToken(MemberTestData.DEFAULT_EMAIL))
                        .isEmpty();
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("요청 정보가 유효하지 않으면 회원가입에 실패한다.")
            void rejectsSignUp_whenRequestIsInvalid() {
                //given
                MemberSignUpRequest request = MemberSignUpRequest.of(
                        MemberTestData.DEFAULT_USERNAME,
                        MemberTestData.DEFAULT_PASSWORD,
                        "apple",
                        MemberTestData.DEFAULT_BIRTHDATE,
                        MemberTestData.DEFAULT_NICKNAME,
                        MemberTestData.DEFAULT_EMAIL,
                        "token",
                        MemberTestData.DEFAULT_GENDER.getValue()
                );

                //when
                assertThatThrownBy(() -> target.processSignUp(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                Integer total = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM member",
                        Integer.class
                );

                assertThat(total).isZero();
            }

            @Test
            @DisplayName("이메일 인증이 완료되지 않으면 회원가입에 실패한다.")
            void throwsException_whenEmailIsNotVerified() {
                //given
                MemberSignUpRequest request = MemberSignUpRequest.of(
                        MemberTestData.DEFAULT_USERNAME,
                        MemberTestData.DEFAULT_PASSWORD,
                        MemberTestData.DEFAULT_NAME,
                        MemberTestData.DEFAULT_BIRTHDATE,
                        MemberTestData.DEFAULT_NICKNAME,
                        MemberTestData.DEFAULT_EMAIL,
                        "not verified",
                        MemberTestData.DEFAULT_GENDER.getValue()
                );

                //when
                assertThatThrownBy(() -> target.processSignUp(request))
                        .isInstanceOf(ApplicationException.class);
            }

            @Test
            @DisplayName("기존 회원과 동일한 정보면 회원가입에 실패한다.")
            void rejectsSignUp_whenUserAlreadyExists() {
                //given
                Member member = MemberTestFixture.builder()
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();

                memberRepository.insert(member);

                MemberSignUpRequest request = MemberSignUpRequest.of(
                        MemberTestData.DEFAULT_USERNAME,
                        MemberTestData.DEFAULT_PASSWORD,
                        MemberTestData.DEFAULT_NAME,
                        MemberTestData.DEFAULT_BIRTHDATE,
                        MemberTestData.DEFAULT_NICKNAME,
                        MemberTestData.DEFAULT_EMAIL,
                        "token",
                        MemberTestData.DEFAULT_GENDER.getValue()
                );

                //when
                assertThatThrownBy(() -> target.processSignUp(request))
                        .isInstanceOf(ApplicationException.class);

                //then
                Integer total = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM member",
                        Integer.class
                );

                assertThat(total).isEqualTo(1);
            }

        }

    }

}