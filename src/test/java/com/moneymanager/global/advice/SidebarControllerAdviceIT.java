package com.moneymanager.global.advice;

import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.SideBarRedisRepository;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.MemberInfoTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.advice<br>
 * 파일이름       : SidebarControllerAdviceIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : SidebarControllerAdvice 클래스 요청을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 9. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class SidebarControllerAdviceIT extends IntegrationTest {

    @Autowired
    SideBarRedisRepository redisRepository;
    
    @Test
    @DisplayName("인증된 사용자의 사이드바 정보를 Model에 추가한다.")
    void addsSidebarToModel_whenUserIsAuthenticated() throws Exception {
    	//given
        Member member = MemberTestFixture.builder()
                .withMemberInfo(MemberInfoTestFixture.builder().profile("profile"))
                .build();

        insertMember(member);
        redisRepository.saveNickname(member.getMemberNumber(), member.getNickname());
        redisRepository.saveProfile(member.getMemberNumber(), "profile");
        
    	//when
        mockMvc.perform(
                get("/ledgers/histories")
                        .cookie(accessTokenCookie(member.getMemberNumber()))
        )
                .andExpect(status().isOk())
                .andExpect(
                        model()
                                .attribute("sidebarUser", allOf(
                                        hasProperty("nickname", is(member.getNickname())),
                                        hasProperty("profile", is("profile"))
                                ))
                );
    }

}