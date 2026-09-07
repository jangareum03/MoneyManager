package com.moneymanager.member.service.command;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.entity.MemberInfo;
import com.moneymanager.member.domain.enums.MemberStatus;
import com.moneymanager.member.service.generator.MemberNumberGenerator;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.command<br>
 * 파일이름       : MemberCommandServiceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 4.<br>
 * 설명              :
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
 * 		 	  <td>26. 9. 4.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class MemberCommandServiceIT extends IntegrationTest {

    @Autowired
    MemberCommandService target;

    @MockBean
    MemberNumberGenerator numberGenerator;

    @Nested
    @DisplayName("회원 저장할 때")
    class Save {

        Member member = MemberTestFixture.builder()
                .withMemberInfo(MemberInfoTestFixture.builder())
                .build();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("정상적으로 저장하면 Member 정보가 데이터베이스에 저장된다.")
            void savesMember_whenRequestIsValid() {
                //when
                target.save(member);

                //then
                Member saved = memberRepository.findById(member.getId());

                assertThat(saved.getId()).isEqualTo(member.getId());
                assertThat(saved.getUsername()).isEqualTo(member.getUsername());
                assertThat(saved.getPassword()).isEqualTo(member.getPassword());
                assertThat(saved.getName()).isEqualTo(member.getName());
                assertThat(saved.getBirthdate()).isEqualTo(member.getBirthdate());
                assertThat(saved.getEmail()).isEqualTo(member.getEmail());
                assertThat(saved.getType()).isEqualTo(member.getType());

                assertThat(saved.getStatus()).isEqualTo(MemberStatus.ACTIVE);
                assertThat(saved.getRole()).contains("USER");

                assertThat(saved.getCreatedAt()).isNotNull();
                assertThat(saved.getDeletedAt()).isNull();
            }

            @Test
            @DisplayName("정상적으로 저장하면 Member Info 정보가 데이터베이스에 저장된다.")
            void savesMemberInfo_whenRequestIsValid() {
                //when
                target.save(member);

                //then
                MemberInfo saved = memberRepository.findById(member.getId()).getInfo();

                //then
                assertThat(saved.getId()).isEqualTo(member.getId());
                assertThat(saved.getGender()).isEqualTo(member.getInfo().getGender());
                assertThat(saved.getProfile()).isEqualTo(member.getInfo().getProfile());

                assertThat(saved.getPoint()).isZero();
                assertThat(saved.getConsecutiveDays()).isZero();
                assertThat(saved.getFailureCount()).isZero();
                assertThat(saved.getImageLimit()).isEqualTo(1);

                assertThat(saved.getLoginAt()).isNull();
            }

            @Test
            @DisplayName("회원번호가 중복되면 새로운 회원번호로 재시도하여 저장한다.")
            void retriesIdGenerationAndSaves_whenDuplicateExists() {
                //given
                memberRepository.insert(member);

                Member newMember = MemberTestFixture.builder()
                        .number(numberGenerator.generate())
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();

                //when
                target.save(newMember);

                //then
                Integer totalCount = jdbcTemplate.queryForObject("select count(*) from member", Integer.class);
                assertThat(totalCount).isEqualTo(2);

                assertThat(newMember.getId()).isNotEqualTo(member.getId());
            }

            @Test
            @DisplayName("회원번호가 중복되면 새로운 회원번호로 재시도하여 저장한다.")
            void retriesMemberNumberGenerationAndSaves_whenDuplicateExists() {
                //given
                memberRepository.insert(member);

                Member newMember = MemberTestFixture.builder()
                        .id(UlidCreator.getUlid().toString())
                        .number(MemberTestData.DEFAULT_NUMBER)
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();

                //when
                target.save(newMember);

                //then
                Integer totalCount = jdbcTemplate.queryForObject("select count(*) from member", Integer.class);
                assertThat(totalCount).isEqualTo(2);

                assertThat(newMember.getMemberNumber()).isNotEqualTo(member.getMemberNumber());
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("회원번호가 계속 중복되면 최대 재시도 후 예외를 발생시킨다.")
            void throwsException_whenExceedsMaxRetries() {
                //given: 최초 번호 + 재시도 번호가 모두 DB에 저장되도록 준비
                String secondNumber = "NEW-02";
                String thirdNumber = "NEW-03";

                memberRepository.insert(member);

                Member secondMember = MemberTestFixture.builder()
                        .id(UlidCreator.getUlid().toString())
                        .number(secondNumber)
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();
                memberRepository.insert(secondMember);

                Member thirdMember = MemberTestFixture.builder()
                        .id(UlidCreator.getUlid().toString())
                        .number(thirdNumber)
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();
                memberRepository.insert(thirdMember);

                when(numberGenerator.generate())
                        .thenReturn(secondNumber, thirdNumber, "NEW-04");

                Member newMember = MemberTestFixture.builder()
                        .id(UlidCreator.getUlid().toString())
                        .number(member.getMemberNumber())
                        .withMemberInfo(MemberInfoTestFixture.builder())
                        .build();

                //when
                assertThatThrownBy(() -> target.save(newMember))
                        .isInstanceOf(ApplicationException.class);
            }

        }

    }

}