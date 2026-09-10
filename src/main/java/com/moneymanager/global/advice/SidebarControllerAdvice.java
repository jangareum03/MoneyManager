package com.moneymanager.global.advice;

import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.domain.dto.response.SideBarUser;
import com.moneymanager.member.service.application.SideBarMemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.advice<br>
 * 파일이름       : SidebarControllerAdvice<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 9<br>
 * 설명              : 사이드바에 필요한 정보를 처리하는 공통 클래스
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
 * 		 	  <td>26. 9. 9.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ControllerAdvice
public class SidebarControllerAdvice {

    private final SideBarMemberService sideBarMemberService;

    public SidebarControllerAdvice(SideBarMemberService sideBarMemberService) {
        this.sideBarMemberService = sideBarMemberService;
    }

    @ModelAttribute("sidebarUser")
    public SideBarUser currentUser(@AuthenticationPrincipal CustomUserDetails user) {
        if(user == null) {
            return null;
        }

        String memberNumber = user.getMemberNumber();

        return sideBarMemberService.get(memberNumber);
    }

}