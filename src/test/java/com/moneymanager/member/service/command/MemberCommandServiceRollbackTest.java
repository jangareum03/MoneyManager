package com.moneymanager.member.service.command;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

}