package com.moneymanager.member.controller.view;

import com.moneymanager.global.log.operation.annotation.Operation;
import com.moneymanager.global.log.operation.enums.ServiceAction;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.service.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : MemberController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 16<br>
 * 설명              : 회원 관련 화면 요청을 처리하는 클래스
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
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/mypage")
    @Operation(ServiceAction.MY_PAGE)
    public String showMyPage(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        model.addAttribute("member", memberService.getMyPageInfo(currentUser.getId()));

        return "member/mypage_info";
    }

    @GetMapping("/mypage/withdrawal")
    @Operation(ServiceAction.MY_WITHDRAWAL)
    public String showWithdrawalPage(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        model.addAttribute("username",  memberService.getUsername(currentUser.getId()));

        return "member/mypage_delete";
    }

}