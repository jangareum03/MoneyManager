package com.moneymanager.member.controller;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.redis.service.EmailVerificationService;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : MemberApiControllerIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 16<br>
 * 설명              : MemberApiController 클래스 요청을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 9. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberApiControllerIT extends IntegrationTest {

    private static final String BASE_URL = "/api/members";

    Member member;

    @Autowired
    EmailVerificationService emailVerificationService;

    @BeforeEach
    void setUp() {
        member = MemberTestFixture.builder()
                .withMemberInfo(MemberInfoTestFixture.builder())
                .build();

        insertMember(member);
    }

    @Nested
    @DisplayName("이메일 수정 요청할 때")
    class UpdateEmail {

        @Test
        @DisplayName("이메일 수정을 요청하면 수정되고 성공 응답을 반환한다.")
        void updatesMemberEmail_whenValidInput() throws Exception {
            //given
            String email = "change@test.com";
            String token = "token";

            emailVerificationService.saveToken(email, token);

            //when
            mockMvc.perform(
                            put(BASE_URL + "/email")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email":"%s",
                                                "token":"%s"
                                            }
                                            """.formatted(email, token))
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.type").value("email"))
                    .andExpect(jsonPath("$.data.value").value(email));

            //then
            assertThat(
                    jdbcTemplate.queryForObject(
                            "SELECT email FROM member WHERE id = ?",
                            String.class,
                            member.getId()
                    )
            ).isEqualTo(email);
        }

        @Test
        @DisplayName("동일한 이메일 수정 요청하면 오류 응답을 반환한다.")
        void existsByEmail_returnsTrue_whenEmailAlreadyExists() throws Exception {
            //given
            String email = MemberTestData.DEFAULT_EMAIL;
            String token = "token";

            //when
            mockMvc.perform(
                            put(BASE_URL + "/email")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "email":"%s",
                                                "token":"%s"
                                            }
                                            """.formatted(email, token))
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("member.signup.failed"));

        }

    }


    @Nested
    @DisplayName("회원정보 수정 요청할 때")
    class UpdateMember {

        @Test
        @DisplayName("이름 수정을 요청하면 수정되고 성공 응답을 반환한다.")
        void updateMemberName_returnsSuccessResponse() throws Exception {
            //when
            mockMvc.perform(
                            patch("/api/members/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "name": "한성빈"
                                            }
                                            """)
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.type").value("name"))
                    .andExpect(jsonPath("$.value").value("한성빈"));

            //then
            Member saved = memberRepository.findById(member.getId());

            assertThat(saved.getName()).isEqualTo("한성빈");
        }

        @Test
        @DisplayName("성별 수정을 요청하면 수정되고 성공 응답을 반환한다.")
        void updateMemberGender_returnsSuccessResponse() throws Exception {
            //when
            mockMvc.perform(
                            patch("/api/members/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "gender": "m"
                                            }
                                            """)
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.type").value("gender"))
                    .andExpect(jsonPath("$.value").value("m"));

            //then
            Member saved = memberRepository.findById(member.getId());

            assertThat(saved.getInfo().getGender()).isSameAs(MemberGender.MALE);
        }

        @Test
        @DisplayName("비밀번호 수정을 요청하면 수정되고 성공 응답을 반환한다.")
        void updateMemberPassword_returnsSuccessResponse() throws Exception {
            //given
            String beforePwd = memberRepository.findById(member.getId()).getPassword();

            //when
            mockMvc.perform(
                            patch("/api/members/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "password": "newPass123!!"
                                            }
                                            """)
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.type").value("password"))
                    .andExpect(jsonPath("$.value").value("********"));

            //then
            Member saved = memberRepository.findById(member.getId());

            assertThat(saved.getPassword()).isNotEqualTo(beforePwd);
        }

        @Test
        @DisplayName("잘못된 요청이면 오류 응답을 반환한다.")
        void returnsBadRequest_whenMemberRequestIsInvalid() throws Exception {
            //when
            mockMvc.perform(
                            patch("/api/members/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "name": "가"
                                            }
                                            """)
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("member.name.invalid"));
        }

        @Test
        @DisplayName("POST로 요청하면 오류 응답을 반환한다.")
        void requestWithPostMethod_navigatesToError() throws Exception {
            //when
            mockMvc.perform(
                            post("/api/members/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("""
                                            {
                                                "name": "한성빈"
                                            }
                                            """)
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                    )
                    .andExpect(status().isMethodNotAllowed());

            //then
            Member saved = memberRepository.findById(member.getId());

            assertThat(saved.getName()).isNotEqualTo("한성빈");
        }
    }


    @Nested
    @DisplayName("프로필 수정 요청할 때")
    class UpdateProfile {

        Member member;

        @BeforeEach
        void setUp() {
            member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder().profile("test.png"))
                    .build();
        }
        
        @Test
        @DisplayName("프로필 이미지가 없으면 기본 이미지로 변경된다.")
        void updatesMemberProfileImageToDefault_whenImageIsNull() throws Exception {
        	//when
            mockMvc.perform(
                    put(BASE_URL + "/profile")
                            .cookie(accessTokenCookie(member.getMemberNumber()))
            )
                    .andDo(print())
                    .andExpect(jsonPath("$.type").value("profile"));
        }
        
        @Test
        @DisplayName("프로필 이미지로 변경된다.")
        void updatesMemberProfileImage_whenValidImageProvided() throws Exception {
        	//given
            MockMultipartFile multipartFile = ImageFixture.jpg("test");
        	
        	//when
            mockMvc.perform(
                    multipart(BASE_URL + "/profile")
                            .file(multipartFile)
                            .cookie(accessTokenCookie(member.getMemberNumber()))
                            .with(request -> {
                                request.setMethod("PUT");

                                return request;
                            })
            )
                    .andExpect(jsonPath("$.type").value("profile"));
        }
        
        @Test
        @DisplayName("잘못된 프로필 이미지면 변경에 실패한다.")
        void failsToUpdateMemberProfileImage_whenInvalidImageProvided() throws Exception {
        	//given
            MockMultipartFile multipartFile = ImageFixture.empty("test");

            //when
            mockMvc.perform(
                            multipart(BASE_URL + "/profile")
                                    .file(multipartFile)
                                    .cookie(accessTokenCookie(member.getMemberNumber()))
                                    .with(request -> {
                                        request.setMethod("PUT");

                                        return request;
                                    })
                    )
                    .andExpect(jsonPath("$.messageKey").value("member.profile.invalid"));
        }
    }

}