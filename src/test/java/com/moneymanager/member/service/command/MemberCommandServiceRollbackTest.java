package com.moneymanager.member.service.command;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.domain.enums.MemberGender;
import com.moneymanager.member.repository.MemberHistoryRepository;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.command<br>
 * 파일이름       : MemberCommandServiceRollbackTest<br>
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
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberCommandServiceRollbackTest {
    
    @Autowired
    private MemberCommandService target;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    MemberHistoryRepository historyRepository;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update(
                "DELETE FROM member"
        );
    }

    @Nested
    @DisplayName("회원 저장할 때")
    class Save {
        
        @Test
        @DisplayName("Member info 저장에 실패하면 Member 정보를 롤백한다.")
        void rollbacksMember_whenMemberInfoInsertionFails() {
        	//given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder().id(null))
                    .build();
        	
        	//when
            assertThatThrownBy(() -> target.save(member));
        	
        	//then
        	assertThat(
                    jdbcTemplate.queryForObject("SELECT count(*) FROM member", Integer.class)
            )
                    .isZero();
        }
        
        @Test
        @DisplayName("Member info 저장에 실패하면 Member info 정보를 롤백한다.")
        void rollbacksMemberInfo_whenMemberInfoInsertionFails() {
            //given
            Member member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder().id(null))
                    .build();

            //when
            assertThatThrownBy(() -> target.save(member));

            //then
            assertThat(
                    jdbcTemplate.queryForObject("SELECT count(*) FROM member_info", Integer.class)
            )
                    .isZero();
        }

    }


    @Nested
    @DisplayName("회원 수정할 때")
    class Update {

        Member member;

        @BeforeEach
        void setUp() {
            member = MemberTestFixture.builder()
                    .withMemberInfo(MemberInfoTestFixture.builder())
                    .build();

            target.save(member);
        }

        @Test
        @DisplayName("내역 저장에 실패하면 비밀번호 정보를 롤백한다.")
        void rollback_whenSavingPasswordHistoryFails() {
        	//given
            String memberId = member.getId();
            String password = "newPass123!!";

            String beforePassword = member.getPassword();

            doThrow(new RuntimeException("내역 저장 실패"))
                    .when(historyRepository)
                            .insertHistory(any());
        	
        	//when
            assertThatThrownBy(() -> target.updatePassword(memberId, password));
        	
        	//then
        	assertThat(
                    jdbcTemplate.queryForObject("SELECT password FROM member WHERE id = ?", String.class, memberId)
            ).isEqualTo(beforePassword);
        }

        @DisplayName("내역 저장에 실패하면 이메일 정보를 롤백한다.")
        void rollback_whenSavingEmailHistoryFails() {
            //given
            String memberId = member.getId();
            String email = "change@test.com";

            String beforeEmail = member.getEmail();

            doThrow(new RuntimeException("내역 저장 실패"))
                    .when(historyRepository)
                    .insertHistory(any());

            //when
            assertThatThrownBy(() -> target.updateEmail(memberId, beforeEmail, email));

            //then
            assertThat(
                    jdbcTemplate.queryForObject("SELECT email FROM member WHERE id = ?", String.class, memberId)
            ).isEqualTo(beforeEmail);
        }

        @Test
        @DisplayName("내역 저장에 실패하면 이름 정보를 롤백한다.")
        void rollback_whenSavingNameHistoryFails() {
            //given
            String memberId = member.getId();
            String name = "수정이름";

            String beforeName = member.getName();

            doThrow(new RuntimeException("내역 저장 실패"))
                    .when(historyRepository)
                    .insertHistory(any());

            //when
            assertThatThrownBy(() -> target.updateName(memberId, beforeName, name));

            //then
            assertThat(
                    jdbcTemplate.queryForObject("SELECT name FROM member WHERE id = ?", String.class, memberId)
            ).isEqualTo(beforeName);
        }

        @Test
        @DisplayName("내역 저장에 실패하면 성별 정보를 롤백한다.")
        void rollback_whenSavingGenderHistoryFails() {
            //given
            String memberId = member.getId();
            MemberGender gender = MemberGender.FEMALE;

            MemberGender beforeGender = member.getInfo().getGender();

            doThrow(new RuntimeException("내역 저장 실패"))
                    .when(historyRepository)
                    .insertHistory(any());

            //when
            assertThatThrownBy(() -> target.updateGender(memberId, beforeGender, gender));

            //then
            assertThat(
                    jdbcTemplate.queryForObject("SELECT gender FROM member_info WHERE member_id = ?", String.class, memberId)
            ).isEqualTo(beforeGender.getValue());
        }

        @Test
        @DisplayName("내역 저장에 실패하면 프로필 정보를 롤백한다.")
        void rollback_whenSavingProfileHistoryFails() {
            //given
            String memberId = member.getId();
            String profile = "test.png";

            String beforeProfile = member.getInfo().getProfile();

            doThrow(new RuntimeException("내역 저장 실패"))
                    .when(historyRepository)
                    .insertHistory(any());

            //when
            assertThatThrownBy(() -> target.updateProfile(memberId, beforeProfile, profile));

            //then
            assertThat(
                    jdbcTemplate.queryForObject("SELECT profile FROM member_info WHERE member_id = ?", String.class, memberId)
            ).isEqualTo(beforeProfile);
        }

    }

}